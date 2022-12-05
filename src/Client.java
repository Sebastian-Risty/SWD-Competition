import java.io.IOException;
import java.net.Socket;
import java.util.Formatter;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Client handles commands being sent from server and utilizes. Communicates with a controller to modify GUI.
 * Temporarily stores data sent from server.
 * If in text mode utilizes different methods of storing states of match.
 */
class Client implements Runnable {

    /**
     * True if client is running in text mode. Text mode only displays the absolute minimum to play the game.
     */
    private boolean textMode = false;
    /**
     * The ip address of the server to connect to
     */
    private final String ip;
    /**
     * The port of the server to connect to
     */
    private final int port;
    /**
     * used to handle input stream from server
     */
    private Scanner input;
    /**
     * used to handle output stream to server
     */
    private Formatter output;
    /**
     * The socket to of the server that the client connected to
     */
    private Socket serverSocket;
    /**
     * The controller handles which stage to display in the GUI
     */
    private Controller controller;
    /**
     * Temporary storage for the string of letters the client is allowed to create words from
     */
    private String letters;
    /**
     * Temporary storage for the string array containing the username and score of each client in a given match, ordered from the greatest score to least [1] -> userName (firstPlace), [2] -> firstplace score, [3] userName (secondPlace) ...
     */
    private String[] gameResults;
    /**
     * Creates thread for the client Runnable which handles message commands from server, also creates thread for match timer
     *
     * @see Runnable
     */
    private final ExecutorService clientExecutor = Executors.newCachedThreadPool();
    /**
     * Temporary storage of the score received from the server after sending in a guess
     */
    private Integer guessResult = null;
    private TimerHandler timerHandler;
    private volatile boolean timerFlag;

    /**
     * TEXTMODE ONLY
     * If the user has been logged in
     */
    private boolean loggedIn = false;
    /**
     * TEXTMODE ONLY
     * If the match has started
     */
    private boolean gameStart = false;

    /**
     * Client constructor, creates server connection and creates thread for the runnable
     *
     * @param ip   Server ip
     * @param port Server port
     */
    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
        startClient();
        clientExecutor.execute(this);
    }

    /**
     * Getter method for the guess result
     *
     * @return the guess result
     */
    public Integer getGuessResult() {
        return guessResult;
    }

    /**
     * isGameStart method that returns if the game is started
     *
     * @return gameStart
     */
    public boolean isGameStart() {
        return gameStart;
    }

    /**
     * isLoggedOn method that returns if the cleint is logged on
     *
     * @return loggedIn
     */
    public boolean isLoggedIn() {
        return loggedIn;
    }

    /**
     * Getter method for the game results
     */
    public String[] getGameResults() {
        return gameResults;
    }

    /**
     * Setter method for the text mode
     *
     * @param textMode the text mode
     */
    public void setTextMode(boolean textMode) {
        this.textMode = textMode;
    }

    /**
     * Getter method for letters
     */
    public String getLetters() {
        return letters;
    }

    /**
     * Setter method for the controller
     *
     * @param controller the controller
     */
    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * flips timer flag to stop timer
     */
    public void stopTimer() {
        timerFlag = false;
    }

    /**
     * sends server a message command
     *
     * @param message The message to be sent
     */
    public void sendMessage(String message) { // MUST END WITH NEWLINE
        output.format(message);
        output.flush();
    }

    /**
     * Calls helper methods to initialize connections
     */
    private void startClient() {
        try {
            connectToServer();
            openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Attempts to create a connection to the server every second until successful
     */
    private void connectToServer() {
        try {
            serverSocket = new Socket(ip, port);
        } catch (IOException ioException) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            connectToServer();
        }
    }

    /**
     * Creates input and output streams to server
     *
     * @throws IOException If creating streams fail
     */
    private void openConnection() throws IOException {
        input = new Scanner(serverSocket.getInputStream());
        output = new Formatter(serverSocket.getOutputStream());
    }

    /**
     * Handles server command messages
     * First half of switch statement is for GUI, second half is for text mode
     */
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                String receivedData = input.nextLine();
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
                            clientExecutor.execute(timerHandler = new TimerHandler(this, clientMessage[1], clientMessage[2]));
                            break;
                        }
                        case "PLAYER_COUNT_UPDATE": {
                            controller.updatePlayersConnected(Integer.parseInt(clientMessage[1]));
                            break;
                        }
                        case "SHUTDOWN": {
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
                        case "GAME_START": {
                            letters = clientMessage[1];
                            gameStart = true;
                            break;
                        }
                        case "GAME_END": {
                            System.out.println("GAME OVER!");
                            gameResults = clientMessage;
                            for (int i = 1; i < getGameResults().length; i += 2) {
                                System.out.printf("Rank: %s | User: %s | Score: %s\n", (i / 2) + 1, getGameResults()[i], getGameResults()[i + 1]);
                            }
                            sendMessage(String.format("%s\n", Server.sendMessage.CLIENT_DISCONNECT));
                            System.exit(-1);
                            break;
                        }
                        case "GUESS_RESULT": {
                            guessResult = Integer.parseInt(clientMessage[1]);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("client closed successfully!");
        }
    }

    /**
     * Handles the display of timers.
     * Takes a start time from server and adjusts displayed timer accordingly.
     * Server should send command to switch scenes around the same time the timers reach zero
     */
    private static class TimerHandler implements Runnable {
        /**
         * Reference to the client the timer is for
         */
        private final Client client;
        /**
         * The start time sent from the server
         */
        private final long startTime;
        /**
         * The total time elapsed since start time
         */
        private final int totalTime;

        /**
         * Time handler constructor
         *
         * @param client    the client
         * @param startTime the starting time
         * @param totalTime the total time
         */
        public TimerHandler(Client client, String startTime, String totalTime) {
            this.client = client;
            this.startTime = Long.parseLong(startTime);
            this.totalTime = Integer.parseInt(totalTime);
            client.timerFlag = true;
        }

        /**
         * run method that calculates and keeps track of the time
         */
        @Override
        public void run() {
            long elapsed;
            while ((elapsed = (System.currentTimeMillis() - startTime)) < (totalTime * 1000L) && client.timerFlag) {
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


    /**
     * Contains commands the client can receive from the server
     */
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
