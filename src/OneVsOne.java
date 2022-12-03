public class OneVsOne extends Game {

    public OneVsOne() {
        System.out.println("IN 1v1 CONSTRUCTOR");
        setGamemode("OneVsOne");
    }

    @Override
    public void pregameLobby() throws InterruptedException {
        System.out.println("IN 1v1 PREGAME");
        while (!isInProgress()) {
            Thread.sleep(10);
            if (getNumConnectedClients() == 2) {
                System.out.println("CHANGED FLAG");
                changeProgressFlag();
                startGame();
            }
        }
    }

    @Override
    public void startGame() {
        if(!isFinished()){
            System.out.println("STARTING MATCH TIMER");
            long startTime = System.currentTimeMillis();
            while (((System.currentTimeMillis() - startTime) / 1000) < 5) ;
            System.out.println("MATCH ENDED");
            changeEndFlag();
        }
    }
}
