import java.net.InetAddress;
import java.net.UnknownHostException;

public abstract class Controller {
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    private Client client;
    Controller() {
//        try {
//            client = new Client(InetAddress.getLocalHost().getHostAddress(), "");
//        }
//        catch(UnknownHostException e) {
//            // Not sure what to do here yet
//        }
    }
}
