public class OneVsOne extends Game {

    public OneVsOne() {
        System.out.println("IN 1v1 CONSTRUCTOR");
        setGamemode("OneVsOne");
    }

    @Override
    public void pregameLobby() {
        System.out.println("IN 1v1 PREGAME");
        while (!isInProgress()) {
            if (getNumConnectedClients() == 2) {
                System.out.println("CHANGED FLAG");
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
