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

    private static final List clients = Collections.synchronizedList(new ArrayList<ConnectedClient>());
    private static final List lobbies = Collections.synchronizedList(new ArrayList<Game>());

//    private static final ArrayList<ConnectedClient> clients = new ArrayList<>();
//    private static final ArrayList<Game> lobbies = new ArrayList<>();

    private static ExecutorService executorService = Executors.newCachedThreadPool();

    public enum sendMessage {
        LOGIN_REQUEST,      // [1] -> username, [2] -> password
        MODE_SELECTION,     // [1] -> name of game from gameMode enum
        GUESS,              // [1] -> clients word guess
        LEADERBOARD,        // requests leaderboard update
        REGISTER_REQUEST    // [1] -> username, [2] -> password
    }

    public enum gameMode {
        ONE_VS_ONE,
        BATTLE_ROYAL
    }

    public static void main(String[] args) {
        server = null;
        try {
            server = new ServerSocket(23704);
            server.setReuseAddress(true);

            executorService.execute(new AcceptPlayers());
            executorService.execute(new LobbyHandler());
            executorService.execute(new FinishedMatchHandler());

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
                    for (Object o : clients) {
                        // HANDLE LOBBY REQUESTS
                        ConnectedClient client = (ConnectedClient) o;
                        if (client.requestedGame != null) {
                            switch (client.requestedGame) {
                                case "ONE_VS_ONE": {
                                    synchronized (lobbies) {
                                        for (Object lobby : lobbies) {
                                            Game game = (Game) lobby;
                                            if (game.getGamemode().equals("OneVsOne") && !game.isInProgress()) { // client joins open game if possible
                                                // add client
                                                System.out.println("ADDED CLIENT TO GAME");
                                                client.currentLobby = game;
                                                game.clientConnected();
                                                break;
                                            }
                                        }
                                    }
                                    if (client.currentLobby == null) { // create lobby if none were found
                                        System.out.println("Created New ONE_VS_ONE Lobby");
                                        Game temp = new OneVsOne();
                                        executorService.execute(temp);
                                        lobbies.add(temp);
                                        client.currentLobby = temp;
                                        temp.clientConnected();
                                    }
                                    break;
                                }
                                case "BATTLE_ROYAL": {
                                    synchronized (lobbies) {
                                        for (Object lobby : lobbies) {
                                            Game game = (Game) lobby;
                                            if (game.getGamemode().equals("BattleRoyale") && !game.isInProgress()) { // client joins open game if possible
                                                // add client
                                                System.out.println("ADDED CLIENT TO GAME");
                                                client.currentLobby = game;
                                                game.clientConnected();
                                                break;
                                            }
                                        }
                                    }
                                    if (client.currentLobby == null) { // create lobby if none were found
                                        Game temp = new BattleRoyale();
                                        executorService.execute(temp);
                                        lobbies.add(temp);
                                        client.currentLobby = temp;
                                        temp.clientConnected();
                                    }
                                    System.out.println("Created New BATTLE_ROYAL Lobby");
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

    private static class FinishedMatchHandler implements Runnable{
        @Override
        public void run(){
            System.out.println("FMH START");
            while (!server.isClosed()){
                synchronized (clients){
                    for (Object o : clients) {
                        ConnectedClient client = (ConnectedClient) o;
                        if (client.currentLobby != null && client.currentLobby.isFinished()) {
                            // update clients data (totalScore+=currentScore)
                            client.totalGamesPlayed += 1;

                            synchronized (lobbies){
                                lobbies.remove(client.currentLobby);
                                System.out.println("Removed match from lobbies");
                            }

                            client.currentLobby = null;
                            System.out.println("Set client cur lobby to null");
                        }
                    }
                }
            }
        }
    }

    private static class ConnectedClient implements Runnable {
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
                        case "GUESS":{
                            if(currentLobby != null && currentLobby.isInProgress()){
                                int tempScore = currentLobby.guess(clientMessage[1]);
                                currentScore += tempScore;
                                output.format(String.format("%s,%s\n", Client.sendMessage.GUESS_RESULT, this.currentScore));
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
                                output.format(String.format("%s,%s\n", Client.sendMessage.CLIENT_DATA, getStatString())); // send client data
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

    }
}

// TODO
// add lobby type field to game class if getting by object type isnt possible
// lobby cleanup once match ends or too many clients leave
//