import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.IOException;

/**
 * HomeScreenController class that derives from Controller and controls the home screen JavaFX screen
 *
 * @see Controller
 * Back ground image from https://www.pexels.com/photo/brown-wooden-parquet-flooring-129731/ Free for personal use license
 */
public class HomeScreenController extends Controller {
    /**
     * Member variable for the stats pane on the screen
     */
    @FXML
    private Pane statsPane;
    /**
     * Member variable for the total wins label
     */
    @FXML
    private Label totalWins;
    /**
     * Member variable for the tourney wins label
     */
    @FXML
    private Label tourneyWins;
    /**
     * Member variable for the battle royale wins label
     */
    @FXML
    private Label brWins;
    /**
     * Member variable for the games played label
     */
    @FXML
    private Label gamesPlayed;
    /**
     * Member variable for the head-to-head games played label
     */
    @FXML
    private Label h2hGames;
    /**
     * Member variable for the tournaments played label
     */
    @FXML
    private Label tourneysPlayed;
    /**
     * Member variable for the battle royale games played
     */
    @FXML
    private Label brPlayed;
    /**
     * Member variable for the username label
     */
    @FXML
    private Label usernameLabel;
    /**
     * Member variable for the head-to-head mode button
     */
    @FXML
    private JFXButton h2hMode;
    /**
     * Member variable for the battle royale mode button
     */
    @FXML
    private JFXButton battleRoyale;
    /**
     * Member variable for the ready up button
     */
    @FXML
    private JFXButton readyUp;
    /**
     * Member variable for the game mode feedback label
     */
    @FXML
    private Label gameModeFeedback;
    /**
     * Member variable for the game status label
     */
    @FXML
    private Label gameStatus;
    /**
     * Member variable for the tournament mode button
     */
    @FXML
    private JFXButton tournamentMode;
    /**
     * Member variable for the head-to-head wins label
     */
    @FXML
    private Label h2hWins;
    /**
     * Member variable for the log-out confirmation yes button
     */
    @FXML
    private JFXButton logoutConfirmationYes;
    /**
     * Member variable for the log-out confirmation no button
     */
    @FXML
    private JFXButton logoutConfirmationNo;
    /**
     * Member variable for the log-out button
     */
    @FXML
    private JFXButton logOutButton;
    /**
     * Member variable for whether the user is currently readied up
     */
    private boolean readiedUp;
    /**
     * Member variable for the if the user clicked log-out
     */
    private boolean logout;

    /**
     * Initialize method for the HomeScreenController that sets the fill colors of buttons,
     * sets the client's controller to this controller and sets up some other GUI components
     */
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

