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
        System.out.println("IN 1v1 PREGAME");
        while (!isInProgress()) {
            Thread.sleep(10);
            if (getNumConnectedClients() == 2) {
                System.out.println("CHANGED FLAG");
                changeProgressFlag();
                changeStartFlag();
                startGame();
            }
        }
    }

    @Override
    public void startGame() {
        if(!isFinished()){
            System.out.println("STARTING MATCH TIMER");
            long startTime = System.currentTimeMillis();
            while (((System.currentTimeMillis() - startTime) / 1000) < getMatchTime()) ;
            System.out.println("MATCH ENDED");
            changeEndFlag();
        }
    }
}
