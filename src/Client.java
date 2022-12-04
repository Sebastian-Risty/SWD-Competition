import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
    private final ExecutorService clientExecutor = Executors.newFixedThreadPool(1);

    // TEXTMODE STUFF
    private boolean loggedIn = false;

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setTextMode(boolean textMode) {
        this.textMode = textMode;
    }
    public boolean isTextMode(){return textMode;}
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
            if(!textMode){
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
                    case "GUESS_RESULT" :{
                        controller.guessResult(Integer.parseInt(clientMessage[1]));
                    }
                    case "SHUTDOWN":
                        clientExecutor.shutdown();
                        try {
                            if(!clientExecutor.awaitTermination(2, TimeUnit.SECONDS)){
                                System.out.println("shutdown failed, forcing.");
                                clientExecutor.shutdownNow();
                            }
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                }
            } else{
                switch (clientMessage[0]) {
                    case "LOGIN_VALID": {
                        loggedIn = true;
                        break;
                    }
                    case "CLIENT_DATA": {
                        System.out.println(Arrays.toString(clientMessage));
                        stats = new PlayerStats(clientMessage[1], clientMessage[2], clientMessage[3], clientMessage[4],
                                clientMessage[5], clientMessage[6], clientMessage[7], clientMessage[8], clientMessage[9]);
                        break;
                    }
                    case "SIGNUP_VALID": {
                        loggedIn = true;
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
                    case "GUESS_RESULT" :{
                        controller.guessResult(Integer.parseInt(clientMessage[1]));
                    }
                    case "SHUTDOWN":
                        clientExecutor.shutdown();
                        try {
                            if(!clientExecutor.awaitTermination(2, TimeUnit.SECONDS)){
                                System.out.println("shutdown failed, forcing.");
                                clientExecutor.shutdownNow();
                            }
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
        GUESS_RESULT,   // [1] -> score received from guess
        GAME_END,        // [1] -> (bool)hasWon, winningUsername,
        TIMER_UPDATE,
        PLAYER_COUNT_UPDATE, // [1] -> numPlayers in match
        SHUTDOWN
    }
}
