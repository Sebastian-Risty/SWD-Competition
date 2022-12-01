import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

class Client {
    private final String ip;
    private final int port;
    private BufferedReader in;
    private PrintWriter out;
    private Socket serverSocket;

    public Client(String ip, String port) {
        this.ip = ip;
        this.port = Integer.parseInt(port);
    }

    public static void main(String[] args) {
        Client client;
        switch (args.length) {
            case 2:
                client = new Client(args[0], args[1]);
                break;
            case 1:
                client = new Client(args[0], "23704");
                break;
            default:
                client = new Client("128.255.17.152", "23704");
        }
        client.startClient();
    }

    private void startClient() {
        try {
            connectToServer();
            openConnection();
            game();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connectToServer() {
        try {
            serverSocket = new Socket(InetAddress.getByName("127.0.0.1"), 23704);
        } catch (IOException ioException) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            connectToServer();
        }
    }

    private void openConnection() throws IOException {
        out = new PrintWriter(
                new OutputStreamWriter(
                        serverSocket.getOutputStream()));
        in = new BufferedReader(
                new InputStreamReader(
                        serverSocket.getInputStream()));
    }

    private void game() throws IOException {
        init();
        while (true) {
            Scanner scnr = new Scanner(System.in);
            String message = null;
            message = scnr.nextLine();

            out.println(message.toLowerCase());
            out.flush();

            String serverInput;
            while ((serverInput = in.readLine()) != null) {
                out.println(serverInput);
                out.flush();
            }

        }
    }

    private void init() throws IOException {
        String startMessage = null;
        while (startMessage == null) {
            startMessage = in.readLine();
        }

    }
}

