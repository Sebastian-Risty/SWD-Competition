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
    JFXTextField createField;
    @FXML
    JFXTextField joinField;


    @FXML
    private ListView<String> tournamentList;

    @FXML
    void createButtonListener(ActionEvent event) {

    }

    @FXML
    void joinButtonListener(ActionEvent event) {

    }

    @FXML
    void menuButtonListener(ActionEvent event) {

    }

    public void initialize() {
        // TODO get tournaments from server and add them to scroll pane
        getClient().setController(this);
        getClient().sendMessage(String.format("%s\n", Server.sendMessage.TOURNAMENT_DATA));

    }

    public void setUpTournamentData() {
        // TODO add tournaments to scroll pane
    }

    @FXML
    public void joinButtonListener(){
        String tournament = joinField.getText();

        if (!Objects.equals(tournament, "")){
            // TODO send message to server
        }

    }

    @FXML
    public void createButtonListener(){
        String tournament = joinField.getText();

        if (!Objects.equals(tournament, "")) {
            // TODO send message to server
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
}
