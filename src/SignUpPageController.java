import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import java.io.IOException;
/**
 * SignUpPageController that derives from Controller and controls the sign-up page scene
 * @see Controller
 */
public class SignUpPageController extends Controller {
    /**
     * Member variable for the password field
     */
    @FXML
    private JFXPasswordField signUpPasswordField;
    /**
     * Member variable for the confirm password field
     */
    @FXML
    private JFXPasswordField signUpConfirmPassword;
    /**
     * Member variable for the sign-up feedback label
     */
    @FXML
    private Label signUpFeedbackLabel;
    /**
     * Member variable for the bottom grid pane
     */
    @FXML
    private GridPane bottomGridPane;
    /**
     * Member variable for the parent grid pane
     */
    @FXML
    private GridPane parentGridPane;
    /**
     * Member variable for the username field
     */
    @FXML
    private JFXTextField usernameField;
    /**
     * Member variable for the rippler
     */
    private JFXRippler rippler;
    /**
     * Listener for the sign-up button that sends a message to the server requesting to sign up
     */
    @FXML
    void signUpButtonListener() {
        String password = signUpPasswordField.getText();
        String confirmPassword = signUpConfirmPassword.getText();
        String username = usernameField.getText();

        if (password.equals("") || confirmPassword.equals("") || username.equals("")) {
            signUpFeedbackLabel.setText("Please fill out all fields");
            rippler.createManualRipple();
        } else if (username.length() > 50) {
            signUpFeedbackLabel.setText("Username is too long");
        } else if (password.equals(confirmPassword)) {
            getClient().sendMessage(String.format("%s,%s,%s\n", Server.sendMessage.REGISTER_REQUEST, username, password));
        } else {
            rippler.createManualRipple();
            signUpFeedbackLabel.setText("Passwords do not match");
        }
    }
    /**
     * Initialize method for the SignUpPageController class that sets the Client's controller to this and sets up the
     * GUI components
     */
    public void initialize() {
        getClient().setController(this);
        rippler = new JFXRippler(bottomGridPane);
        rippler.setRipplerFill(new Color(1, 0, 0, 0));
        parentGridPane.getChildren().add(rippler);
    }
    /**
     * Method for the client to call when the sign-up is valid and switches the scene to the home screen
     */
    @Override
    public void signUpValid() {
        setPlayer(new PlayerStats(usernameField.getText()));

        Platform.runLater(() -> {
            rippler.setRipplerFill(new Color(0, 0, 1, 0));
            signUpFeedbackLabel.setText("Account Created\n Loading Home Page...");

            try {
                switchScene("homeScreenFXML.fxml", "Home Screen");
            } catch (IOException e) {
                signUpFeedbackLabel.setText("Home Screen Could Not Be Loaded");
            }
        });
    }
    /**
     * Method for the client to tell the controller that the sign-up was invalid
     */
    @Override
    public void signUpInvalid() {
        Platform.runLater(() -> {
            rippler.setRipplerFill(new Color(1, 0, 0, 0));
            signUpFeedbackLabel.setText("Username Already Used");
        });
    }
}
