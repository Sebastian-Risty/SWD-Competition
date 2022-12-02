import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Final_ProjectGUImain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        URL fxmlFile = getClass().getResource("LoginFXMl.fxml");

        Controller controller = new Controller();
        controller.setStage(primaryStage);

        assert fxmlFile != null;
        Parent root = FXMLLoader.load(fxmlFile);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Word Game");
        primaryStage.setScene(scene);

        controller.setScene(scene);
        controller.setRoot(root);

        primaryStage.show();
    }
}
