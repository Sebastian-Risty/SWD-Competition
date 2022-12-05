import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
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
    private GridPane leaderboardPane;
    @FXML
    private GridPane userPane;
    @FXML
    private JFXButton startButton;
    @FXML
    private JFXButton mainMenuButton;

    public void initialize() {

        startButton.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        mainMenuButton.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        int j = 0;
        while(j<getTournamentData().length - 2) {

        }

        boolean length = getTournamentData().length >= 17;
        System.out.println(Arrays.toString(getTournamentData()));

        if(length) {
            for(int i = 2; i<15; i+=3) {
                if(getTournamentData()[i].equals(getPlayer().getUsername())) {
                    addToUserPane(String.valueOf(((i-1)/3)+1), getTournamentData()[i], getTournamentData()[i+1], getTournamentData()[i+2]);
                }
                addToLeaderBoardPane(String.valueOf(((i-1)/3)), getTournamentData()[i], getTournamentData()[i+1], getTournamentData()[i+2]);
            }
        }
        else {
            for(int i = 2; i<getTournamentData().length-2; i+=3) {
                System.out.println(i);
                if(getTournamentData()[i].equals(getPlayer().getUsername())) {
                    addToUserPane(String.valueOf(((i-1)/3)+1), getTournamentData()[i], getTournamentData()[i+1], getTournamentData()[i+2]);
                }
                addToLeaderBoardPane(String.valueOf(((i-1)/3)+1), getTournamentData()[i], getTournamentData()[i+1], getTournamentData()[i+2]);
            }
        }

        // username, wins, gamesleft

    }

    @FXML
    public void startButtonListener(){

    }

    @FXML
    public void menuButtonListener() throws IOException {
        switchScene("homeScreenFXML.fxml", "Main Menu");
    }

    private int getNumOnLeaderboardPane() {
        // https://stackoverflow.com/questions/20766363/get-the-number-of-rows-in-a-javafx-gridpane
        int numRows = leaderboardPane.getRowConstraints().size();
        for (int i = 0; i < leaderboardPane.getChildren().size(); i++) {
            Node child = leaderboardPane.getChildren().get(i);
            if (child.isManaged()) {
                Integer rowIndex = GridPane.getRowIndex(child);
                if(rowIndex != null){
                    numRows = Math.max(numRows,rowIndex+1);
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
                addToLeaderBoardPane(rank,username,winCount,gamesLeftCount);
            }
        });
    }

    @Override
    public void updateTSUser(String rank, String username, String winCount, String gamesLeftCount) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                addToUserPane(rank,username,winCount,gamesLeftCount);
            }
        });
    }


}
