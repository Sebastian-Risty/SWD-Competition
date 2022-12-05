import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.Objects;

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

    public void initialize() {
        getClient().setController(this);
        getClient().sendMessage(String.format("%s\n", Server.sendMessage.TOURNAMENT_DATA));
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                verifyRippler = new JFXRippler(ripplerPane);
                parentPane.getChildren().add(verifyRippler);
                joinTourney.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null,null)));
                createTourney.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null,null)));
            }
        });
    }

    public void setUpTournamentData() {
        // TODO add tournaments to scroll pane
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
    void menuButtonListener() throws IOException {
        switchScene("homeScreen.fxml", "Home Screen");
    }

    @Override
    public void updateTournament(String[] data) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ObservableList<String> tempList = FXCollections.observableArrayList();
                for (int i = 1; i < data.length; i++) {
                    tempList.add(data[i]);
                }
                tournamentList.setItems(tempList);
            }
        });
    }

    @Override
    public void joinTournament(String[] data) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (data[1].equals("true")) {
                    setTournamentData(data[2]);
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
            }
        });

    }

    @Override
    public void createTournament(String[] data) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (data[1].equals("true")) {
                    try {
                        setTournamentData(data[2]);
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
            }
        });
    }
}
