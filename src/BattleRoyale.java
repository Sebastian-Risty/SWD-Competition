import java.io.File;

public class BattleRoyale extends Game {

    private boolean preGameLobbyFlag = false;
    private long lobbyStartTime;

    public boolean getPreGameLobbyFlag() {
        return preGameLobbyFlag;
    }

    public void setPreGameLobbyFlag(boolean preGameLobbyFlag) {
        this.preGameLobbyFlag = preGameLobbyFlag;
    }

    public long getLobbyStartTime() {
        return lobbyStartTime;
    }

    public void setLobbyStartTime(long lobbyStartTime) {
        this.lobbyStartTime = lobbyStartTime;
    }

    public BattleRoyale(File filePath, int fileIndex) {
        super(filePath, fileIndex);
        setGamemode("BattleRoyale");
    }

    public BattleRoyale() {
        setGamemode("BattleRoyale");
    }

    @Override
    public void pregameLobby() {
        while (!isInProgress()) {
            if (getNumConnectedClients() == 3) {
                preGameLobbyFlag = true;
                lobbyStartTime = System.currentTimeMillis();
                while (((System.currentTimeMillis() - lobbyStartTime) / 1000) < getCountDownTime()) ;
                changeProgressFlag();
            }
        }
    }

    @Override
    public void startGame() {
        preGameLobbyFlag = false;
        long startTime = System.currentTimeMillis();
        while (((System.currentTimeMillis() - startTime) / 1000) < 69) ;
        changeProgressFlag();
        changeEndFlag();
    }
}
// TODO
// when adding player to lobby, send to client numPLayers connected
// if pregame flag is false, dont send time
// if true, send lobbyStartTime
//