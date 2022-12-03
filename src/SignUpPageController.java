import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class SignUpPageController extends Controller {
    @FXML
    private JFXPasswordField signUpPasswordField;
    @FXML
    private JFXPasswordField signUpConfirmPassword;
    @FXML
    private Label signUpFeedbackLabel;
    @FXML
    private GridPane bottomGridPane;
    @FXML
    private GridPane parentGridPane;
    @FXML
    private JFXTextField usernameField;
    JFXRippler rippler;

    @FXML
    void signUpButtonListener2(ActionEvent event) {
        String password = signUpPasswordField.getText();
        String confirmPassword = signUpConfirmPassword.getText();
        String username = usernameField.getText();

        if(password.equals("") || confirmPassword.equals("") || username.equals(""))
        {
            signUpFeedbackLabel.setText("Please fill out all fields");
            rippler.createManualRipple();
        }
        else if (username.length() > 50){
            signUpFeedbackLabel.setText("Username is too long");
        }
        else if(password.equals(confirmPassword)) {
            //getClient().send(login info)

            get

            setUsername(username);

            // Flip to main page

            // getClient().receive response back from server

            // set the feedback label based on what the server responded with

            signUpFeedbackLabel.setText("Account created ");
        }
        else {
            rippler.createManualRipple();
            signUpFeedbackLabel.setText("Passwords do not match");
        }
    }

    public void initialize() {
        rippler = new JFXRippler(bottomGridPane);
        rippler.setRipplerFill(new Color(1,0, 0,0));
        parentGridPane.getChildren().add(rippler);
    }
}
