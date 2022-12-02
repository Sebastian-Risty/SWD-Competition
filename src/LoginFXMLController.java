import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class LoginFXMLController {

    @FXML
    private JFXHamburger hamburger;
    @FXML
    private JFXRippler verifyRippler;

    @FXML
    private JFXPasswordField passwordField;

    @FXML
    private JFXTextField usernameField;
    @FXML
    private Pane pane;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private GridPane gridPane;

    public void initialize() {
        verifyRippler = new JFXRippler(pane);

        gridPane.getChildren().add(verifyRippler);
    }
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

            verifyRippler.createManualRipple();
        }
    }

//    void initialize() {
//        hamburger.set
//    }

}
