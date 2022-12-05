import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
class Client implements Runnable {
    private boolean textMode = false;
    private final String ip;
    private final int port;
    private Scanner input; // input from server
    private Formatter output; // output to server
    private Socket serverSocket;
    private Controller controller;
    private String letters;
    private String[] gameResults;
    private PlayerStats stats;
    private final ExecutorService clientExecutor = Executors.newCachedThreadPool();
    private Integer guessResult = null;

    // TEXTMODE STUFF
    private boolean loggedIn = false;
    private boolean gameStart = false;

    public Integer getGuessResult() {
        return guessResult;
    }

    public boolean isGameStart() {
        return gameStart;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setTextMode(boolean textMode) {
        this.textMode = textMode;
    }

    public boolean isTextMode() {
        return textMode;
    }

    public String[] getGameResults() {
        return gameResults;
    }

    public String getLetters() {
        return letters;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void sendMessage(String message) { // MUST END WITH NEWLINE
        output.format(message);
        output.flush();
    }

    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
        startClient();
        clientExecutor.execute(this);
    }

    private void startClient() {
        try {
            connectToServer();
            openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connectToServer() {
        try {
            serverSocket = new Socket(ip, port);
        } catch (IOException ioException) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("RETRY CONNECTION");
            connectToServer(); //TODO: limit retry count
        }
    }

    private void openConnection() throws IOException {
        input = new Scanner(serverSocket.getInputStream());
        output = new Formatter(serverSocket.getOutputStream());
    }

    @Override
    public void run() {
        System.out.println("AWAITING SERVER DATA");
        while (true) {
            String receivedData = input.nextLine();
            System.out.printf("Message Received: %s\n", receivedData);
            String[] clientMessage = receivedData.split(",");
            if (!textMode) {
                switch (clientMessage[0]) {
                    case "LOGIN_VALID": {
                        controller.loginValid();
                        break;
                    }
                    case "LOGIN_INVALID": {
                        controller.loginInvalid();
                        break;
                    }
                    case "CLIENT_DATA": {
                        System.out.println(Arrays.toString(clientMessage));
                        controller.updatePlayerStats(clientMessage[1], clientMessage[2], clientMessage[3], clientMessage[4],
                                clientMessage[5], clientMessage[6], clientMessage[7], clientMessage[8], clientMessage[9]);
                        controller.updatePlayerStatsScreen();
                        break;
                    }
                    case "SIGNUP_VALID": {
                        controller.signUpValid();
                        break;
                    }
                    case "SIGNUP_INVALID": {
                        controller.signUpInvalid();
                        break;
                    }
                    case "TOURNAMENT_DATA": {
                        controller.updateTournament(clientMessage);
                        break;
                    }
                    case "GAME_START": {
                        letters = clientMessage[1];
                        controller.gameStart();
                        break;
                    }
                    case "GAME_END": {
                        gameResults = clientMessage;
                        controller.endGame();
                        break;
                    }
                    case "GUESS_RESULT": {
                        controller.guessResult(Integer.parseInt(clientMessage[1]));
                        break;
                    }
                    case "TIMER_UPDATE": {
                        clientExecutor.execute(new TimerHandler(this, clientMessage[1], clientMessage[2]));
                        break;
                    }
                    case "PLAYER_COUNT_UPDATE": {
                        controller.updatePlayersConnected(Integer.parseInt(clientMessage[1]));
                        break;
                    }
                    case "SHUTDOWN": {
                        System.out.println("CALLING SYS EXIT");
                        System.exit(-1);
                        break;
                    }
                    case "CREATE_TOURNAMENT": {
                        controller.createTournament(clientMessage);
                        break;
                    }
                    case "TOURNAMENT_PLAYER_DATA": {
                        controller.joinTournament(clientMessage);
                        break;
                    }
                }
                } else {
                switch (clientMessage[0]) {
                    case "LOGIN_VALID":
                    case "SIGNUP_VALID": {
                        loggedIn = true;
                        break;
                    }
                    case "CLIENT_DATA": {
                        System.out.println(Arrays.toString(clientMessage));
                        stats = new PlayerStats(clientMessage[1], clientMessage[2], clientMessage[3], clientMessage[4],
                                clientMessage[5], clientMessage[6], clientMessage[7], clientMessage[8], clientMessage[9]);
                        break;
                    }
                    case "GAME_START": {
                        letters = clientMessage[1];
                        gameStart = true;
                        break;
                    }
                    case "GAME_END": {
                        System.out.println("GAME OVER!");
                        gameResults = clientMessage;
                        for (int i = 1; i < getGameResults().length; i+=2) {
                            System.out.printf("Rank: %s | User: %s | Score: %s\n", (i / 2) + 1, getGameResults()[i], getGameResults()[i+1]);
                        }
                        sendMessage(String.format("%s\n", Server.sendMessage.CLIENT_DISCONNECT));
                        System.exit(0);
                        break;
                    }
                    case "GUESS_RESULT": {
                        guessResult = Integer.parseInt(clientMessage[1]);
                        break;
                    }
                }
            }
        }
    }

    private static class TimerHandler implements Runnable {
        private final Client client;
        private final long startTime;
        private final int totalTime;

        public TimerHandler(Client client, String startTime, String totalTime) {
            System.out.println("TIMER CONSTRUCTOR");
            this.client = client;
            this.startTime = Long.parseLong(startTime);
            this.totalTime = Integer.parseInt(totalTime);
        }

        @Override
        public void run() {
            System.out.println("TIMER RUN");
            long elapsed;
            while ((elapsed = (System.currentTimeMillis() - startTime)) < (totalTime * 1000L)) {
                if ((elapsed % 1000) == 0) {
                    client.controller.updateTimer((int) (totalTime - (elapsed / 1000)));
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }


    public enum sendMessage {
        LOGIN_VALID,   // username/password incorrect
        LOGIN_INVALID,  //
        CLIENT_DATA,    // [1:9] -> username, total wins, T GP, OVO wins, OVO GP, BR wins, BR GP, T wins, T GP
        SIGNUP_VALID,   //
        SIGNUP_INVALID, //
        GAME_START, //
        GUESS_RESULT, // [1] -> score received from guess
        TOURNAMENT_DATA,
        TOURNAMENT_PLAYER_DATA,
        CREATE_TOURNAMENT,
        GAME_END,        // [1] -> (bool)hasWon, winningUsername,
        TIMER_UPDATE,
        PLAYER_COUNT_UPDATE, // [1] -> numPlayers in match
        SHUTDOWN
    }
}
