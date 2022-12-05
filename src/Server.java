import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


class Server {
    private static ServerSocket server;
    private static File scrambleFile = null;
    private static Integer fileIndex = 0;

    private static final List<ConnectedClient> clients = Collections.synchronizedList(new ArrayList<>());
    private static final Map<Game, List<ConnectedClient>> lobbies = Collections.synchronizedMap(new HashMap<>());
    private static final Map<Tournament, List<TournamentStats>> tournaments = Collections.synchronizedMap(new HashMap<>());


    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public enum sendMessage {
        LOGIN_REQUEST,      // [1] -> username, [2] -> password
        MODE_SELECTION,     // [1] -> name of game from gameMode enum
        GUESS,              // [1] -> clients word guess
        LEADERBOARD,        // requests leaderboard update
        REGISTER_REQUEST,    // [1] -> username, [2] -> password

        CLIENT_DATA_REQUEST,
        CREATE_TOURNAMENT,
        JOIN_TOURNAMENT,
        TOURNAMENT_DATA,
        CANCEL_MM,
        LOGOUT_REQUEST,
        CLIENT_DISCONNECT
    }

    public enum gameMode {
        ONE_VS_ONE,
        BATTLE_ROYAL
    }

    public static void main(String[] args) { // args[0] port, args[1] file name/path
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

    private static class AcceptPlayers implements Runnable {
        @Override
        public void run() {
            System.out.println("AP START");
            try {
                while (!server.isClosed()) {
                    new Thread(new ConnectedClient(server.accept())).start();
                    System.out.println("Client Connection Accepted!");
                }
            } catch (IOException e) {
                System.out.println("ERROR");
            }
        }
    }

    private static void initializeTournaments() throws SQLException {
        Database.setTable("mastertournament");
        String[] tournamentData = Database.getInfo("");
        for (int i = 0; i < tournamentData.length; i += 2) {
            Tournament tournament = new Tournament(tournamentData[i], tournamentData[i + 1]);
            String[] userData = Database.getUserData(tournament.getName());
            tournaments.put(tournament, Collections.synchronizedList(new ArrayList<>()));
            for (int j = 0; j < userData.length; j += 3) {
                tournaments.get(tournament).add(new TournamentStats(userData[j], userData[j + 1], userData[j + 2]));
            }
        }
    }

    private static class TournamentHandler implements Runnable {
        @Override
        public void run() {
            System.out.println("TH START");
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
                                System.out.println("Removed match from current lobbies");
                            }
                        }
                    }
                }
            }
        }
    }

    private static class LobbyHandler implements Runnable {
        @Override
        public void run() {
            System.out.println("LH START");
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
                                                System.out.println("ADDED CLIENT TO GAME");
                                                client.currentLobby = game;
                                                game.clientConnected();
                                                break;
                                            }
                                        }
                                    }
                                    if (client.currentLobby == null) { // create lobby if none were found
                                        System.out.println("Created New ONE_VS_ONE Lobby");
                                        Game temp;
                                        int MATCH_TIME = 30;
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
                                                System.out.println("ADDED CLIENT TO Existing Battle Royale Lobby");
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
                                        System.out.println("Created New Battle Royale Lobby");
                                        Game temp;
                                        int MATCH_TIME = 30;
                                        int COUNTDOWN_TIME = 60;
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
                            }
                            client.requestedGame = null; // set to null once client finds lobby
                        }
                    }
                }
            }
        }
    }

    private static class MatchHandler implements Runnable {
        @Override
        public void run() {
            System.out.println("FMH START");
            while (!server.isClosed()) {
                synchronized (lobbies) {
                    for (Game lobby : lobbies.keySet()) {
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

                            for (ConnectedClient client : lobbies.get(lobby)) { // send match data
                                client.output.format(temp);
                                client.output.flush();

                                client.currentLobby = null;
                                client.currentScore = 0;
                                System.out.println("Set client cur lobby to null");
                                client.totalGamesPlayed++;

                                switch (lobby.getGamemode()) {
                                    case "OneVsOne":
                                        client.OVOGamesPlayed++;
                                        break;
                                    case "BattleRoyale":
                                        client.BRGamesPlayed++;
                                        break;
                                }
                            }
                            sortedClients.get(0).totalWins++;
                            switch (lobby.getGamemode()) {
                                case "OneVsOne":
                                    sortedClients.get(0).OVOGamesPlayed++;
                                    break;
                                case "BattleRoyale":
                                    sortedClients.get(0).BRGamesPlayed++;
                                    break;
                            }

                            synchronized (lobbies) {
                                lobbies.remove(lobby);
                                System.out.println("Removed match from current lobbies");
                            }
                        }
                    }
                }
            }
        }
    }

    private static class ConnectedClient implements Runnable, Comparable<ConnectedClient> {
        private final Socket clientSocket;
        private String username = null;
        private String requestedGame = null;
        private Game currentLobby = null;
        private int currentScore = 0;
        private int totalWins = 0;
        private int totalGamesPlayed = 0;
        private int OVOWins = 0;
        private int OVOGamesPlayed = 0;
        private int BRWins = 0;
        private int BRGamesPlayed = 0;
        private int tourneyWins = 0;
        private int tourneyGamesPlayed = 0;

        private Formatter output;
        private Scanner input;

        public ConnectedClient(Socket socket) {
            this.clientSocket = socket;

            try {
                input = new Scanner(clientSocket.getInputStream());
                output = new Formatter(clientSocket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String getStatString() {
            return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s", username, totalWins, totalGamesPlayed, OVOWins, OVOGamesPlayed, BRWins, BRGamesPlayed, tourneyWins, tourneyGamesPlayed);
        }

        private String getTournamentData() {
            StringBuilder sb = new StringBuilder();

            for (Tournament tournament : tournaments.keySet()) {
                sb.append(tournament.getName()).append(",");
            }
            sb.delete(sb.length() - 1, sb.length());
            return sb.toString();
        }

        private String getTournamentPlayerData(String name) throws SQLException {
            String[] data = Database.getUserData(name);
            StringBuilder sb = new StringBuilder();

            for (String s : data) {
                sb.append(s).append(",");
            }
            sb.delete(sb.length() - 1, sb.length());
            return sb.toString();
        }

        @Override
        public void run() {
            try {
                init(); // verify login and load client data from db
                while (!server.isClosed()) { // handle client messages
                    System.out.println("AWAITING CLIENT COMMAND");
                    String receivedData = input.nextLine();
                    System.out.printf("Message Received: %s\n", receivedData);
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
                                        output.format(String.format("%s,%s\n", Client.sendMessage.CREATE_TOURNAMENT, true));
                                        output.flush();
                                        tournaments.put(new Tournament(clientMessage[1], String.valueOf(System.currentTimeMillis())), Collections.synchronizedList(
                                                new ArrayList<TournamentStats>() {{
                                                    add(new TournamentStats(username));
                                                }}));
                                        // TODO UPDATE DATABASE
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
                                        output.format(String.format("%s,%s,%s\n", Client.sendMessage.TOURNAMENT_PLAYER_DATA, true, getTournamentPlayerData(clientMessage[1])));
                                        output.flush();
                                    } else {
                                        output.format(String.format("%s,%s,%s\n", Client.sendMessage.TOURNAMENT_PLAYER_DATA, false, ""));
                                        output.flush();
                                    }
                                }
                                break;
                            case "LOGOUT_REQUEST":
                                Database.setTable("Accounts");
                                Database.update(getStatString().split(","));

                                System.out.println("CLIENT LOG OUT");
                                clients.remove(this);
                                if (this.currentLobby != null) {
                                    lobbies.get(this.currentLobby).remove(this); // TODO: may not need this here
                                    this.currentLobby.clientDisconnected();
                                    this.currentLobby = null;
                                }
                                this.username = null;
                                this.requestedGame = null;
                                init();
                                break;
                            case "CLIENT_DISCONNECT":
                                Database.setTable("Accounts");
                                Database.update(getStatString().split(","));

                                System.out.println("CLIENT DISCONNECT");
                                clients.remove(this);
                                if (this.currentLobby != null) {
                                    lobbies.get(this.currentLobby).remove(this); // TODO: may need to sync
                                    this.currentLobby.clientDisconnected();
                                    this.currentLobby = null;
                                }
                                this.username = null;
                                this.requestedGame = null;

                                output.format("%s", Client.sendMessage.SHUTDOWN);
                                output.flush();
                                break;
                            case "CANCEL_MM":
                                System.out.println("CLIENT CANCELED MM");
                                this.requestedGame = null;
                                if (this.currentLobby != null) {
                                    lobbies.get(this.currentLobby).remove(this); // TODO: may need to put in sync blocks
                                    this.currentLobby.clientDisconnected();
                                    this.currentLobby = null;
                                }
                                break;
                        }
                    } // TODO: on window close send command to server saying it close and then flip flag inside this run to then break form loop d then hit finally block so account is removed or smthn
                    catch (Exception e) {
                        System.out.println("BAD INPUT RECEIVED");
                    }
                }
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    System.out.println("Sending Client Data to DB");
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

        private void init() throws IOException, SQLException {
            System.out.println("START INIT");
            Database.initialize("Login");
            while (this.username == null) {
                if (input.hasNext()) {
                    String receivedData = input.nextLine();
                    System.out.printf("Message Received: %s\n", receivedData);
                    String[] clientMessage = receivedData.split(",");
                    switch (clientMessage[0]) {
                        case "LOGIN_REQUEST":
                            boolean good = true;
                            synchronized (clients) {
                                for (ConnectedClient client : clients) {
                                    if (client.username.equals(clientMessage[1])) {
                                        System.out.println("Client Already Logged In, LOGIN FAILED");
                                        output.format(String.format("%s\n", Client.sendMessage.LOGIN_INVALID));
                                        output.flush();
                                        good = false;
                                    }
                                }
                            }
                            if (Database.validLogin(clientMessage[1], clientMessage[2]) && good) {
                                System.out.println("LOGIN GOOD");
                                Database.setTable("Accounts");
                                acceptAccountData(Database.getInfo(clientMessage[1]));
                                output.format(String.format("%s\n", Client.sendMessage.LOGIN_VALID));
                                output.flush();
                                clients.add(this);
                                System.out.printf("Added Client %s to client list\n", this.username);
                            } else {
                                System.out.println("LOGIN FAILED");
                                output.format(String.format("%s\n", Client.sendMessage.LOGIN_INVALID));
                                output.flush();
                            }
                            break;
                        case "REGISTER_REQUEST":
                            if (Database.addAccount(clientMessage[1], clientMessage[2])) {
                                System.out.println("SUCCESSFULLY REGISTERED");
                                this.username = clientMessage[1];
                                output.format(String.format("%s\n", Client.sendMessage.SIGNUP_VALID));
                                output.flush();
                                output.format(String.format("%s,%s\n", Client.sendMessage.CLIENT_DATA, getStatString())); // send client data
                                output.flush();
                                clients.add(this);
                                System.out.printf("Client %s successfully registered and logged in!\n", this.username);
                            } else {
                                System.out.println("FAILED TO REGISTERED");
                                output.format(String.format("%s\n", Client.sendMessage.SIGNUP_INVALID));
                                output.flush();
                            }
                            break;
                    }
                }
            }
        }

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

        // [1] -> userName, total wins, T GamePlayed, OVO wins, OVO GP, BR wins, BR GP, T wins, T GP
        private void sendAccountData() throws SQLException, FileNotFoundException {
            Database.setTable("Accounts");
            Database.update(getStatString().split(","));
        }

        @Override
        public int compareTo(ConnectedClient connectedClient) {
            return connectedClient.currentScore - this.currentScore;
        }
    }
}

// TODO
// text GUI
// game display timer
// handle file to be read
// tourney
// save client data when their window closes