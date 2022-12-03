import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXHamburger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.IOException;

public class HomeScreenController extends Controller {
    @FXML
    private Pane statsPane;

    @FXML
    private Label totalWins;

    @FXML
    private Label tourneyWins;

    @FXML
    private Label brWins;

    @FXML
    private Label gamesPlayed;

    @FXML
    private Label h2hGames;

    @FXML
    private Label tourneysPlayed;

    @FXML
    private Label brPlayed;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private Label usernameLabel;

    @FXML
    private JFXButton h2hMode;

    @FXML
    private JFXButton battleRoyale;

    @FXML
    private JFXButton readyUp;

    @FXML
    private Label gameModeFeedback;

    @FXML
    private Label gameStatus;

    @FXML
    private JFXButton tournamentMode;

    @FXML
    private Label h2hWins;

    private boolean readiedUp;


//    private Background background;
//    private BackgroundFill red;
//    private BackgroundFill green;
//    private BackgroundFill blue;

    @FXML
    void readyUpListener() {

        if(!readiedUp) {
            if (battleRoyale.getBackground().getFills().get(0).getFill().equals(Color.GREEN)) {
                // send start game message to server with battle royale
                gameStatus.setText("Connecting to Game...");
                readyUp.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));
                getClient().sendMessage(String.format("%s,%s\n", Server.sendMessage.MODE_SELECTION, Server.gameMode.BATTLE_ROYAL));
                readiedUp = true;
                readyUp.setText("Cancel");
            } else if (h2hMode.getBackground().getFills().get(0).getFill().equals(Color.GREEN)) {
                // send start game message to server with head to head
                gameStatus.setText("Connecting to Game...");
                readyUp.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));
                getClient().sendMessage(String.format("%s,%s\n", Server.sendMessage.MODE_SELECTION, Server.gameMode.ONE_VS_ONE));
                readiedUp = true;
                readyUp.setText("Cancel");
            } else {
                gameModeFeedback.setText("Select a Game Mode");
                //gameModeFeedback.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
                gameModeFeedback.setTextFill(Color.RED);
            }
        }
        else {
            // send cancel message to server
            readyUp.setText("Ready Up!");
            readyUp.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
            h2hMode.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
            battleRoyale.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
            gameStatus.setText("");
            readiedUp = false;
        }
    }

    @FXML
    void h2hListener() {
        if(!readiedUp) {
            gameModeFeedback.setText("");
            battleRoyale.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
            h2hMode.setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));
        }
    }

    @FXML
    void battleRoyaleListener() {
        if(!readiedUp) {
            gameModeFeedback.setText("");
            h2hMode.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
            battleRoyale.setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));
        }
    }

    @FXML
    void hamburgerListener(MouseEvent event) {

    }

    @FXML
    void logOutListener() {
        try {
            switchScene("LoginFXML.fxml", "Login");
            setPlayer(null);
        }
        catch(IOException e) {
            e.printStackTrace();
        }

    }


    @FXML
    void tournamentModeListener() {
        // ask server if user is in a tournament
        // get response from server

        //getClient().sendMessage(String.format("%s,%s,%s\n",Server.sendMessage.MODE_SELECTION ,"Tournament", ""));

        try {
            switchScene("TournamentHomeFXML.fxml", "Tournament Home");
        } catch (IOException e) {
            gameModeFeedback.setText("Could not Open Tournament Mode");
        }
    }

    public void initialize() {
        readiedUp = false;
        getClient().setController(this);
        getClient().sendMessage(String.format("%s\n", Server.sendMessage.CLIENT_DATA_REQUEST));
        usernameLabel.setText(getPlayer().getUsername() + "'s Player Stats");
        statsPane.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        h2hMode.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        battleRoyale.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        tournamentMode.setBackground(new Background(new BackgroundFill(Color.LIGHTYELLOW, null, null)));
//        totalWins.setText(getPlayer().getTotalWins());
//        gamesPlayed.setText(getPlayer().getTotalGamesPlayed());
//        h2hWins.setText(getPlayer().getOVOWins());
//        h2hGames.setText(getPlayer().getOVOGamesPlayed());
//        brWins.setText(getPlayer().getBRWins());
//        brPlayed.setText(getPlayer().getBRGamesPlayed());
//        tourneyWins.setText(getPlayer().getTournamentWins());
//        tourneysPlayed.setText(getPlayer().getTournamentsPlayed());
    }

    @Override
    public void updatePlayerStats(String username, String totalWinsIn, String totalGamesPlayed, String OVOWins, String OVOGamesPlayed,
                                  String BRWins, String BRGamesPlayed, String tournamentWins, String tournamentsPlayed) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                updatePlayerStatsHelper(username, totalWinsIn, totalGamesPlayed, OVOWins, OVOGamesPlayed, BRWins, BRGamesPlayed, tournamentWins, tournamentsPlayed);
                totalWins.setText(totalWinsIn);
                gamesPlayed.setText(totalGamesPlayed);
                h2hWins.setText(OVOWins);
                h2hGames.setText(OVOGamesPlayed);
                brWins.setText(BRWins);
                brPlayed.setText(BRGamesPlayed);
                tourneyWins.setText(tournamentWins);
                tourneysPlayed.setText(tournamentsPlayed);
            }
        });
    }

    @Override
    public void gameStart() {
        try {
            switchScene("gameFXML.fxml", "Word Game");
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}