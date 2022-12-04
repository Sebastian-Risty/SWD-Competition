import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Controller {
    private static String ip;
    private static int port;

    private static Client client;

    private static PlayerStats player;

    private static TournamentStats tournament;

    public double getxPos() {
        return xPos;
    }

    public void setxPos(double xPos) {
        Controller.xPos = xPos;
    }

    public double getyPos() {
        return yPos;
    }

    public void setyPos(double yPos) {
        Controller.yPos = yPos;
    }

    private static double xPos;
    private static double yPos;

    public static PlayerStats getPlayer() {
        return player;
    }

    public static void setPlayer(PlayerStats player) {
        Controller.player = player;
    }

    public static TournamentStats getTournament() {
        return tournament;
    }

    public static void setTournament(TournamentStats tournament) {
        Controller.tournament = tournament;
    }



    public void setIp(String ip) {
        Controller.ip = ip;
    }

    public void setPort(int port) {
        Controller.port = port;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public Client getClient() {
        return client;
    }

    public static void setClient(Client client) {
        Controller.client = client;
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        Controller.scene = scene;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        Controller.stage = stage;
    }

    public Parent getRoot() {
        return root;
    }

    public void setRoot(Parent root) {
        Controller.root = root;
    }

    private static Scene scene;
    private static Stage stage;
    private static Parent root;

    public void switchScene(String fxmlUrl, String sceneTitle) throws IOException {

        Stage temp2 = stage;

        setyPos(stage.getY());
        setxPos(stage.getX());
        Stage tempStage = new Stage();

        setStage(tempStage);

        URL fxmlFile = getClass().getResource(fxmlUrl);
        assert fxmlFile != null;
        setRoot(FXMLLoader.load(fxmlFile));
        setScene(new Scene(getRoot()));
        getStage().setScene(getScene());

        stage.setTitle(sceneTitle);
        stage.setX(getxPos());
        stage.setY(getyPos());
        getStage().show();

        temp2.close();
    }

    public void loginInvalid() {
    }

    public void loginValid() {
    }

    public void signUpValid() {
    }

    public void signUpInvalid() {
    }

    public void updateTimer(int time) {
    }

    public void gameStart() {
    }

    public void endGame() {
    }

    public void displayResults(String[] clientMessage) {
    }

    public void guessResult(int score) {
    }

    public void updatePlayerStats(String username, String totalWins, String totalGamesPlayed, String OVOWins, String OVOGamesPlayed,
                                  String BRWins, String BRGamesPlayed, String tournamentWins, String tournamentsPlayed) {
        player.setUsername(username);
        player.setTotalWins(totalWins);
        player.setTotalGamesPlayed(totalGamesPlayed);
        player.setOVOWins(OVOWins);
        player.setOVOGamesPlayed(OVOGamesPlayed);
        player.setBRWins(BRWins);
        player.setBRGamesPlayed(BRGamesPlayed);
        player.setTournamentWins(tournamentWins);
        player.setTournamentsPlayed(tournamentsPlayed);
    }

    public void updatePlayerStatsScreen() {
    }

    public void updateTSLeader(String rank, String username, String tournamentWins, String tournamentGamesLeft) {
        tournament.setRank(Integer.parseInt(rank));
        tournament.setUsername(username);
        tournament.setTournamentWins(Integer.parseInt(tournamentWins));
        tournament.setTournamentGamesLeft(Integer.parseInt(tournamentGamesLeft));
    }

    public void updateTSUser(String rank, String username, String tournamentWins, String tournamentGamesLeft) {
        tournament.setRank(Integer.parseInt(rank));
        tournament.setUsername(username);
        tournament.setTournamentWins(Integer.parseInt(tournamentWins));
        tournament.setTournamentGamesLeft(Integer.parseInt(tournamentGamesLeft));
    }


}
