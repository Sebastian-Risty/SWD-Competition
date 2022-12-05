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
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.IOException;

/**
 * LoginScreenController class that derives from Controller and controls the login JavaFX screen
 *
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
    /**
     * Member variable for the loginFeedback label
     */
    @FXML
    private Label loginFeedback;
    /**
     * Member variable for the username field
     */
    @FXML
    private JFXTextField usernameField;
    /**
     * Member variable for a pane that the rippler is in
     */
    @FXML
    private Pane pane;
    /**
     * Member variable for the parent pane of the rippler pane
     */
    @FXML
    private Pane parentPane;
    /**
     * Member variable for the enter button
     */
    @FXML
    private JFXButton enterButton;

    /**
     * Initialize method for the LoginScreenController class that sets the client controller to this and sets up some
     * GUI stuff
     */
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

    /**
     * Listener for when enter is presses in the password field that automatically calls the enter button listener
     *
     * @param event the key event that triggered the listener
     */
    @FXML
    void passwordFieldEnter(KeyEvent event) {
        if (event.getEventType().equals(KeyEvent.KEY_PRESSED) && event.getCode().equals(KeyCode.ENTER)) {
            enterButtonListener();
        }
    }

    /**
     * Listener for the username field that when enter is pressed automatically puts the focus on the password field
     *
     * @param event the key event that triggered the listener
     */
    @FXML
    void usernameFieldEnter(KeyEvent event) {
        if (event.getEventType().equals(KeyEvent.KEY_PRESSED) && event.getCode().equals(KeyCode.ENTER)) {
            passwordField.requestFocus();
        }
    }

    /**
     * Listener for the sign-up button that switches the scene to the sign-up screen
     */
    @FXML
    void signUpButtonListener() throws IOException {
        switchScene("signUpScreen.fxml", "Sign Up");
    }

    /**
     * Listener for the enter button that sends a request to the server to login
     */
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

    /**
     * method for the client to call when it gets an invalid login request
     */
    @Override
    public void loginInvalid() {
        Platform.runLater(() -> {
            loginFeedback.setText("Invalid Login or user is already logged in");
            pane.getChildren().remove(0);
            verifyRippler = new JFXRippler(pane);
            parentPane.getChildren().add(verifyRippler);
            verifyRippler.setRipplerFill(new Color(1, 0, 0, 0));
            verifyRippler.createManualRipple();
        });
    }

    /**
     * method for the client to call when it gets a valid log in request and switches the scene to the home page
     */
    @Override
    public void loginValid() {
        setPlayer(new PlayerStats(usernameField.getText()));
        Platform.runLater(() -> {
            try {
                switchScene("homeScreenFXML.fxml", "Home Screen");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
