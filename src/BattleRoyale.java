import java.io.File;

public class BattleRoyale extends Game {
    public BattleRoyale(int matchTime, int countDownTime, File filePath, int fileIndex) {
        super(matchTime, filePath, fileIndex);
        setCountDownTime(countDownTime);
        setGamemode("BattleRoyale");
    }

    public BattleRoyale(int matchTime, int countDownTime) {
        super(matchTime);
        setCountDownTime(countDownTime);
        setGamemode("BattleRoyale");
    }

    @Override
    public void pregameLobby() throws InterruptedException {
        while (!isInProgress()) {
            Thread.sleep(10);
            if (getNumConnectedClients() == 3) {
                System.out.println("ENOUGH PlAYErS FOUND, STARTING COUNTDOWN TIMER");
                setPreGameLobbyFlag(true);
                setLobbyStartTime(System.currentTimeMillis());
                while (((System.currentTimeMillis() - getLobbyStartTime()) / 1000) < getCountDownTime()) ;
                System.out.println("TIMER FINISHED");
                changeProgressFlag();
                changeStartFlag();
                startGame();
            }
        }
    }

    @Override
    public void startGame() {
        setPreGameLobbyFlag(false);
        long startTime = System.currentTimeMillis();
        while (((System.currentTimeMillis() - startTime) / 1000) < getMatchTime()) ;
        changeEndFlag();
    }
}