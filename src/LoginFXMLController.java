import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXTextField;

import javafx.event.ActionEvent;
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

        verifyRippler = new JFXRippler(pane);
        verifyRippler.setRipplerFill(new Color(1,0, 0,0));
        gridPane.getChildren().add(verifyRippler);

    }
    @FXML
    void signUpButtonListener(ActionEvent event) throws IOException {
        switchScene("signUpScreen.fxml", "Sign Up");
    }
    @FXML
    void enterButtonListener(ActionEvent event) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // getClient().send the login info
        getClient().sendMessage(String.format("%s,%s,%s\n",Server.sendMessage.LOGIN_REQUEST ,username, password));

        // getClient().receive whether valid or not

        // Set verified equal to what the server said
        boolean verified = true;

        if(verified) {
            setUsername(username);
            switchScene("homeScreenFXML.fxml", "Home Screen");
        }
        else {
            verifyRippler.createManualRipple();
        }
    }
}
