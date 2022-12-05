import java.io.File;

public class TournamentGame extends OneVsOne {

    public TournamentGame(int matchTime, File filePath, int fileIndex) {
        super(matchTime, filePath, fileIndex);
        setGamemode("Tournament");
    }

    public TournamentGame(int matchTime) {
        super(matchTime);
        setGamemode("Tournament");
    }
}
