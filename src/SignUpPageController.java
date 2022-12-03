import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.io.IOException;

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
    void signUpButtonListener() {
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
            getClient().sendMessage(String.format("%s,%s,%s\n",Server.sendMessage.REGISTER_REQUEST,username, password));
        }
        else {
            rippler.createManualRipple();
            signUpFeedbackLabel.setText("Passwords do not match");
        }
    }

    public void initialize() {
        getClient().setController(this);
        rippler = new JFXRippler(bottomGridPane);
        rippler.setRipplerFill(new Color(1,0, 0,0));
        parentGridPane.getChildren().add(rippler);
    }

    @Override
    public void signUpValid() {
        setPlayer(new PlayerStats(usernameField.getText()));

        rippler.setRipplerFill(new Color(0, 0, 1, 0));
        signUpFeedbackLabel.setText("Account Created\n Loading Home Page...");

        try {
            Thread.sleep(2000);
        }
        catch(InterruptedException e) {
            e.printStackTrace();
        }

        try {
            switchScene("homeScreenFXML.fxml", "Home Screen");
        }
        catch(IOException e) {
            signUpFeedbackLabel.setText("Home Screen Could Not Be Loaded");
        }
    }

    @Override
    public void signUpInvalid() {
        rippler.setRipplerFill(new Color(1, 0, 0, 0));
        signUpFeedbackLabel.setText("Username Already Used");
    }
}
