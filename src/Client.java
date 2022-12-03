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

class Client implements Runnable{
    private final String ip;
    private final int port;
    private Scanner input; // input from server
    private Formatter output; // output to server
    private Socket serverSocket;
    //private Controller controller;

    private enum messages{
        LOGIN_FAILED,
        LOGIN_SUCCESS,
        CLIENT_DATA,
        LOGIN_REQUEST
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

    public void sendMessage(String message){ // MUST END WITH NEWLINE
        output.format(message);
        output.flush();
    }

    @Override
    public void run(){
        System.out.println("AWAITING SERVER DATA");
        while(true){
            // TODO: handle server messages to update gui here

        }
    }
}
