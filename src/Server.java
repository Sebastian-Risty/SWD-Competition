import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


class Server {
    private static ServerSocket server;

    private static volatile boolean gameStart = false;
    private static volatile boolean gameEnd = false;
    private int numPlayers = 0;
    private HashMap<String, Integer> results = new HashMap<>();

    public static void main(String[] args) {
        server = null;
        try {
            server = new ServerSocket(23704);
            server.setReuseAddress(true);

            ExecutorService executorService = Executors.newCachedThreadPool();
            Thread apThread = new Thread(new AcceptPlayers());
            executorService.execute(new AcceptPlayers());

            long startTime = System.currentTimeMillis();
            while (((System.currentTimeMillis() - startTime) / 1000) < 30) {
            }
            apThread.interrupt();

            gameStart = true;

            while (((System.currentTimeMillis() - startTime) / 1000) < 30) {
            }

            gameEnd = true;

            System.out.println("Game End");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class AcceptPlayers implements Runnable {
        @Override
        public void run() {
            System.out.println("AP START");
            try {
                while (true) {
                    Socket client = server.accept();

                    System.out.println("New player connected " + client.getInetAddress().getHostAddress());

                    ClientHandler clientSock = new ClientHandler(client);

                    new Thread(clientSock).start();
                }
            } catch (IOException e) {
                System.out.println("ERROR");
            }
        }
    }


    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private String username = null;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(
                        new OutputStreamWriter(
                                clientSocket.getOutputStream()));
                in = new BufferedReader(
                        new InputStreamReader(
                                clientSocket.getInputStream()));

                init();
                while (!gameStart) {
                }
                game();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void init() throws IOException {
            out.println("Enter Username: ");
            out.flush();
            while (username == null) {
                username = in.readLine();
            }
        }

        private void game() throws IOException {
            int score = 0;
            ArrayList<String> correctGuesses = new ArrayList<>();
//            out.println(
//                    "Guess as many words using the following letters as you can: \n" +
//                            Server.letters.toString() + "\n");
            out.flush();
            while (!gameEnd) {
                String userGuess = in.readLine();
//                checkGuess(userGuess);
            }
        }
    }
}
