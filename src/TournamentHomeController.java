import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.Arrays;

/**
 * TournamentHomeController class that derives from Controller and controls the Tournament home screen
 */
public class TournamentHomeController extends Controller {
    /**
     * Member variable for the create tourney field
     */
    @FXML
    private JFXTextField createField;
    /**
     * Member variable for the join tourney field
     */
    @FXML
    private JFXTextField joinField;
    /**
     * Member variable to store all the tournaments
     */
    @FXML
    private ListView<String> tournamentList;
    /**
     * Member variable for the parent pane of the rippler
     */
    @FXML
    private AnchorPane parentPane;
    /**
     * Member variable for the rippler pane
     */
    @FXML
    private Pane ripplerPane;
    /**
     * Member variable for the rippler
     */
    @FXML
    private JFXRippler verifyRippler;
    /**
     * Member variable for the join tourney button
     */
    @FXML
    private JFXButton joinTourney;
    /**
     * Member variable for the create tourney button
     */
    @FXML
    private JFXButton createTourney;
    /**
     * Member variable for the main menu button
     */
    @FXML
    private JFXButton mainMenuButton;

    /**
     * TournamentHomeController class controller that sets the client's controller to this and initializes all the
     * GUI components
     */

    public void initialize() {
        getClient().setController(this);
        getClient().sendMessage(String.format("%s\n", Server.sendMessage.TOURNAMENT_DATA));
        Platform.runLater(() -> {
            verifyRippler = new JFXRippler(ripplerPane);
            parentPane.getChildren().add(verifyRippler);
            joinTourney.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
            createTourney.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
            mainMenuButton.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        });
    }

    /**
     * Listener method for the join button that sends a message to the server to join a tournament
     */
    @FXML
    public void joinButtonListener() {
        String tournament = joinField.getText();
        if (!tournament.equals("")) {
            getClient().sendMessage(String.format("%s,%s\n", Server.sendMessage.JOIN_TOURNAMENT, tournament));
        }
    }

    /**
     * Listener method for the create tourney button that sends a message to the server
     */
    @FXML
    public void createButtonListener() {
        String tournament = createField.getText();
        if (!tournament.equals("")) {
            getClient().sendMessage(String.format("%s,%s\n", Server.sendMessage.CREATE_TOURNAMENT, tournament));
        }
    }

    /**
     * listener method for the menu button that sends it back to the home screen
     */
    @FXML
    void menuButtonListener() {
        Platform.runLater(() -> {
            try {
                switchScene("homeScreenFXML.fxml", "Home Screen");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Method for the client to update the tournaments. Overrides the Controller's method
     *
     * @param data the tournament data from the server
     */
    @Override
    public void updateTournament(String[] data) {
        Platform.runLater(() -> {
            ObservableList<String> tempList = FXCollections.observableArrayList();
            tempList.addAll(Arrays.asList(data).subList(1, data.length));
            tournamentList.setItems(tempList);
        });
    }

    /**
     * Method for the client to tell the controller they can join a tournament and switches the scene to the
     * tournament individual screen
     *
     * @param data the tournament data
     */
    @Override
    public void joinTournament(String[] data) {

        Platform.runLater(() -> {
            if (data[1].equals("true")) {
                setTournamentData(data);
                try {
                    switchScene("IndividualTournament.fxml", "Tournament");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                parentPane.getChildren().remove(7);
                verifyRippler = new JFXRippler(ripplerPane);
                parentPane.getChildren().add(verifyRippler);
                verifyRippler.setRipplerFill(new Color(1, 0, 0, 0));
                verifyRippler.createManualRipple();
            }
        });
    }

    /**
     * Method for the server to tell the controller it can enter the tournament it created
     *
     * @param data the tournament data
     */
    @Override
    public void createTournament(String[] data) {
        Platform.runLater(() -> {
            if (data[1].equals("true")) {
                try {
                    setTournamentData(data);
                    switchScene("IndividualTournament.fxml", "Tournament");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                parentPane.getChildren().remove(7);
                verifyRippler = new JFXRippler(ripplerPane);
                parentPane.getChildren().add(verifyRippler);
                verifyRippler.setRipplerFill(new Color(1, 0, 0, 0));
                verifyRippler.createManualRipple();
            }
        });
    }
}
