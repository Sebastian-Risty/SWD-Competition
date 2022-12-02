import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Formatter;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Client extends Application implements Runnable{
    private final String ip;
    private final int port;
    private Scanner input; // input from server
    private Formatter output; // output to server
    private Socket serverSocket;
    private Controller controller;

    private enum messages{
        LOGIN_FAILED,
        LOGIN_SUCCESS,
        CLIENT_DATA,
        LOGIN_REQUEST
    }

    public Client(String ip, String port) {
        this.ip = ip;
        this.port = Integer.parseInt(port);
        Executors.newFixedThreadPool(1).execute(this); // add thread for client
    }

    public static void main(String[] args) throws UnknownHostException {
        Client client;
        switch (args.length) {
            case 3:
                if(args[0].equals("text")){
                    //TODO: call text class
                } else {
                    launch();
                }
                client = new Client(args[1], args[2]);
                break;
            case 1:
                if(args[0].equals("text")){
                    //TODO: call text class
                } else {
                    launch();
                }
                client = new Client(InetAddress.getLocalHost().getHostAddress(), "23704");
                break;
            default:
                launch();
                client = new Client(InetAddress.getLocalHost().getHostAddress(), "23704");
        }

        client.startClient();
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
            connectToServer(); //TODO: limit retry count
        }
    }

    private void openConnection() throws IOException { // TODO conform to server std
        input = new Scanner(serverSocket.getInputStream());
        output = new Formatter(serverSocket.getOutputStream());
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        URL fxmlFile = getClass().getResource("LoginFXMl.fxml");

        controller = new Controller();
        controller.setClient(this);

        assert fxmlFile != null;
        Parent root = FXMLLoader.load(fxmlFile);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Word Game");
        primaryStage.setScene(scene);

        controller.setRoot(root);
        //controller.setScene(scene);

        primaryStage.show();
    }

    @Override
    public void run(){
        System.out.println("AWAITING SERVER DATA");
        while(true){
            // TODO: handle server messages to update gui here

        }
    }
}
