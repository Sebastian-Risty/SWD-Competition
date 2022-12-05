import java.io.File;

/**
 * TournamentGame class that extends OneVsOne and defines a tournament game
 * @see OneVsOne
 */
public class TournamentGame extends OneVsOne {
    /**
     * Tournament name
     */
    String tournamentName;
    /**
     * Getter for tournament name
     * @return tournament name
     */
    public String getTournamentName() {
        return tournamentName;
    }
    /**
     * TournamentGame constructor
     * @param matchTime the match time
     * @param filePath the path to the word file
     * @param fileIndex the index in the word file
     * @param tournamentName the name of the tournament
     *
     */
    public TournamentGame(int matchTime, File filePath, int fileIndex, String tournamentName) {
        super(matchTime, filePath, fileIndex);
        setGamemode("Tournament");
        this.tournamentName = tournamentName;
    }

    /**
     * TournamentGame constructor with two inputs
     * @param matchTime the match time
     * @param tournamentName the name of the tournament
     */
    public TournamentGame(int matchTime, String tournamentName) {
        super(matchTime);
        setGamemode("Tournament");
        this.tournamentName = tournamentName;
    }
    /**
     * pregameLobby method that waits for two players to connect. Overrides the OneVsOne version
     * @throws InterruptedException exception
     */
    @Override
    public void pregameLobby() throws InterruptedException {
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
    /**
     * startGame method for starting the match timer and keeping track of the match time
     */
    @Override
    public void startGame() {
        if (!isFinished()) {
            System.out.println("STARTING MATCH TIMER");
            long startTime = System.currentTimeMillis();
            while (((System.currentTimeMillis() - startTime) / 1000) < getMatchTime()) ;
            System.out.println("MATCH ENDED");
            changeEndFlag();
        }
    }
}
