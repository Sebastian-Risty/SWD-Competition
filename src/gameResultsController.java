import com.jfoenix.controls.JFXHamburger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class gameResultsController extends Controller {

    @FXML
    private ListView<String> leaderboardList;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private Label positionLabel;

    @FXML
    private Label scoreLabel;


    public void initialize() {
        getClient().setController(this);
        displayResults(getClient().getGameResults());
    }

    @Override
    public void displayResults(String[] results) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ObservableList<String> resultsList = FXCollections.observableArrayList();
                for (int i = 2; i < results.length - 1; i += 2) {
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
    void returnToMenuButton(KeyEvent event) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    switchScene("homeScreenFXML.fxml", "Main Menu");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void hamburgerListener(MouseEvent mouseEvent) {
    }
}
