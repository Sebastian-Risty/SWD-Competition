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

    public static PlayerStats getPlayer() {
        return player;
    }

    public static void setPlayer(PlayerStats player) {
        Controller.player = player;
    }

    private static PlayerStats player;

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
        this.scene = scene;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Parent getRoot() {
        return root;
    }

    public void setRoot(Parent root) {
        this.root = root;
    }

    private static Scene scene;
    private static Stage stage;
    private static Parent root;

    public void switchScene(String fxmlUrl, String sceneTitle) throws IOException {

        Stage temp2 = stage;

        Stage tempStage = new Stage();

        setStage(tempStage);

        URL fxmlFile = getClass().getResource(fxmlUrl);
        assert fxmlFile != null;
        setRoot(FXMLLoader.load(fxmlFile));
        setScene(new Scene(getRoot()));
        getStage().setScene(getScene());

        stage.setTitle(sceneTitle);
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

    public void gameStart() {

    }

    public void endGame() {
    }

    public void displayResults(String[] results) {

    }

    public void getLetters(String letters) {

    }

    public void updatePlayerStats(String username, String totalWins, String totalGamesPlayed, String OVOWins, String OVOGamesPlayed,
                                  String BRWins, String BRGamesPlayed, String tournamentWins, String tournamentsPlayed) {
        updatePlayerStatsHelper(username, totalWins, totalGamesPlayed, OVOWins, OVOGamesPlayed, BRWins, BRGamesPlayed, tournamentWins, tournamentsPlayed);
    }

    public void updatePlayerStatsHelper(String username, String totalWins, String totalGamesPlayed, String OVOWins, String OVOGamesPlayed,
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

}
