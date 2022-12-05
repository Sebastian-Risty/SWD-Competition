import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.io.IOException;

/**
 * GameResultsController class that inherits from Controller and controls the javaFX scene for the game results page
 *
 * @see Controller
 */
public class GameResultsController extends Controller {
    /**
     * Member variable for the leader board list
     */
    @FXML
    private ListView<String> leaderboardList;
    /**
     * Member variable for the position label
     */
    @FXML
    private Label positionLabel;
    /**
     * Member variable for the score label
     */
    @FXML
    private Label scoreLabel;

    /**
     * Initialize method for the GameResultsController class that sets the client's controller to this and displays the
     * results of the game by calling the display results method
     */
    public void initialize() {
        getClient().setController(this);
        displayResults(getClient().getGameResults());
    }

    /**
     * Method for displaying the results of the game to the screen. Overrides the controller class's method
     *
     * @param results an array of strings that are the results of the game
     */
    @Override
    public void displayResults(String[] results) {
        Platform.runLater(() -> {
            ObservableList<String> resultsList = FXCollections.observableArrayList();
            for (int i = 1; i < results.length - 1; i += 2) {
                String name = results[i];

                if (name.equals(getPlayer().getUsername())) {
                    positionLabel.setText(String.valueOf((i / 2) + 1));
                    scoreLabel.setText(results[i + 1]);
                }

                String score = results[i + 1];
                resultsList.add(name + "  :  " + score);
                leaderboardList.setItems(resultsList);
            }
        });
    }

    /**
     * Listener method for the return to main method button that calls switch scene back to the home page
     */
    @FXML
    void returnToMenuButtonListener() {
        try {
            switchScene("homeScreenFXML.fxml", "Home Screen");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
