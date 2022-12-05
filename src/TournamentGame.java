import java.io.File;

public class TournamentGame extends OneVsOne {

    String tournamentName;

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

    public String getTournamentName() {
        return tournamentName;
    }
}
