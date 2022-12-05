import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.ArrayList;

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
    @FXML
    private JFXButton logoutConfirmationYes;
    @FXML
    private JFXButton logoutConfirmationNo;
    @FXML
    private JFXButton logOutButton;
    @FXML
    private Label connectedPlayersLabel;

    private boolean readiedUp;
    private boolean logout;

    public void initialize() {
        readiedUp = false;
        logout = false;
        getClient().setController(this);
        logOutButton.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        readyUp.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, null, null)));
        getClient().sendMessage(String.format("%s\n", Server.sendMessage.CLIENT_DATA_REQUEST));
        usernameLabel.setText(getPlayer().getUsername() + "'s Player Stats");
        statsPane.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        h2hMode.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        battleRoyale.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        tournamentMode.setBackground(new Background(new BackgroundFill(Color.LIGHTYELLOW, null, null)));
    }

    @FXML
    void readyUpListener() {

        if(!readiedUp) {
            logOutButton.setText("");
        if (battleRoyale.getBackground().getFills().get(0).getFill().equals(Color.LIGHTBLUE)) {
                // Send client the mode the user selected
                getClient().sendMessage(String.format("%s,%s\n", Server.sendMessage.MODE_SELECTION, Server.gameMode.BATTLE_ROYAL));
                // Set the labels on the screen while waiting to connect to the game
                gameStatus.setText("Connecting to Game...");
                gameStatus.setText("Waiting for Players");
                readyUp.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));
                readyUp.setText("Cancel");

                readiedUp = true;
            } else if (h2hMode.getBackground().getFills().get(0).getFill().equals(Color.LIGHTBLUE)) {
                // send user's mode selection to server
                getClient().sendMessage(String.format("%s,%s\n", Server.sendMessage.MODE_SELECTION, Server.gameMode.ONE_VS_ONE));
                // Edit the labels while waiting to connect to the game
                gameStatus.setText("Connecting to Game...");
                gameStatus.setText("Waiting for a Match");
                readyUp.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));
                readyUp.setText("Cancel");
                readiedUp = true;

            } else {
                gameModeFeedback.setText("Select a Game Mode");
                gameModeFeedback.setTextFill(Color.RED);
            }
        }
        else {
            // send cancel message to server
            getClient().sendMessage(String.format("%s\n", Server.sendMessage.CANCEL_MM));
            readyUp.setText("Ready Up!");
            logOutButton.setText("Log Out");
            readyUp.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, null, null)));
            h2hMode.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
            battleRoyale.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
            gameStatus.setText("");
            gameModeFeedback.setText("");
            readiedUp = false;
        }
    }

    @FXML
    void h2hListener() {
        if(!readiedUp) {
            gameModeFeedback.setText("");
            battleRoyale.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
            h2hMode.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));
        }
    }

    @FXML
    void battleRoyaleListener() {
        if(!readiedUp) {
            gameModeFeedback.setText("");
            h2hMode.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
            battleRoyale.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));
        }
    }

    @FXML
    void logOutListener() {
        if(!readiedUp) {
            logout = true;
            logOutButton.setText("Are You Sure?");
            logoutConfirmationNo.setText("No");
            logoutConfirmationYes.setText("Yes");
        }
    }

    @FXML
    void logOutConfirmationYesListener() {
        if(logout) {
            try {
                getClient().sendMessage(String.format("%s\n", Server.sendMessage.LOGOUT_REQUEST));
                setPlayer(null);
                switchScene("LoginFXML.fxml", "Log In");
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
    @FXML
    void logOutConfirmationNoListener() {
        if(logout) {
            logOutButton.setText("Log Out");
            logoutConfirmationNo.setText("");
            logoutConfirmationYes.setText("");
            logout = false;
        }
    }

    @FXML
    void tournamentModeListener() {
        if(!readiedUp) {
            try {
                switchScene("IndividualTournament.fxml", "Tournament Home");
            } catch (IOException e) {
                gameModeFeedback.setText("Could not Open Tournament Mode");
            }
        }
    }

    @Override
    public void updatePlayerStatsScreen() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                totalWins.setText(getPlayer().getTotalWins());
                gamesPlayed.setText(getPlayer().getTotalGamesPlayed());
                h2hWins.setText(getPlayer().getOVOWins());
                h2hGames.setText(getPlayer().getOVOGamesPlayed());
                brWins.setText(getPlayer().getBRWins());
                brPlayed.setText(getPlayer().getBRGamesPlayed());
                tourneyWins.setText(getPlayer().getTournamentWins());
                tourneysPlayed.setText(getPlayer().getTournamentsPlayed());
            }
        });
    }

    @Override
    public void gameStart() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    switchScene("gameFXML.fxml", "Game");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void updatePlayersConnected(int numPlayers) {
        if (numPlayers > 2) {
            connectedPlayersLabel.setText("Players connected: " + numPlayers);
        }
    }

    public void updateTimer(int time) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                gameModeFeedback.setText("Time: " + time);
            }
        });
    }
}