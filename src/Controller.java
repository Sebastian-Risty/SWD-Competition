import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

public class Controller {
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    private Client client;

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Parent getRoot() {
        return root;
    }

    public void setRoot(Parent root) {
        this.root = root;
    }

    private Scene scene;
    private Stage stage;
    private Parent root;

    public void switchScene(String fxmlUrl, String sceneTitle) throws IOException {

        Stage tempStage = stage;
        //setStage(new Stage());

        URL fxmlFile = getClass().getResource(fxmlUrl);
        assert fxmlFile != null;
        setRoot(FXMLLoader.load(fxmlFile));
        setScene(new Scene(getRoot()));

        //Stage stage = new Stage();
        stage.setScene(getScene());

        //setStage(stage);

        stage.setTitle(sceneTitle);
        getStage().show();
    }
}
