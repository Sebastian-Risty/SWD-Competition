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
    //    private static final List lobbies = Collections.synchronizedList(new ArrayList<Game>());
    private static final Map<Game, List<ConnectedClient>> lobbies = Collections.synchronizedMap(new HashMap<>());

//    private static final ArrayList<ConnectedClient> clients = new ArrayList<>();
//    private static final ArrayList<Game> lobbies = new ArrayList<>();

    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public enum sendMessage {
        LOGIN_REQUEST,      // [1] -> username, [2] -> password
        MODE_SELECTION,     // [1] -> name of game from gameMode enum
        GUESS,              // [1] -> clients word guess
        LEADERBOARD,        // requests leaderboard update
        REGISTER_REQUEST,    // [1] -> username, [2] -> password

        CLIENT_DATA_REQUEST
    }

    public enum gameMode {
        ONE_VS_ONE,
        BATTLE_ROYAL
    }

    public static void main(String[] args) { // args[0] port, args[1] file name/path
        server = null;
        try {
            switch (args.length){
                case 1:
                    server = new ServerSocket(Integer.parseInt(args[0]));
                    break;
                case 2:
                    server = new ServerSocket(Integer.parseInt(args[0]));
                    scrambleFile = new File(String.format("./%s", args[1]));
                    if(!scrambleFile.exists()){
                        scrambleFile = new File(String.format("%s", args[1]));
                        if(!scrambleFile.exists()){
                            System.out.println("FILE CANNOT BE FOUND");
                            scrambleFile = null;
                        }
                    }
                    if(scrambleFile!=null){
                        System.out.println("FILE FOUND");
                    }
                    break;
                default:
                    server = new ServerSocket(23704);
            }
            server.setReuseAddress(true);

            executorService.execute(new AcceptPlayers());
            executorService.execute(new LobbyHandler());
            executorService.execute(new MatchHandler());

        } catch (IOException e) {
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

    private static class LobbyHandler implements Runnable {
        @Override
        public void run(){
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
                                        if(scrambleFile != null){
                                            temp = new OneVsOne(scrambleFile, fileIndex);
                                        } else{
                                            temp = new OneVsOne();
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

                                                for(ConnectedClient lobbyClient : lobbies.get(game)){
                                                    lobbyClient.output.format(String.format("%s,%s\n", Client.sendMessage.PLAYER_COUNT_UPDATE, game.getNumConnectedClients()));
                                                    lobbyClient.output.flush();
                                                }

                                                if (game.getNumConnectedClients() == 3){ // send lobby start time to the 3 connected clients
                                                    for(ConnectedClient lobbyClient : lobbies.get(game)){
                                                        lobbyClient.output.format(String.format("%s,%s\n", Client.sendMessage.TIMER_UPDATE, game.getLobbyStartTime()));
                                                        lobbyClient.output.flush();
                                                    }
                                                } else if(game.getNumConnectedClients() > 3){ // send the remaining time to any new clients
                                                    client.output.format(String.format("%s,%s\n", Client.sendMessage.TIMER_UPDATE, game.getLobbyStartTime()));
                                                    client.output.flush();
                                                }
                                                break;
                                            }
                                        }
                                    }
                                    if (client.currentLobby == null) { // create lobby if none were found
                                        System.out.println("Created New Battle Royale Lobby");
                                        Game temp;
                                        if(scrambleFile != null){
                                            temp = new BattleRoyale(scrambleFile, fileIndex);
                                        } else{
                                            temp = new BattleRoyale();
                                        }
                                        executorService.execute(temp);
                                        temp.setCountDownTime(30);
                                        temp.setMatchTime(60);
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

    private static class MatchHandler implements Runnable{
        @Override
        public void run(){
            System.out.println("FMH START");
            while (!server.isClosed()){
                synchronized (lobbies){
                    for (Game lobby : lobbies.keySet()) {
                        if(lobby.hasStarted()){ // START GAME
                            for(ConnectedClient client : lobbies.get(lobby)){
                                client.output.format(String.format("%s,%s\n", Client.sendMessage.GAME_START, lobby.getLetters().toString().replaceAll("[], \\[]", "")));
                                client.output.flush();
                            }
                            lobby.changeStartFlag();
                        }

                        if (lobby.isFinished()) {
                            List<ConnectedClient> sortedClients = lobbies.get(lobby);
                            Collections.sort(sortedClients);

                            StringBuilder sb = new StringBuilder();

                            sb.append(Client.sendMessage.GAME_END).append(',');

                            for(ConnectedClient client : sortedClients){
                                sb.append(client.username).append(',').append(client.currentScore).append(',');
                            }


                            sb.delete(sb.length() - 1, sb.length()).append("\n");

                            String temp = sb.toString();

                            for(ConnectedClient client : lobbies.get(lobby)){ // send match data
                                client.output.format(temp);
                                client.output.flush();

                                client.currentLobby = null;
                                System.out.println("Set client cur lobby to null");
                                client.totalGamesPlayed++;

                                switch(lobby.getGamemode()){
                                    case "OneVsOne":
                                        client.OVOGamesPlayed++;
                                        break;
                                    case "BattleRoyale":
                                        client.BRGamesPlayed++;
                                        break;
                                }
                            }
                            sortedClients.get(0).totalWins++;
                            switch(lobby.getGamemode()){
                                case "OneVsOne":
                                    sortedClients.get(0).OVOGamesPlayed++;
                                    break;
                                case "BattleRoyale":
                                    sortedClients.get(0).BRGamesPlayed++;
                                    break;
                            }

                            synchronized (lobbies){
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
        private String getStatString(){
            return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s", username, totalWins, totalGamesPlayed, OVOWins, OVOGamesPlayed, BRWins, BRGamesPlayed, tourneyWins, tourneyGamesPlayed);
        }

        public ConnectedClient(Socket socket) {
            this.clientSocket = socket;

            try{
                input = new Scanner(clientSocket.getInputStream());
                output = new Formatter(clientSocket.getOutputStream());
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                init(); // verify login and load client data from db
                while(!server.isClosed()){ // handle client messages
                    System.out.println("AWAITING CLIENT COMMAND");
                    String receivedData = input.nextLine();
                    System.out.printf("Message Received: %s\n", receivedData);
                    String[] clientMessage = receivedData.split(",");
                    switch (clientMessage[0]){
                        case "MODE_SELECTION":{
                            requestedGame = clientMessage[1];
                            break;
                        }
                        case "CLIENT_DATA_REQUEST" :{
                            output.format(String.format("%s,%s\n", Client.sendMessage.CLIENT_DATA, getStatString())); // send client data
                            output.flush();
                            break;
                        }
                        case "GUESS":{
                            if(currentLobby != null && currentLobby.isInProgress()){
                                int tempScore = currentLobby.guess(clientMessage[1]);
                                currentScore += tempScore;
                                output.format(String.format("%s,%s\n", Client.sendMessage.GUESS_RESULT, tempScore));
                                output.flush();
                            }
                            break;
                        }
                    } // TODO: on window close send command to server saying it close and then flip flag inside this run to then break form loop d then hit finally block so account is removed or smthn
                }
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    System.out.println("Sending Client Data to DB");
                    sendAccountData();
                    synchronized (clients){
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
            Database.initialize("login");
            while(this.username == null){
                if(input.hasNext()){
                    String receivedData = input.nextLine();
                    System.out.printf("Message Received: %s\n", receivedData);
                    String[] clientMessage = receivedData.split(",");
                    switch(clientMessage[0]){
                        case "LOGIN_REQUEST":
                            if(Database.validLogin(clientMessage[1],clientMessage[2])){
                                System.out.println("LOGIN GOOD");
                                Database.setTable("accounts");
                                acceptAccountData(Database.getInfo(clientMessage[1]));
                                output.format(String.format("%s\n", Client.sendMessage.LOGIN_VALID));
                                output.flush();
                                clients.add(this);
                                System.out.printf("Added Client %s to client list\n", this.username);
                            } else{
                                System.out.println("LOGIN FAILED");
                                output.format(String.format("%s\n", Client.sendMessage.LOGIN_INVALID));
                                output.flush();
                            }
                            break;
                        case "REGISTER_REQUEST":
                            if(Database.addAccount(clientMessage[1],clientMessage[2])){
                                System.out.println("SUCCESSFULLY REGISTERED");
                                this.username = clientMessage[1];
                                output.format(String.format("%s\n", Client.sendMessage.SIGNUP_VALID));
                                output.flush();
                                output.format(String.format("%s,%s\n", Client.sendMessage.CLIENT_DATA, getStatString())); // send client data
                                output.flush();
                                clients.add(this);
                                System.out.printf("Client %s successfully registered and logged in!\n", this.username);
                            } else{
                                System.out.println("FAILED TO REGISTERED");
                                output.format(String.format("%s\n", Client.sendMessage.SIGNUP_INVALID));
                                output.flush();
                            }
                            break;
                    }
                }
            }
        }
        private void acceptAccountData(String[] data){
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
            Database.setTable("accounts");
            Database.update(getStatString().split(","));
        }
        @Override
        public int compareTo(ConnectedClient connectedClient) {
            return this.currentScore - connectedClient.currentScore;
        }
    }
}

// TODO
// text GUI
// game display timer
// handle file to be read
// make sure BR works
// tourney
// save client data when their window closes
// prevent account logging in  more than once