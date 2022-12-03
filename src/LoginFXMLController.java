import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.IOException;

public class LoginFXMLController extends Controller {
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

    public void initialize() {
        // create a client
        setClient(new Client(getIp(), getPort()));
        getClient().setController(this);
        verifyRippler = new JFXRippler(pane);
        verifyRippler.setRipplerFill(new Color(1, 0, 0, 0));
        gridPane.getChildren().add(verifyRippler);
    }

    @FXML
    void signUpButtonListener() throws IOException {
        switchScene("signUpScreen.fxml", "Sign Up");
    }

    @FXML
    void enterButtonListener() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.equals("") || password.equals("")) {
            verifyRippler.createManualRipple();
        } else {
            getClient().sendMessage(String.format("%s,%s,%s\n", Server.sendMessage.LOGIN_REQUEST, username, password));
        }
    }

    @Override
    public void loginInvalid() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                verifyRippler.createManualRipple();
            }
        });
    }

    @Override
    public void loginValid() {
        setPlayer(new PlayerStats(usernameField.getText()));

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    switchScene("homeScreenFXML.fxml", "Home Screen");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
