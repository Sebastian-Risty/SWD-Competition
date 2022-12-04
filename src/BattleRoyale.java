import java.io.File;

public class BattleRoyale extends Game {
    public BattleRoyale(File filePath, int fileIndex) {
        super(filePath, fileIndex);
        setGamemode("BattleRoyale");
    }

    public BattleRoyale() {
        setGamemode("BattleRoyale");
    }

    @Override
    public void pregameLobby() {
        while (!isInProgress()) {
            if (getNumConnectedClients() == 3) {
                System.out.println("ENOUGH PlAYErS FOUND, STARTING COUNTDOWN TIMER");
                setPreGameLobbyFlag(true);
                setLobbyStartTime(System.currentTimeMillis());
                while (((System.currentTimeMillis() - getLobbyStartTime()) / 1000) < getCountDownTime()) ;
                System.out.println("TIMER FINISHED");
                changeProgressFlag();
                startGame();
            }
        }
    }

    @Override
    public void startGame() {
        setPreGameLobbyFlag(false);
        long startTime = System.currentTimeMillis();
        System.out.println("STARTING GAME TIMER");
        while (((System.currentTimeMillis() - startTime) / 1000) < getMatchTime()) ;
        System.out.println("GAME FINISHED");
        changeEndFlag();
    }
}