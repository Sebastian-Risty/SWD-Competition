import java.io.File;

public class TournamentGame extends OneVsOne {

    String tournamentName;

    public String getTournamentName() {
        return tournamentName;
    }

    public TournamentGame(int matchTime, File filePath, int fileIndex, String tournamentName) {
        super(matchTime, filePath, fileIndex);
        setGamemode("Tournament");
        this.tournamentName = tournamentName;
    }

    public TournamentGame(int matchTime, String tournamentName) {
        super(matchTime);
        setGamemode("Tournament");
        this.tournamentName = tournamentName;
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
