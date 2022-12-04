import java.io.File;

public class OneVsOne extends Game {

    public OneVsOne(File filePath, int fileIndex) {
        super(filePath, fileIndex);
        setGamemode("OneVsOne");
    }

    public OneVsOne() {
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
            while (((System.currentTimeMillis() - startTime) / 1000) < 60) ;
            System.out.println("MATCH ENDED");
            changeEndFlag();
        }
    }
}
