import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


class Server {
    private static ServerSocket server;

    private static final ArrayList<ConnectedClient> clients = new ArrayList<>();
    private static final ArrayList<Game> lobbies = new ArrayList<>();

    private enum messages{
        LOGIN_FAILED,
        LOGIN_SUCCESS,
        CLIENT_DATA,
        LOGIN_REQUEST,
        MODE_SELECTION,
        GUESS,
        GUESS_RESULT,
        REQUEST_STATS,
        STATS
    }

    private enum gameMode{
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
        public void run(){
            System.out.println("LH START");
            while (!server.isClosed()) {
                // loop through client list
                for(ConnectedClient client : clients){
                    // loop through list of running lobbies, create if none exist or all are full
                    if(client.requestedGame != null){
                        switch(client.requestedGame){
                            case "ONE_VS_ONE":{
                                // TODO: check for open existing lobbies
                                // create lobby if none are open/exist, add to lobby list
                                // add client to the lobby (update currentLobby field + increment lobby's playercount field)
                                // increment connectedClient count field within lobby class
                                break;
                            }
                            case "BATTLE_ROYAL":{
                                // TODO: check for open existing lobbies
                                // create lobby if none are open/exist
                                // add client to the lobby
                                // increment connectedClient count field within lobby class
                                break;
                            }
                        }
                        System.out.println("Created New Lobby");
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
        private int totalScore = 0; // TODO: add to total score once lobby ends, probably done in lobby cleanup method or smthn
        private int currentScore = 0;

        private Formatter output;
        private Scanner input;

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
                            if(currentLobby != null){
                                int tempScore = currentLobby.guess(clientMessage[1]);
                                currentScore += tempScore;
                                output.format(String.format("%s,%s\n", messages.GUESS_RESULT, this.currentScore));
                                output.flush();
                            }
                            break;
                        }
                        case "SHOW_STATS":{
                            output.format(String.format("%s,%s\n", messages.STATS, this.totalScore)); // TODO: add other stats to display here
                            output.flush();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try { // TODO: upload client data to DB
                    if (output != null) {
                        output.close();
                    }
                    if (input != null) {
                        input.close();
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void init() throws IOException {
            System.out.println("START INIT");

            String receivedData = input.nextLine();
            System.out.printf("Message Received: %s\n", receivedData);

            String[] clientMessage = receivedData.split(",");
           if(clientMessage[0].equals(messages.LOGIN_REQUEST.toString())){
               // [1] -> userName, [2] -> password
               try{
                   Accounts.addAccount(clientMessage[1],clientMessage[2]);
                   Accounts.validLogin(clientMessage[1],clientMessage[2]);
                   // call db with data
                   // TODO: check db for user+pass

                   // if userName + pass is found update the data of client
                   //TODO: create method to take db data and update all client info

                   // output login was success
                   output.format(String.format("%s\n", messages.LOGIN_SUCCESS));
                   output.flush();

                   // add valid client to list of connected clients to play match
                   clients.add(this);
                   System.out.printf("Added Client %s to client list\n", this.username);

               } catch(Exception e){
                   output.format(String.format("%s\n", messages.LOGIN_FAILED));
                   output.flush();
               }
           } // TODO: may need to loop here until client successfully logs in
        }
    }
}

// TODO
// add lobby type field to game class if getting by object type isnt possible
// lobby cleanup once match ends or too many clients leave
//