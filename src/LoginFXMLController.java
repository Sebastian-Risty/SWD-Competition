import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class LoginFXMLController {

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXPasswordField passwordField;

    @FXML
    private JFXTextField usernameField;

    @FXML
    void signUpButtonListener(ActionEvent event) {
        // switch to sign up screen
    }
    @FXML
    void enterButtonListener(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // call Matts function for verifying

        boolean verified = false;

        if(verified) {
            // go to the home page of an account
        }
        else {
            // set stuff to let the user know it was wrong and try again
        }
    }

//    void initialize() {
//        hamburger.set
//    }

}
