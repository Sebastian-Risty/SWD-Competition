import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class LoginFXMLController {

    @FXML
    private JFXRippler verifyRippler;
    @FXML
    private JFXPasswordField passwordField;
    @FXML
    private JFXTextField usernameField;
    @FXML
    private Pane pane;
    @FXML
    private GridPane gridPane;

    private Client client;

    public void initialize() {

        // create a client

        verifyRippler = new JFXRippler(pane);
        verifyRippler.setRipplerFill(new Color(1,0, 0,0));
        gridPane.getChildren().add(verifyRippler);

    }
    @FXML
    void signUpButtonListener(ActionEvent event) throws IOException {
        URL fxmlFile = getClass().getResource("signUpScreen.fxml");

        Stage primaryStage = new Stage();
        assert fxmlFile != null;
        Parent root = FXMLLoader.load(fxmlFile);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Sign Up");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    @FXML
    void enterButtonListener(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // call Matts function for verifying
        // verified = verifyAccount(username, password);
        boolean verified = false;

        if(verified) {
            // go to the home page of an account
        }
        else {
            verifyRippler.createManualRipple();
        }
    }
}
