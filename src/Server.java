import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


class Server {
    private static ServerSocket server;

    private static final ArrayList<ConnectedClient> clients = new ArrayList<>();
    private static final ArrayList<Game> lobbies = new ArrayList<>();


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

            ExecutorService executorService = Executors.newCachedThreadPool();
            executorService.execute(new AcceptPlayers());
            executorService.execute(new LobbyHandler());

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
        public void run() {
            System.out.println("LH START");
            while (!server.isClosed()) {
                // loop through client list
                for (ConnectedClient client : clients) {

                    // HANDLE LOBBY REQUESTS
                    if (client.requestedGame != null) {
                        switch (client.requestedGame) {
                            case "ONE_VS_ONE": {
                                for (Game game : lobbies) {
                                    if (game instanceof OneVsOne && !game.isInProgress()) { // client joins open game if possible
                                        // add client
                                        client.currentLobby = game;
                                        game.clientConnected();
                                        break;
                                    }
                                }
                                if (client.currentLobby == null) { // create lobby if none were found
                                    Game temp = new OneVsOne();
                                    lobbies.add(temp);
                                    client.currentLobby = temp;
                                    temp.clientConnected();
                                }
                                break;
                            }
                            case "BATTLE_ROYAL": {
                                for (Game game : lobbies) {
                                    if (game instanceof BattleRoyale && !game.isInProgress()) { // client joins open game if possible
                                        // add client
                                        client.currentLobby = game;
                                        game.clientConnected();
                                        break;
                                    }
                                }
                                if (client.currentLobby == null) { // create lobby if none were found
                                    Game temp = new BattleRoyale();
                                    lobbies.add(temp);
                                    client.currentLobby = temp;
                                    temp.clientConnected();
                                }
                                break;
                            }
                        }
                        System.out.println("Created New Lobby");
                    }
                }
            }

        }
    }

    private static class FinishedMatchHandler implements Runnable {
        @Override
        public void run() {
            System.out.println("FMH START");
            while (!server.isClosed()) {
                for (ConnectedClient client : clients) {
                    if (client.currentLobby != null) {
                        // check if match is finished via flag
                        // update clients data (totalScore+=currentScore)
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

        @Override
        public void run() {
            try {
                init(); // verify login and load client data from db
                while (!server.isClosed()) { // handle client messages
                    System.out.println("AWAITING CLIENT COMMAND");
                    String receivedData = input.nextLine();
                    System.out.printf("Message Received: %s\n", receivedData);
                    String[] clientMessage = receivedData.split(",");
                    switch (clientMessage[0]) {
                        case "MODE_SELECTION": {
                            requestedGame = clientMessage[1];
                            break;
                        }
                        case "GUESS": {
                            if (currentLobby != null && currentLobby.isInProgress()) {
                                int tempScore = currentLobby.guess(clientMessage[1]);
                                currentScore += tempScore;
                                output.format(String.format("%s,%s\n", Client.sendMessage.GUESS_RESULT, this.currentScore));
                                output.flush();
                            }
                            break;
                        }
                    }
                }
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    sendAccountData(); // TODO: remove client from list
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
            Accounts.initialize("login");
            while (this.username == null) {
                if (input.hasNext()) {
                    String receivedData = input.nextLine();
                    System.out.printf("Message Received: %s\n", receivedData);
                    String[] clientMessage = receivedData.split(",");
                    switch (clientMessage[0]) {
                        case "LOGIN_REQUEST":
                            if (Accounts.validLogin(clientMessage[1], clientMessage[2])) {
                                System.out.println("LOGIN GOOD");
                                Accounts.setTable("accounts");
                                acceptAccountData(Accounts.getInfo(clientMessage[1]));
                                output.format(String.format("%s\n", Client.sendMessage.LOGIN_VALID));
                                output.flush();
                                output.format(String.format("%s,%s\n", Client.sendMessage.CLIENT_DATA, getStatString())); // send client data
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
                            if (Accounts.addAccount(clientMessage[1], clientMessage[2])) {
                                this.username = clientMessage[1];
                                output.format(String.format("%s\n", Client.sendMessage.SIGNUP_VALID));
                                output.flush();
                                output.format(String.format("%s,%s\n", Client.sendMessage.CLIENT_DATA, getStatString())); // send client data
                                output.flush();
                                clients.add(this);
                                System.out.printf("Client %s successfully registered and logged in!\n", this.username);
                            } else {
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
        private void sendAccountData() throws SQLException {
            Accounts.setTable("accounts");
            Accounts.update(getStatString().split(","));
        }

    }
}

// TODO
// add lobby type field to game class if getting by object type isnt possible
// lobby cleanup once match ends or too many clients leave
//