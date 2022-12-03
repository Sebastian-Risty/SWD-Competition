import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXHamburger;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

public class HomeScreenController {
    @FXML
    private JFXHamburger hamburger;

    @FXML
    private Label usernameLabel;

    @FXML
    private JFXButton h2hMode;

    @FXML
    private JFXButton tourneyMode;

    @FXML
    void hamburgerListener(MouseEvent event) {

    }
    public void initialize() {

        h2hMode.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
        tourneyMode.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
    }
}