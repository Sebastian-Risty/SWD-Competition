public class OneVsOne extends Game {

    @Override
    public void pregameLobby() {
        while (!isInProgress()) {
            if (getConnectedClients() == 2) {
                changeProgress();
            }
        }
    }

    @Override
    public void startGame() {
        while (isInProgress()) {

        }
    }

    @Override
    public void closeLobby() {

    }
}
