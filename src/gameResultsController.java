import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXHamburger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyEvent;

import java.awt.event.MouseEvent;
import java.io.IOException;

public class gameResultsController extends Controller {

    @FXML
    private ListView<String> leaderboardList;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXButton logoutConfirmationNo;

    @FXML
    private JFXButton logoutConfirmationYes;

    @FXML
    private Label positionLabel;

    @FXML
    private Label scoreLabel;

    @FXML
    void hamburgerListener(MouseEvent event) {

    }

    public void initialize() {
        getClient().setController(this);
    }

    @Override
    public void displayResults(String[] results) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ObservableList<String> resultsList = FXCollections.observableArrayList();
                for (int i = 2; i < results.length; i += 2) {
                    String name = results[i];
                    String score = results[i + 1];
                    resultsList.add(name + "  :  " + score);
                    if (results[1].equals(name)) {
                        positionLabel.setText("" + (i / 2));
                        scoreLabel.setText("" + score);
                    }
                    leaderboardList = new ListView<>(resultsList);
                }
            }
        });
    }

    @FXML
    void returnToMenuButton(KeyEvent event) throws IOException {
        switchScene("homeScreenFXML.fxml", "Main Menu");
    }
}
