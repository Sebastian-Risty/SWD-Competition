import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXHamburger;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class HomeScreenController extends Controller{
    @FXML
    private JFXHamburger hamburger;
    @FXML
    private Pane statsPane;

    @FXML
    private Label usernameLabel;

    @FXML
    private JFXButton h2hMode;
    @FXML
    private JFXButton tournamentMode;

    @FXML
    private JFXButton battleRoyale;
    @FXML
    private JFXButton readyUp;
    @FXML
    private Label gamesPlayed;

    @FXML
    private Label score;

    @FXML
    private Label h2hWins;

    @FXML
    private Label tourneyWins;

    @FXML
    private Label tier;

    @FXML
    private Label worldRank;

    @FXML
    private Label gameModeFeedback;

//    private Background background;
//    private BackgroundFill red;
//    private BackgroundFill green;
//    private BackgroundFill blue;

    @FXML
    private Label gameStatus;

    @FXML
    void readyUpListener() {

        if(battleRoyale.getBackground().getFills().get(0).getFill().equals(Color.GREEN)) {
            // send start game message to server with battle royale
            gameStatus.setText("Connecting to Game...");
            readyUp.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));
            getClient().sendMessage(String.format("%s,%s,%s\n",Server.sendMessage.MODE_SELECTION ,"Battle Royale", ""));

        }
        else if(h2hMode.getBackground().getFills().get(0).getFill().equals(Color.GREEN)) {
            // send start game message to server with head to head
            gameStatus.setText("Connecting to Game...");
            readyUp.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));
            getClient().sendMessage(String.format("%s,%s,%s\n",Server.sendMessage.MODE_SELECTION ,"H2H", ""));

        }
        else {
            gameModeFeedback.setText("Select a Game Mode");
            //gameModeFeedback.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
            gameModeFeedback.setTextFill(Color.RED);
        }
    }

    @FXML
    void h2hListener() {
        gameModeFeedback.setText("");
        battleRoyale.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        h2hMode.setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));
    }

    @FXML
    void battleRoyaleListener() {
        gameModeFeedback.setText("");
        h2hMode.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        battleRoyale.setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));
    }

    @FXML
    void hamburgerListener(MouseEvent event) {

    }

    @FXML
    void tournamentModeListener() {
        // switch to tournament mode screen
    }

    public void initialize() {
        statsPane.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        h2hMode.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        battleRoyale.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        tournamentMode.setBackground(new Background(new BackgroundFill(Color.LIGHTYELLOW, null, null)));
        score.setText(String.valueOf(getPlayer().getScore()));
        gamesPlayed.setText(String.valueOf(getPlayer().getNumGamesPlayed()));
        h2hWins.setText(String.valueOf(getPlayer().getH2hWins()));
        tourneyWins.setText(String.valueOf(getPlayer().getTourneyWins()));
        tier.setText(String.valueOf(getPlayer().getTier()));
        worldRank.setText(String.valueOf(getPlayer().getRank()));
    }
}