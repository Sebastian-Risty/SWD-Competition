public class BattleRoyale extends Game {
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
