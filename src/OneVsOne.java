public class OneVsOne extends Game {

    @Override
    public void pregameLobby() {
        while (!isInProgress()) {
            if (getNumConnectedClients() == 2) {
                changeProgressFlag();
            }
        }
    }

    @Override
    public void startGame() {
        long startTime = System.currentTimeMillis();
        while (((System.currentTimeMillis() - startTime) / 1000) < 5) ;
        changeProgressFlag();
        changeEndFlag();
    }
}
