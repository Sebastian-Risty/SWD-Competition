import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.Arrays;

public class IndividualTournamentController extends Controller {

    @FXML
    private JFXButton readyUp;

    @FXML
    private Label gameModeFeedback;

    @FXML
    private Label gameStatus;

    @FXML
    private JFXButton mainMenuButton;

    @FXML
    private GridPane leaderboardPane;

    @FXML
    private GridPane userPane;

    private boolean readiedUp;

    public void initialize() {

        readyUp.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        mainMenuButton.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));

        boolean length = getTournamentData().length >= 17;
        System.out.println(Arrays.toString(getTournamentData()));
        if (length) {
            for (int i = 2; i < 15; i += 3) {
                addToLeaderBoardPane(String.valueOf(((i - 1) / 3)), getTournamentData()[i], getTournamentData()[i + 1], getTournamentData()[i + 2]);
            }
        } else {
            for (int i = 2; i < getTournamentData().length - 2; i += 3) {
                System.out.println(i);
                addToLeaderBoardPane(String.valueOf(((i - 1) / 3) + 1), getTournamentData()[i], getTournamentData()[i + 1], getTournamentData()[i + 2]);
            }
        }

        // username, wins, gamesleft

    }


    @FXML
    void mainMenuButtonListener(ActionEvent event) throws IOException {
        switchScene("homeScreenFXML.fxml", "Main Menu");
    }

    @FXML
    void readyUpListener(ActionEvent event) {
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

    public void initialize() {
        readyUp.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        mainMenuButton.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
    }

    private int getNumOnLeaderboardPane() {
        // https://stackoverflow.com/questions/20766363/get-the-number-of-rows-in-a-javafx-gridpane
        int numRows = leaderboardPane.getRowConstraints().size();
        for (int i = 0; i < leaderboardPane.getChildren().size(); i++) {
            Node child = leaderboardPane.getChildren().get(i);
            if (child.isManaged()) {
                Integer rowIndex = GridPane.getRowIndex(child);
                if (rowIndex != null) {
                    numRows = Math.max(numRows, rowIndex + 1);
                }
            }
        }
        return numRows;
    }


    private void addToLeaderBoardPane(String rankIn, String usernameInp, String winIn, String gamesLeftInp) {
        if (Integer.parseInt(rankIn) <= 5) { // only adds top 5 to leaderboard
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


    @Override
    public void updateTSLeader(String rank, String username, String winCount, String gamesLeftCount) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                addToLeaderBoardPane(rank, username, winCount, gamesLeftCount);
            }
        });
    }

    @Override
    public void updateTSUser(String rank, String username, String winCount, String gamesLeftCount) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                addToUserPane(rank, username, winCount, gamesLeftCount);
            }
        });
    }


}
