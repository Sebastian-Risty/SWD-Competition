import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import java.io.IOException;

public class GameResultsController extends Controller {

    @FXML
    private ListView<String> leaderboardList;
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
                System.out.println("Player " + getPlayer().getUsername());
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
            }
        });
    }

    @FXML
    void returnToMenuButtonListener() {
        try {
            switchScene("homeScreenFXML.fxml", "Home Screen");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
