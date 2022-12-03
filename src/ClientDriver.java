import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

public class ClientDriver extends Application {
    private static Controller controller;

    public static void main(String[] args) throws UnknownHostException {
        switch (args.length) {
            case 3:
                if (args[0].equals("text")) {
                    //TODO: call text class
                } else {
                    controller = new Controller();
                    controller.setIp(args[1]);
                    controller.setPort(Integer.parseInt(args[2]));
                    launch();
                }
                break;
            case 1:
                if (args[0].equals("text")) {
                    //TODO: call text class
                } else {
                    controller = new Controller();
                    controller.setIp(InetAddress.getLocalHost().getHostAddress());
                    controller.setPort(Integer.parseInt("23704"));
                    launch();
                }
                break;
            default:
                System.out.println("STARTING");
                controller = new Controller();
                controller.setIp(InetAddress.getLocalHost().getHostAddress());
                controller.setPort(Integer.parseInt("23704"));
                launch();
        }
    }


    @Override
    public void start(Stage primaryStage) throws IOException {
        URL fxmlFile = getClass().getResource("LoginFXMl.fxml");

        assert fxmlFile != null;
        Parent root = FXMLLoader.load(fxmlFile);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Word Game");
        primaryStage.setScene(scene);

        controller.setRoot(root);
        controller.setStage(primaryStage);

        primaryStage.show();
    }
}
