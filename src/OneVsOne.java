import java.io.File;

/**
 * Represents specific rules for head on head matches
 */
@SuppressWarnings({"BusyWait", "StatementWithEmptyBody"})
public class OneVsOne extends Game {

    /**
     * OneVsOne constructor
     *
     * @param matchTime How long each match should last
     * @param filePath  File object path
     * @param fileIndex Index to read from file
     *                  Calls super constructor
     */
    public OneVsOne(int matchTime, File filePath, int fileIndex) {
        super(matchTime, filePath, fileIndex);
        setGamemode("OneVsOne");
    }

    /**
     * OneVsOne constructor
     *
     * @param matchTime How long the match should be
     *                  Calls super constructor
     */
    public OneVsOne(int matchTime) {
        super(matchTime);
        setGamemode("OneVsOne");
    }

    /**
     * Starts the match once 2 clients are connected
     *
     * @throws InterruptedException If thread is interrupted
     */
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

    /**
     * Waits for match time then finished the match
     */
    @Override
    public void startGame() {
        if (!isFinished()) {
            long startTime = System.currentTimeMillis();
            while (((System.currentTimeMillis() - startTime) / 1000) < getMatchTime()) ;
            changeEndFlag();
        }
    }

}
