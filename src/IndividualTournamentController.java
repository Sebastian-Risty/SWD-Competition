import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import java.io.IOException;

/**
 * IndividualTournamentController class that derives from Controller and controls the individual tournament screen
 */
public class IndividualTournamentController extends Controller {
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
     * Member variable for the main menu button
     */
    @FXML
    private JFXButton mainMenuButton;
    /**
     * Member variable for the leader board pane
     */
    @FXML
    private GridPane leaderboardPane;
    /**
     * Member variable for the user pane
     */
    @FXML
    private GridPane userPane;
    /**
     * Member variable for if the player is readied up
     */
    private boolean readiedUp;
    /**
     * Initialize method for the IndividualTournamentController that sets the controller to this and sets up the
     * leader board pane and user pane
     */
    public void initialize() {
        getClient().setController(this);
        readyUp.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        mainMenuButton.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));

        int j = 2;
        while(j<getTournamentData().length - 2) {
            if(getTournamentData()[j].equals(getPlayer().getUsername())) {
                addToUserPane(String.valueOf(((j - 1) / 3) + 1), getTournamentData()[j], getTournamentData()[j + 1], getTournamentData()[j + 2]);
                if (getTournamentData()[j + 2].equals("0")) {
                    readyUp.setDisable(true);
                }
            }
            j+=3;
        }

        boolean length = getTournamentData().length >= 32;
        if(length) {
            for (int i = 2; i < 30; i += 3) {
                addToLeaderBoardPane(String.valueOf(((i - 1) / 3) + 1), getTournamentData()[i], getTournamentData()[i + 1], getTournamentData()[i + 2]);
            }
        } else {
            for (int i = 2; i < getTournamentData().length - 2; i += 3) {
                System.out.println(i);
                addToLeaderBoardPane(String.valueOf(((i - 1) / 3) + 1), getTournamentData()[i], getTournamentData()[i + 1], getTournamentData()[i + 2]);
            }
        }
    }
    /**
     * Menu button listener that sends the user back to the main menu
     * @throws IOException if scene can not be loaded
     */
    @FXML
    void mainMenuButtonListener() throws IOException {
        if (!readiedUp) {
            switchScene("homeScreenFXML.fxml", "Main Menu");
        }
    }
    /**
     * Method for the client to call to tell the controller to start the game and switches the scene to the game screen
     */
    @Override
    public void gameStart() {
        System.out.println("GAME START");
        Platform.runLater(() -> {
            try {
                switchScene("gameFXML.fxml", "Game");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    /**
     * Listener for the ready up button that starts match making if a mode is selected
     */
    @FXML
    void readyUpListener() {
        if (!readiedUp) {
            // Send client the mode the user selected
            getClient().sendMessage(String.format("%s,%s\n", Server.sendMessage.MODE_SELECTION, Server.gameMode.TOURNAMENT));
            // Set the labels on the screen while waiting to connect to the game
            gameStatus.setText("Connecting to Game...");
            gameModeFeedback.setText("Waiting for Player");
            readyUp.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));
            readyUp.setText("Cancel");

            readiedUp = true;
        } else {
            // send cancel message to server
            getClient().sendMessage(String.format("%s\n", Server.sendMessage.CANCEL_MM));
            readyUp.setText("Ready Up!");
            readyUp.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, null, null)));
            gameStatus.setText("");
            gameModeFeedback.setText("");
            readiedUp = false;
        }
    }
    /**
     * Method for adding all the stats to the leader board pane
     *
     * @param rankIn the user's rank
     * @param usernameInp the user's username
     * @param winIn the user's wins
     * @param gamesLeftInp the user's games left to play in the tourney
     */
    private void addToLeaderBoardPane(String rankIn, String usernameInp, String winIn, String gamesLeftInp) {
        if (Integer.parseInt(rankIn) <= 10) { // only adds top 5 to leaderboard
            final Label rank = new Label(rankIn);
            final Label username = new Label(usernameInp);
            final Label wins = new Label(winIn);
            final Label gamesLeft = new Label(gamesLeftInp);

            leaderboardPane.add(rank, 0, Integer.parseInt(rankIn));
            leaderboardPane.add(username, 1, Integer.parseInt(rankIn));
            leaderboardPane.add(wins, 2, Integer.parseInt(rankIn));
            leaderboardPane.add(gamesLeft, 3, Integer.parseInt(rankIn));
        }
    }
    /**
     * Method for adding all the user's stats to the user stats pane
     *
     * @param rankInp the user's rank
     * @param usernameInp the user's username
     * @param winIn the user's wins
     * @param gamesLeftInp the user's games left to play in the tourney
     */
    private void addToUserPane(String rankInp, String usernameInp, String winIn, String gamesLeftInp) {
        final Label rank = new Label(rankInp);
        final Label username = new Label(usernameInp);
        final Label wins = new Label(winIn);
        final Label gamesLeft = new Label(gamesLeftInp);

        userPane.add(rank, 0, 1);
        userPane.add(username, 1, 1);
        userPane.add(wins, 2, 1);
        userPane.add(gamesLeft, 3, 1);
    }

    /**
     * Method for updating the leader board
     *
     * @param rank the user's rank
     * @param username the user's username
     * @param winCount the user's wins
     * @param gamesLeftCount the user's games left to play in the tourney
     */
    @Override
    public void updateTSLeader(String rank, String username, String winCount, String gamesLeftCount) {
        Platform.runLater(() -> addToLeaderBoardPane(rank, username, winCount, gamesLeftCount));
    }
    /**
     * Method for updating the user's stats
     *
     * @param rank the user's rank
     * @param username the user's username
     * @param winCount the user's wins
     * @param gamesLeftCount the user's games left to play in the tourney
     */
    @Override
    public void updateTSUser(String rank, String username, String winCount, String gamesLeftCount) {
        Platform.runLater(() -> addToUserPane(rank, username, winCount, gamesLeftCount));
    }


}
