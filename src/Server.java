import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Server handles client connections, matchmaking, match handling, and backing up data to database.
 */
class Server {
    /**
     * Network socket the server will use
     */
    private static ServerSocket server;
    /**
     * Used to read letter combinations from a given file
     */
    private static File scrambleFile = null;
    /**
     * Creates threads for subclasses of Server and for each ConnectedClient object
     *
     * @see AcceptPlayers
     * @see LobbyHandler
     * @see MatchHandler
     * @see TournamentHandler
     * @see ConnectedClient
     */
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * Synchronized list used to store clients connected to the server
     */
    private static final List<ConnectedClient> clients = Collections.synchronizedList(new ArrayList<>());
    /**
     * Synchronized map containing Game objects as keys with a list of clients in said game as the values
     */
    private static final Map<Game, List<ConnectedClient>> lobbies = Collections.synchronizedMap(new HashMap<>());
    /**
     * Synchronized map containing Tournament objects as keys with a list of TournamentStats as the values
     */
    private static final Map<Tournament, List<TournamentStats>> tournaments = Collections.synchronizedMap(new HashMap<>());
    /**
     * \
     * Incremented each time a scramble is used from scramble file
     * Game objects will begin at start of file if index exceeds line length in scramble file
     */
    private static Integer fileIndex = 0;


    /**
     * Contains commands the server can receive from clients
     */
    public enum sendMessage {
        LOGIN_REQUEST,
        MODE_SELECTION,
        GUESS,
        LEADERBOARD,
        REGISTER_REQUEST,

        CLIENT_DATA_REQUEST,
        CREATE_TOURNAMENT,
        JOIN_TOURNAMENT,
        TOURNAMENT_DATA,
        CANCEL_MM,
        LOGOUT_REQUEST,
        CLIENT_DISCONNECT
    }

    /**
     * Contains an easy to see way to select which game mode a client is requesting to be placed into
     */
    public enum gameMode {
        ONE_VS_ONE,
        BATTLE_ROYAL,
        TOURNAMENT
    }

