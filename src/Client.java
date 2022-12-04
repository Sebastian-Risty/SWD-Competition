import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Scanner;
import java.util.concurrent.Executors;

class Client implements Runnable {
    private final String ip;
    private final int port;
    private Scanner input; // input from server
    private Formatter output; // output to server
    private Socket serverSocket;
    private Controller controller;
    private String letters;

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
        Executors.newFixedThreadPool(1).execute(this); // add thread for client
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

    private void openConnection() throws IOException { // TODO conform to server std
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
                    controller.endGame();
                    controller.displayResults(clientMessage);
                    break;
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
        GAME_END        // [1] -> (bool)hasWon, winningUsername,
    }
}