    /**
     * Listener for the ready up button that tells the server that the user is ready for a game and also switches the
     * text to cancel after readying up. It also tells the server which mode the user selected
     */
    @FXML
    void readyUpListener() {

        if (!readiedUp) {
            logOutButton.setText("");
            if (battleRoyale.getBackground().getFills().get(0).getFill().equals(Color.LIGHTBLUE)) {
                // Send client the mode the user selected
                getClient().sendMessage(String.format("%s,%s\n", Server.sendMessage.MODE_SELECTION, Server.gameMode.BATTLE_ROYAL));
                // Set the labels on the screen while waiting to connect to the game
                gameStatus.setTextFill(Color.BLACK);
                gameStatus.setText("Connecting to Game...");
                gameStatus.setText("Waiting for Players");
                readyUp.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));
                readyUp.setText("Cancel");

                readiedUp = true;
            } else if (h2hMode.getBackground().getFills().get(0).getFill().equals(Color.LIGHTBLUE)) {
                // send user's mode selection to server
                getClient().sendMessage(String.format("%s,%s\n", Server.sendMessage.MODE_SELECTION, Server.gameMode.ONE_VS_ONE));
                // Edit the labels while waiting to connect to the game
                gameStatus.setTextFill(Color.BLACK);
                gameStatus.setText("Connecting to Game...");
                gameStatus.setText("Waiting for a Match");
                readyUp.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));
                readyUp.setText("Cancel");
                readiedUp = true;

            } else {
                gameStatus.setText("Select a Game Mode");
                gameStatus.setTextFill(Color.RED);
            }
        } else {
            // send cancel message to server
            getClient().sendMessage(String.format("%s\n", Server.sendMessage.CANCEL_MM));
            getClient().stopTimer();
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

    /**
     * Head-to-head mode listener for when the user clicks the head-to-head mode button
     */
    @FXML
    void h2hListener() {
        if (!readiedUp) {
            gameStatus.setText("");
            battleRoyale.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
            h2hMode.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));
        }
    }

    /**
     * Battle Royale mode listener for when the button is clicked by the user
     */
    @FXML
    void battleRoyaleListener() {
        if (!readiedUp) {
            gameStatus.setText("");
            h2hMode.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
            battleRoyale.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));
        }
    }

    /**
     * Listener for when the user clicks the log-out button that brings up the are you sure buttons
     */
    @FXML
    void logOutListener() {
        if (!readiedUp) {
            logout = true;
            logOutButton.setText("Are You Sure?");
            logoutConfirmationNo.setText("No");
            logoutConfirmationYes.setText("Yes");
        }
    }

    /**
     * Listener for the log-out confirmation yes button that logs out the user when visible and clicked
     */
    @FXML
    void logOutConfirmationYesListener() {
        if (logout) {
            try {
                getClient().sendMessage(String.format("%s\n", Server.sendMessage.LOGOUT_REQUEST));
                setPlayer(null);
                switchScene("LoginFXML.fxml", "Log In");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Listener for the log-out no confirmation button that resets the log-out button
     */
    @FXML
    void logOutConfirmationNoListener() {
        if (logout) {
            logOutButton.setText("Log Out");
            logoutConfirmationNo.setText("");
            logoutConfirmationYes.setText("");
            logout = false;
        }
    }

    /**
     * Listener for the tournament mode button that switches the scene to the tournament home screen
     */
    @FXML
    void tournamentModeListener() {
        if (!readiedUp) {
            try {
                switchScene("TournamentHomeFXML.fxml", "Tournament Home");
            } catch (IOException e) {
                gameStatus.setText("Could not Open Tournament Mode");
            }
        }
    }

    /**
     * Method that overrides the updatePlayerStatsScreen in controller that updates the stats on the screen when the
     * client gets new data from the server
     */
    @Override
    public void updatePlayerStatsScreen() {
        Platform.runLater(() -> {
            totalWins.setText(getPlayer().getTotalWins());
            gamesPlayed.setText(getPlayer().getTotalGamesPlayed());
            h2hWins.setText(getPlayer().getOVOWins());
            h2hGames.setText(getPlayer().getOVOGamesPlayed());
            brWins.setText(getPlayer().getBRWins());
            brPlayed.setText(getPlayer().getBRGamesPlayed());
            tourneyWins.setText(getPlayer().getTournamentWins());
            tourneysPlayed.setText(getPlayer().getTournamentsPlayed());
        });
    }

    /**
     * Method for the client to call to tell the controller the game has started and switches the scene to the game
     * screen
     */
    @Override
    public void gameStart() {
        Platform.runLater(() -> {
            try {
                switchScene("gameFXML.fxml", "Game");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * updatePlayersConnected method that overrides the controller method and updates the screen with the server data of
     * how many clients are connected
     *
     * @param numPlayers the number of players currently connected
     */
    @Override
    public void updatePlayersConnected(int numPlayers) {

        Platform.runLater(() -> {
            if (readiedUp) {
                gameStatus.setText("Players connected: " + numPlayers);
                if (numPlayers < 3) {
                    gameModeFeedback.setText("Waiting for players");
                }
            }
        });
    }

    /**
     * Method for updating the countdown to match label on the screen. Overrides the controller's method
     *
     * @param time the updated time
     */
    @Override
    public void updateTimer(int time) {
        Platform.runLater(() -> {
            if (readiedUp) {
                gameModeFeedback.setText("Time: " + time);

            }
        });
    }
}