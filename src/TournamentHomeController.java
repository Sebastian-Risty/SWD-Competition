import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.util.Objects;

public class TournamentHomeController extends Controller {
    @FXML
    private JFXTextField createField;
    @FXML
    private JFXTextField joinField;
    @FXML
    private ListView<String> tournamentList;

    public void initialize() {
        getClient().setController(this);
        getClient().sendMessage(String.format("%s\n", Server.sendMessage.TOURNAMENT_DATA));
    }

    public void setUpTournamentData() {
        // TODO add tournaments to scroll pane
    }

    @FXML
    public void joinButtonListener(){
        String tournament = joinField.getText();

        if (!tournament.equals("")){
            getClient().sendMessage(String.format("%s\n", Server.sendMessage.JOIN_TOURNAMENT));
        }

    }

    @FXML
    public void createButtonListener(){
        String tournament = createField.getText();

        if (!tournament.equals("")) {
            getClient().sendMessage(String.format("%s\n", Server.sendMessage.CREATE_TOURNAMENT));
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
    public void joinTournament() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
           // TODO
            }
        });
    }

    @Override
    public void createTournament() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //TODO
            }
        });
    }
}
