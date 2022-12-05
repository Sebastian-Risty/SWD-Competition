import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;

import java.io.IOException;
import java.util.Objects;

public class TournamentHomeController extends Controller{
    @FXML
    JFXTextField createField;
    @FXML
    JFXTextField joinField;

    public void initialize() {
        // TODO get tournaments from server and add them to scroll pane
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

        if (!Objects.equals(tournament, "")){
            // TODO send message to server
        }

    }

    @FXML
    void menuButtonListener() throws IOException {
        switchScene("homeScreen.fxml", "Home Screen");
    }
}
