import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import java.io.IOException;
/**
 * LoginScreenController class that derives from Controller and controls the login JavaFX screen
 * @see Controller
 */
public class LoginScreenController extends Controller {
    /**
     * Member variable for the rippler that ripples to let the user know if the login was invalid
     */
    @FXML
    private JFXRippler verifyRippler;
    /**
     * Member variable for the passwordField
     */
    @FXML
    private JFXPasswordField passwordField;
    @FXML
    private Label loginFeedback;
    @FXML
    private JFXTextField usernameField;
    @FXML
    private Pane pane;
    @FXML
    private Pane parentPane;
    @FXML
    private JFXButton enterButton;

    public void initialize() {
        // create a client
        setClient(new Client(getIp(), getPort()));
        getClient().setController(this);
        enterButton.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        verifyRippler = new JFXRippler(pane);
        verifyRippler.setRipplerFill(new Color(1, 0, 0, 0));
        parentPane.getChildren().add(verifyRippler);
        usernameField.requestFocus();
    }

    @FXML
    void passwordFieldEnter(KeyEvent event) {
        if(event.getEventType().equals(KeyEvent.KEY_PRESSED) && event.getCode().equals(KeyCode.ENTER)) {
            enterButtonListener();
        }
    }
    @FXML
    void usernameFieldEnter(KeyEvent event) {
        if(event.getEventType().equals(KeyEvent.KEY_PRESSED) && event.getCode().equals(KeyCode.ENTER)) {
            passwordField.requestFocus();
        }
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
                loginFeedback.setText("Invalid Login or user is already logged in");
                pane.getChildren().remove(0);
                verifyRippler = new JFXRippler(pane);
                parentPane.getChildren().add(verifyRippler);
                verifyRippler.setRipplerFill(new Color(1, 0, 0, 0));
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
