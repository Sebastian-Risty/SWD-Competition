import java.io.File;

/**
 * Represents specific rules for BR matches
 */
public class BattleRoyale extends Game {
    /**
     * BattleRoyale constructor
     * @param matchTime How long each match should last
     * @param countDownTime How long the lobby should wait for additional clients to join
     * @param filePath File object path
     * @param fileIndex Index to read from file
     *                  Calls super constructor
     */
    public BattleRoyale(int matchTime, int countDownTime, File filePath, int fileIndex) {
        super(matchTime, filePath, fileIndex);
        setCountDownTime(countDownTime);
        setGamemode("BattleRoyale");
    }

    /**
     * OneVsOne constructor
     * @param matchTime How long the match should be
     * @param countDownTime How long the lobby should wait for additional clients to join
     *                  Calls super constructor
     */
    public BattleRoyale(int matchTime, int countDownTime) {
        super(matchTime);
        setCountDownTime(countDownTime);
        setGamemode("BattleRoyale");
    }

    /**
     * Starts lobby countdown once 3 clients connect, once countdown finishes match starts
     * @throws InterruptedException If thread is interrupted
     */
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

    /**
     * Waits until match time to run down then ends the match
     */
    @Override
    public void startGame() {
        setPreGameLobbyFlag(false);
        long startTime = System.currentTimeMillis();
        while (((System.currentTimeMillis() - startTime) / 1000) < getMatchTime()) ;
        changeEndFlag();
    }
}