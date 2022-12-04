import java.io.File;

public class BattleRoyale extends Game {

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
                long startTime = System.currentTimeMillis();
                while (((System.currentTimeMillis() - startTime) / 1000) < 30) ;
                changeProgressFlag();
            }
        }
    }

    @Override
    public void startGame() {
        long startTime = System.currentTimeMillis();
        while (((System.currentTimeMillis() - startTime) / 1000) < 30) ;
        changeProgressFlag();
        changeEndFlag();
    }
}
