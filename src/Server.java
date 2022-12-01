import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


class Server {
    private static ServerSocket server;

    private static volatile boolean gameStart = false;
    private static volatile boolean gameEnd = false;
    private static ArrayList<String> validWords = new ArrayList<>();
    private static ArrayList<String> letters = new ArrayList<>();
    private int numPlayers = 0;
    private HashMap<String, Integer> results = new HashMap<>();

    public static void main(String[] args) {
        server = null;
        try {
            server = new ServerSocket(23704);
            server.setReuseAddress(true);

            ExecutorService executorService = Executors.newCachedThreadPool();
            Thread apThread = new Thread(new AcceptPlayers());
            executorService.execute(new GameInitializer());
            executorService.execute(new AcceptPlayers());

            long startTime = System.currentTimeMillis();
            while (((System.currentTimeMillis() - startTime) / 1000) < 30) {
            }
            apThread.interrupt();

            System.out.println(validWords);
            System.out.println(letters);

            System.out.println("Game Start");

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

    private static class GameInitializer implements Runnable {
        private static final ArrayList<String> totalWordPool = new ArrayList<>();
        private static final ArrayList<String> sixLetterWordPool = new ArrayList<>();
        private static String selectedWord;
        private static int[] letterFreq = new int[26];

        private static ArrayList<String> findValidWords() {
            ArrayList<String> matchWords = new ArrayList<>();
            letterFreq = findLetterFreq(selectedWord);
            for (String word : totalWordPool) {
                int[] tempFreq = findLetterFreq(word);
                if (match(tempFreq)) {
                    matchWords.add(word);
                }
            }
            return matchWords;
        }

        private static boolean match(int[] tempFreq) {
            for (int i = 0; i < 26; i++) {
                if (letterFreq[i] == 0 && tempFreq[i] > 0) {
                    return false;
                } else if (letterFreq[i] < tempFreq[i]) {
                    return false;
                }
            }
            return true;
        }

        private static int[] findLetterFreq(String word) {
            int[] freq = new int[26];
            for (char letter : word.toCharArray()) {
                freq[letter - 'a']++;
            }
            return freq;
        }

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            System.out.println("StartTime: " + startTime);
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader("words"));

                String line = reader.readLine();
                while (line != null) {
                    if (line.length() > 2 && line.length() < 7) {
                        totalWordPool.add(line);
                        if (line.length() == 6) {
                            sixLetterWordPool.add(line);
                        }
                    }
                    line = reader.readLine();
                }
                reader.close();
                System.out.println("reading took: " + ((System.currentTimeMillis() - startTime)));

                int selectedIndex = (int) (Math.random() * sixLetterWordPool.size());

                selectedWord = sixLetterWordPool.get(selectedIndex);

                Server.letters = new ArrayList<>(Arrays.asList(selectedWord.split("\\a")));
                Server.validWords = findValidWords();

                System.out.println("finding valid words took: " + ((System.currentTimeMillis() - startTime)));

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                System.out.println("Interrupting Game init thread");
                Thread.currentThread().interrupt();
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
            out.println(
                    "Guess as many words using the following letters as you can: \n" +
                            Server.letters.toString() + "\n");
            out.flush();
            while (!gameEnd) {
                String userGuess = in.readLine();
//                checkGuess(userGuess);
            }
        }
    }
}
