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

public class TournamentHomeController extends Controller {
    @FXML
    private JFXTextField createField;
    @FXML
    private JFXTextField joinField;
    @FXML
    private ListView<String> tournamentList;
    @FXML
    private AnchorPane parentPane;
    @FXML
    private Pane ripplerPane;
    @FXML
    private JFXRippler verifyRippler;
    @FXML
    private JFXButton joinTourney;
    @FXML
    private JFXButton createTourney;
    @FXML
    private JFXButton mainMenuButton;

    public void initialize() {
        getClient().setController(this);
        getClient().sendMessage(String.format("%s\n", Server.sendMessage.TOURNAMENT_DATA));
        Platform.runLater(() -> {
            verifyRippler = new JFXRippler(ripplerPane);
            parentPane.getChildren().add(verifyRippler);
            joinTourney.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null,null)));
            createTourney.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null,null)));
            mainMenuButton.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        });
    }

    @FXML
    public void joinButtonListener() {
        String tournament = joinField.getText();
        if (!tournament.equals("")) {
            getClient().sendMessage(String.format("%s,%s\n", Server.sendMessage.JOIN_TOURNAMENT, tournament));
        }
    }

    @FXML
    public void createButtonListener() {
        String tournament = createField.getText();
        if (!tournament.equals("")) {
            getClient().sendMessage(String.format("%s,%s\n", Server.sendMessage.CREATE_TOURNAMENT, tournament));
        }
    }

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

    @Override
    public void updateTournament(String[] data) {
        Platform.runLater(() -> {
            ObservableList<String> tempList = FXCollections.observableArrayList();
            tempList.addAll(Arrays.asList(data).subList(1, data.length));
            tournamentList.setItems(tempList);
        });
    }

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