    /**
     * Initializes scramble file and server port if possible. Create threads for subclasses.
     *
     * @param args args[0] is the port to run the server on, args[1] is file directory path, including the file name.
     *             If the file is at the root of the project directory, just the file name can be used.
     * @see ExecutorService
     */
    public static void main(String[] args) {
        Database.initialize("Accounts");
        Database.initialize("mastertournament");
        server = null;
        try {
            switch (args.length) {
                case 1:
                    server = new ServerSocket(Integer.parseInt(args[0]));
                    break;
                case 2:
                    server = new ServerSocket(Integer.parseInt(args[0]));
                    scrambleFile = new File(String.format("./%s", args[1]));
                    if (!scrambleFile.exists()) {
                        scrambleFile = new File(String.format("%s", args[1]));
                        if (!scrambleFile.exists()) {
                            System.out.println("FILE CANNOT BE FOUND");
                            scrambleFile = null;
                        }
                    }
                    if (scrambleFile != null) {
                        System.out.println("FILE FOUND");
                    }
                    break;
                default:
                    server = new ServerSocket(23704);
            }
            server.setReuseAddress(true);

            initializeTournaments();

            executorService.execute(new AcceptPlayers());
            executorService.execute(new TournamentHandler());
            executorService.execute(new LobbyHandler());
            executorService.execute(new MatchHandler());

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new thread for each Client connecting to the server
     *
     * @see Client
     */
    private static class AcceptPlayers implements Runnable {
        @Override
        public void run() {
            try {
                while (!server.isClosed()) {
                    new Thread(new ConnectedClient(server.accept())).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static void initializeTournaments() throws SQLException {
        Database.setTable("mastertournament");
        String[] tournamentData = Database.getInfo("");
        if (tournamentData.length > 1) {
            for (int i = 0; i < tournamentData.length; i += 2) {
                Tournament tournament = new Tournament(tournamentData[i], tournamentData[i + 1]);
                String[] userData = Database.getUserData(tournament.getName());
                tournaments.put(tournament, Collections.synchronizedList(new ArrayList<>()));
                for (int j = 0; j < userData.length; j += 3) {
                    tournaments.get(tournament).add(new TournamentStats(userData[j], userData[j + 1], userData[j + 2]));
                }
            }
        }
    }

    private static class TournamentHandler implements Runnable {
        @Override
        public void run() {
            while (!server.isClosed()) {
                synchronized (tournaments) {
                    for (Tournament tournament : tournaments.keySet()) {
                        if (tournament.checkEndFlag()) {
                            List<TournamentStats> sortedClients = tournaments.get(tournament);
                            Collections.sort(sortedClients);

                            for (TournamentStats client : tournaments.get(tournament)) {
                                String username = client.getUsername();
                                Database.setTable("Accounts");
                                String[] userInfo;
                                try {
                                    userInfo = Database.getInfo(username);
                                    userInfo[8] = String.valueOf(Integer.parseInt(userInfo[8]) + 1);
                                    Database.update(userInfo);
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            String[] winnerInfo;
                            if (sortedClients.get(0).getTournamentWins() > sortedClients.get(1).getTournamentWins()) {
                                try {
                                    winnerInfo = Database.getInfo(sortedClients.get(0).getUsername());
                                    winnerInfo[8] = String.valueOf(Integer.parseInt(winnerInfo[7]) + 1);
                                    Database.update(winnerInfo);
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            synchronized (tournaments) {
                                tournaments.remove(tournament);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Handles the creation of lobbies and the placement of players into open lobbies
     */
    private static class LobbyHandler implements Runnable {
        @Override
        public void run() {
            while (!server.isClosed()) {
                // loop through client list
                synchronized (clients) {
                    for (ConnectedClient client : clients) {
                        // HANDLE LOBBY REQUESTS
                        if (client.requestedGame != null) {
                            switch (client.requestedGame) {
                                case "ONE_VS_ONE": {
                                    synchronized (lobbies) {
                                        for (Game game : lobbies.keySet()) {
                                            if (game.getGamemode().equals("OneVsOne") && !game.isInProgress()) { // client joins open game if possible
                                                // add client
                                                lobbies.get(game).add(client);
                                                client.currentLobby = game;
                                                game.clientConnected();
                                                break;
                                            }
                                        }
                                    }
                                    if (client.currentLobby == null) { // create lobby if none were found
                                        Game temp;
                                        int MATCH_TIME = 30; // how long each match should be in seconds
                                        if (scrambleFile != null) {
                                            temp = new OneVsOne(MATCH_TIME, scrambleFile, fileIndex);
                                        } else {
                                            temp = new OneVsOne(MATCH_TIME);
                                        }
                                        executorService.execute(temp);
                                        lobbies.put(temp, Collections.synchronizedList(new ArrayList<ConnectedClient>() {{
                                            add(client);
                                        }}));
                                        client.currentLobby = temp;
                                        temp.clientConnected();
                                        fileIndex++;
                                    }
                                    break;
                                }
                                case "BATTLE_ROYAL": {
                                    synchronized (lobbies) {
                                        for (Game game : lobbies.keySet()) {
                                            if (game.getGamemode().equals("BattleRoyale") && !game.isInProgress()) { // client joins open game if possible
                                                // add client
                                                lobbies.get(game).add(client);
                                                client.currentLobby = game;
                                                game.clientConnected();

                                                for (ConnectedClient lobbyClient : lobbies.get(game)) {
                                                    lobbyClient.output.format(String.format("%s,%s\n", Client.sendMessage.PLAYER_COUNT_UPDATE, game.getNumConnectedClients()));
                                                    lobbyClient.output.flush();
                                                }

                                                if (game.getNumConnectedClients() == 3) { // send lobby start time to the 3 connected clients
                                                    try {
                                                        Thread.sleep(100);
                                                    } catch (InterruptedException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                    for (ConnectedClient lobbyClient : lobbies.get(game)) {
                                                        lobbyClient.output.format(String.format("%s,%s,%s\n", Client.sendMessage.TIMER_UPDATE, game.getLobbyStartTime(), game.getCountDownTime()));
                                                        lobbyClient.output.flush();
                                                    }
                                                } else if (game.getNumConnectedClients() > 3) { // send the remaining time to any new clients
                                                    client.output.format(String.format("%s,%s,%s\n", Client.sendMessage.TIMER_UPDATE, game.getLobbyStartTime(), game.getCountDownTime()));
                                                    client.output.flush();
                                                }
                                                break;
                                            }
                                        }
                                    }
                                    if (client.currentLobby == null) { // create lobby if none were found
                                        Game temp;
                                        int MATCH_TIME = 30; // how long each match should be in seconds
                                        int COUNTDOWN_TIME = 20; // how long the
                                        if (scrambleFile != null) {
                                            temp = new BattleRoyale(MATCH_TIME, COUNTDOWN_TIME, scrambleFile, fileIndex);
                                        } else {
                                            temp = new BattleRoyale(MATCH_TIME, COUNTDOWN_TIME);
                                        }
                                        executorService.execute(temp);
                                        lobbies.put(temp, Collections.synchronizedList(new ArrayList<ConnectedClient>() {{
                                            add(client);
                                        }}));
                                        client.currentLobby = temp;
                                        temp.clientConnected();
                                        client.output.format("%s,%s\n", Client.sendMessage.PLAYER_COUNT_UPDATE, temp.getNumConnectedClients());
                                        fileIndex++;
                                    }
                                    break;
                                }
                                case "TOURNAMENT": {
                                    synchronized (lobbies) {
                                        for (Game game : lobbies.keySet()) {
                                            if (game.getGamemode().equals("Tournament") && !game.isInProgress()) { // client joins open game if possible
                                                if (lobbies.get(game).get(0).currentTournament == client.currentTournament) {
                                                    lobbies.get(game).add(client);
                                                    client.currentLobby = game;
                                                    game.clientConnected();
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    if (client.currentLobby == null) { // create lobby if none were found
                                        Game temp;
                                        int MATCH_TIME = 30;
                                        if (scrambleFile != null) {
                                            temp = new TournamentGame(MATCH_TIME, scrambleFile, fileIndex, client.currentTournament.getName());
                                        } else {
                                            temp = new TournamentGame(MATCH_TIME, client.currentTournament.getName());
                                        }
                                        executorService.execute(temp);
                                        lobbies.put(temp, Collections.synchronizedList(new ArrayList<ConnectedClient>() {{
                                            add(client);
                                        }}));
                                        client.currentLobby = temp;
                                        temp.clientConnected();
                                        fileIndex++;
                                    }
                                    break;
                                }
                            }
                            client.requestedGame = null; // set to null once client finds lobby
                        }
                    }
                }
            }
        }
    }

    /**
     * Handles game state and communicates it with clients within said game
     */
    private static class MatchHandler implements Runnable {
        @Override
        public void run() {
            while (!server.isClosed()) {
                synchronized (lobbies) {
                    for (Game lobby : lobbies.keySet()) {
                        if (lobby.getNumConnectedClients() == 0) {
                            lobby.setPreGameLobbyFlag(false);
                            lobbies.remove(lobby);
                        } else {
                            if (lobby.hasStarted()) { // START GAME
                                for (ConnectedClient client : lobbies.get(lobby)) {
                                    client.output.format(String.format("%s,%s\n", Client.sendMessage.GAME_START, lobby.getLetters().toString().replaceAll("[], \\[]", "")));
                                    client.output.flush();
                                    client.output.format(String.format("%s,%s,%s\n", Client.sendMessage.TIMER_UPDATE, System.currentTimeMillis(), lobby.getMatchTime()));
                                    client.output.flush();
                                }
                                lobby.changeStartFlag();
                            }
                            if (lobby.isFinished()) { // END GAME
                                List<ConnectedClient> sortedClients = lobbies.get(lobby);
                                Collections.sort(sortedClients);

                                StringBuilder sb = new StringBuilder();

                                sb.append(Client.sendMessage.GAME_END).append(',');

                                for (ConnectedClient client : sortedClients) {
                                    sb.append(client.username).append(',').append(client.currentScore).append(',');
                                }


                                sb.delete(sb.length() - 1, sb.length()).append("\n");

                                String temp = sb.toString();

                                if (sortedClients.get(0).currentScore > sortedClients.get(1).currentScore) {
                                    sortedClients.get(0).totalWins++;
                                    switch (lobby.getGamemode()) {
                                        case "OneVsOne":
                                            sortedClients.get(0).OVOWins++;
                                            break;
                                        case "BattleRoyale":
                                            sortedClients.get(0).BRWins++;
                                            break;
                                        case "Tournament":
                                            List<TournamentStats> statsList = tournaments.get(sortedClients.get(0).currentTournament);
                                            for (TournamentStats stats : statsList) {
                                                if (sortedClients.get(0).username.equals(stats.getUsername())) {
                                                    stats.setTournamentWins(stats.getTournamentWins() + 1);
                                                }
                                            }
                                            break;
                                    }
                                }

                                synchronized (lobbies) {
                                    for (ConnectedClient client : lobbies.get(lobby)) { // send match data
                                        System.out.println(client.username);
                                        client.output.format(temp);
                                        client.output.flush();

                                        client.currentLobby = null;
                                        client.currentScore = 0;
                                        client.currentTournament = null;
                                        client.totalGamesPlayed++;

                                        switch (lobby.getGamemode()) {
                                            case "OneVsOne":
                                                client.OVOGamesPlayed++;
                                                break;
                                            case "BattleRoyale":
                                                client.BRGamesPlayed++;
                                                break;
                                            case "Tournament":
                                                synchronized (tournaments) {
                                                    Database.initialize(client.currentTournament.getName());
                                                    Database.setTable(client.currentTournament.getName());
                                                    for (TournamentStats stats : tournaments.get(client.currentTournament)) {
                                                        if (stats.getUsername().equals(client.username)) {
                                                            stats.setTournamentGamesLeft(stats.getTournamentGamesLeft() - 1);
                                                            try {
                                                                System.out.println(stats.getTournamentGamesLeft());

                                                                Database.update(
                                                                        new String[]{stats.getUsername(),
                                                                                String.valueOf(stats.getTournamentWins()),
                                                                                String.valueOf(stats.getTournamentGamesLeft())});
                                                            } catch (SQLException e) {
                                                                throw new RuntimeException(e);
                                                            }
                                                        }
                                                    }
                                                }
                                                break;
                                        }
                                    }
                                }

                                synchronized (lobbies) {
                                    lobbies.remove(lobby);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Server representation of a connected client, contains temporary stats before uploading to database.
     * Handles input and output streams for communication between server and client.
     */
    private static class ConnectedClient implements Runnable, Comparable<ConnectedClient> {
        /**
         * Client socket
         */
        private final Socket clientSocket;
        /**
         * Clients username used to log in
         */
        private String username = null;
        /**
         * The game mode the client is requesting to be placed into
         */
        private String requestedGame = null;
        /**
         * The reference to the game lobby that the client was placed into
         */
        private Game currentLobby = null;
        /**
         * The tournament that the client is currently in
         */
        private Tournament currentTournament = null;
        /**
         * The clients current score in the game they are in, used to determine the winner of the match
         */
        private int currentScore = 0;
        /**
         * The total number of wins in all game modes
         */
        private int totalWins = 0;
        /**
         * The total number of games played in all game modes
         */
        private int totalGamesPlayed = 0;
        /**
         * The total number of wins from head on head matches
         */
        private int OVOWins = 0;
        /**
         * The total number of head on head matches played
         */
        private int OVOGamesPlayed = 0;
        /**
         * The total number of battle royale matches won
         */
        private int BRWins = 0;
        /**
         * The total number of battle royale matches played
         */
        private int BRGamesPlayed = 0;
        /**
         * The number of tournaments won
         */
        private int tourneyWins = 0;
        /**
         * The number of tournament matches played
         */
        private int tourneyGamesPlayed = 0;
        /**
         * Used to store stats about tournaments before being uploaded to DB
         */
        private TournamentStats tournamentStats;
        /**
         * Used to send message commands to the Clients
         *
         * @see Client
         */

        private Formatter output;
        /**
         * Used to accept message commands from Clients
         *
         * @see Client
         */
        private Scanner input;

        /**
         * Creates input and output streams to Client
         *
         * @param socket The clients socket
         * @see Client
         */
        public ConnectedClient(Socket socket) {
            this.clientSocket = socket;

            try {
                input = new Scanner(clientSocket.getInputStream());
                output = new Formatter(clientSocket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * @return comma delimited string of client stats
         */
        private String getStatString() {
            return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s", username, totalWins, totalGamesPlayed, OVOWins, OVOGamesPlayed, BRWins, BRGamesPlayed, tourneyWins, tourneyGamesPlayed);
        }


        private String getTournamentData() {
            StringBuilder sb = new StringBuilder();

            for (Tournament tournament : tournaments.keySet()) {
                sb.append(tournament.getName()).append(",");
            }
            if (sb.length() > 0) {
                sb.delete(sb.length() - 1, sb.length());
            }
            return sb.toString();
        }

        private String getTournamentPlayerData(String name) throws SQLException {
            String[] data = Database.getUserData(name);
            StringBuilder sb = new StringBuilder();

            for (String s : data) {
                sb.append(s).append(",");
            }
            if (sb.length() > 0) {
                sb.delete(sb.length() - 1, sb.length());
            }
            return sb.toString();
        }

        /**
         * Handles message commands sent from client
         */
        @Override
        public void run() {
            try {
                init(); // verify login and load client data from db
                while (!Thread.currentThread().isInterrupted()) { // handle client messages
                    String receivedData = input.nextLine();
                    String[] clientMessage = receivedData.split(",");
                    try {
                        switch (clientMessage[0]) {
                            case "MODE_SELECTION":
                                requestedGame = clientMessage[1];
                                break;

                            case "CLIENT_DATA_REQUEST":
                                output.format(String.format("%s,%s\n", Client.sendMessage.CLIENT_DATA, getStatString())); // send client data
                                output.flush();
                                break;

                            case "TOURNAMENT_DATA":
                                output.format(String.format("%s,%s\n", Client.sendMessage.TOURNAMENT_DATA, getTournamentData())); // send client data
                                output.flush();
                                break;
                            case "GUESS":
                                if (currentLobby != null && currentLobby.isInProgress()) {
                                    int tempScore = currentLobby.guess(clientMessage[1]);
                                    currentScore += tempScore;
                                    output.format(String.format("%s,%s\n", Client.sendMessage.GUESS_RESULT, tempScore));
                                    output.flush();
                                }
                                break;
                            case "CREATE_TOURNAMENT":
                                synchronized (tournaments) {
                                    ArrayList<String> names = new ArrayList<>();
                                    for (Tournament tournament : tournaments.keySet()) {
                                        names.add(tournament.getName());
                                    }
                                    if (names.contains(clientMessage[1])) {
                                        output.format(String.format("%s,%s\n", Client.sendMessage.CREATE_TOURNAMENT, false));
                                        output.flush();
                                    } else {
                                        long startTime = System.currentTimeMillis();
                                        tournamentStats = new TournamentStats(username);
                                        tournaments.put(currentTournament = new Tournament(clientMessage[1], String.valueOf(startTime)), Collections.synchronizedList(
                                                new ArrayList<TournamentStats>() {{
                                                    add(tournamentStats);
                                                }}));
                                        Database.createTournament(clientMessage[1], String.valueOf(startTime));
                                        Thread.sleep(10);
                                        Database.addToTournament(username, clientMessage[1]);
                                        Thread.sleep(10);
                                        output.format(String.format("%s,%s,%s\n", Client.sendMessage.CREATE_TOURNAMENT, true, getTournamentPlayerData(clientMessage[1])));
                                        output.flush();
                                    }
                                }
                                break;
                            case "JOIN_TOURNAMENT":
                                synchronized (tournaments) {
                                    ArrayList<String> names = new ArrayList<>();
                                    for (Tournament tournament : tournaments.keySet()) {
                                        names.add(tournament.getName());
                                    }
                                    if (names.contains(clientMessage[1])) {
                                        boolean tempFlag = false;
                                        for (Tournament tournament : tournaments.keySet()) {
                                            if (tournament.getName().equals(clientMessage[1])) {
                                                for (TournamentStats stats : tournaments.get(tournament)) {
                                                    if (stats.getUsername().equals(username)) {
                                                        tempFlag = true;
                                                        break;
                                                    }
                                                }
                                                if (!tempFlag) {
                                                    tournamentStats = new TournamentStats(username);
                                                    tournaments.get(tournament).add(tournamentStats);
                                                }
                                            }
                                        }

                                        Database.addToTournament(username, clientMessage[1]);
                                        Thread.sleep(10);
                                        output.format(String.format("%s,%s,%s\n", Client.sendMessage.TOURNAMENT_PLAYER_DATA, true, getTournamentPlayerData(clientMessage[1])));
                                        output.flush();
                                        for (Tournament tournament : tournaments.keySet()) {
                                            if (tournament.getName().equals(clientMessage[1])) {
                                                currentTournament = tournament;
                                            }
                                        }
                                    } else {
                                        output.format(String.format("%s,%s\n", Client.sendMessage.TOURNAMENT_PLAYER_DATA, false));
                                        output.flush();
                                    }
                                }
                                break;
                            case "LOGOUT_REQUEST":
                                Database.setTable("Accounts");
                                Database.update(getStatString().split(","));

                                clients.remove(this);
                                this.username = null;
                                init();
                                break;
                            case "CLIENT_DISCONNECT":
                                Database.setTable("Accounts");
                                Database.update(getStatString().split(","));

                                clients.remove(this);
                                if (this.currentLobby != null) {
                                    synchronized (lobbies) {
                                        lobbies.get(this.currentLobby).remove(this);
                                        this.currentLobby.clientDisconnected();
                                        this.currentLobby = null;
                                    }
                                }
                                this.username = null;
                                this.requestedGame = null;
                                Thread.currentThread().interrupt();
                                break;
                            case "CANCEL_MM":
                                synchronized (lobbies) {
                                    this.requestedGame = null;
                                    for (ConnectedClient client : lobbies.get(this.currentLobby)) {
                                        if (client != this) {
                                            client.output.format(String.format("%s,%s\n", Client.sendMessage.PLAYER_COUNT_UPDATE, (this.currentLobby.getNumConnectedClients() - 1)));
                                            client.output.flush();
                                        }
                                    }
                                    this.currentLobby.clientDisconnected();
                                    this.currentLobby = null;
                                }
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    sendAccountData();
                    synchronized (clients) {
                        clients.remove(this);

                    }
                    if (output != null) {
                        output.close();
                    }
                    if (input != null) {
                        input.close();
                        clientSocket.close();
                    }
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * Waits until a log in request is received before loading client data and allowing access to main menu
         *
         * @throws IOException  If there are thread interrupts.
         * @throws SQLException If there are database errors.
         */
        private void init() throws IOException, SQLException {
            Database.initialize("Login");
            while (this.username == null) {
                if (input.hasNext()) {
                    String receivedData = input.nextLine();
                    String[] clientMessage = receivedData.split(",");
                    switch (clientMessage[0]) {
                        case "LOGIN_REQUEST":
                            boolean good = true;
                            synchronized (clients) {
                                for (ConnectedClient client : clients) {
                                    if (client.username.equals(clientMessage[1])) {
                                        output.format(String.format("%s\n", Client.sendMessage.LOGIN_INVALID));
                                        output.flush();
                                        good = false;
                                    }
                                }
                            }
                            if (Database.validLogin(clientMessage[1], clientMessage[2]) && good) {
                                Database.setTable("Accounts");
                                acceptAccountData(Database.getInfo(clientMessage[1]));
                                output.format(String.format("%s\n", Client.sendMessage.LOGIN_VALID));
                                output.flush();
                                clients.add(this);
                            } else {
                                output.format(String.format("%s\n", Client.sendMessage.LOGIN_INVALID));
                                output.flush();
                            }
                            break;
                        case "REGISTER_REQUEST":
                            if (Database.addAccount(clientMessage[1], clientMessage[2])) {
                                this.username = clientMessage[1];
                                output.format(String.format("%s\n", Client.sendMessage.SIGNUP_VALID));
                                output.flush();
                                output.format(String.format("%s,%s\n", Client.sendMessage.CLIENT_DATA, getStatString())); // send client data
                                output.flush();
                                clients.add(this);
                            } else {
                                output.format(String.format("%s\n", Client.sendMessage.SIGNUP_INVALID));
                                output.flush();
                            }
                            break;
                    }
                }
            }
        }

        /**
         * Helper function that receives data from database and loads it into appropriate fields
         *
         * @param data [1] -> userName, total wins, T GamePlayed, OVO wins, OVO GP, BR wins, BR GP, T wins, T GP
         */
        private void acceptAccountData(String[] data) {
            username = data[0];
            totalWins = Integer.parseInt(data[1]);
            totalGamesPlayed = Integer.parseInt(data[2]);
            OVOWins = Integer.parseInt(data[3]);
            OVOGamesPlayed = Integer.parseInt(data[4]);
            BRWins = Integer.parseInt(data[5]);
            BRGamesPlayed = Integer.parseInt(data[6]);
            tourneyWins = Integer.parseInt(data[7]);
            tourneyGamesPlayed = Integer.parseInt(data[8]);
        }

        /**
         * Sends account data to database to be stored. Utilizes getStatString() to get data.
         *
         * @throws SQLException          If there is a database error
         * @throws FileNotFoundException If the connection to the database fails
         */
        private void sendAccountData() throws SQLException, FileNotFoundException {
            Database.setTable("Accounts");
            Database.update(getStatString().split(","));
        }

        /**
         * Used to order the results of game to later be sent to client to display
         *
         * @param connectedClient The other client to compare to
         * @return The client with the greater current score
         */
        @Override
        public int compareTo(ConnectedClient connectedClient) {
            return connectedClient.currentScore - this.currentScore;
        }
    }
}