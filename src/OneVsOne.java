import java.io.File;

public class OneVsOne extends Game {

    public OneVsOne(int matchTime, File filePath, int fileIndex) {
        super(matchTime, filePath, fileIndex);
        setGamemode("OneVsOne");
    }

    public OneVsOne(int matchTime) {
        super(matchTime);
        setGamemode("OneVsOne");
    }

    @Override
    public void pregameLobby() throws InterruptedException {
        while (!isInProgress()) {
            Thread.sleep(10);
            if (getNumConnectedClients() == 2) {
                changeProgressFlag();
                changeStartFlag();
                startGame();
            }
        }
    }

    @Override
    public void startGame() {
        if (!isFinished()) {
            long startTime = System.currentTimeMillis();
            while (((System.currentTimeMillis() - startTime) / 1000) < getMatchTime()) ;
            changeEndFlag();
        }
    }

}
