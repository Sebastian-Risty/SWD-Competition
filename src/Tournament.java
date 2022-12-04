public class Tournament implements Runnable {

    private final long hoursLive = 2;
    private long startTime;
    private boolean endFlag = false;

    public boolean checkStatus() {
        return endFlag;
    }

    public Tournament(long startTime) {
        this.startTime = startTime;
    }

    public Tournament() {
        startTime = System.currentTimeMillis();
    }

    @Override
    public void run() {
        while (((System.currentTimeMillis() - startTime) / 1000) < (hoursLive * 60 * 60)) ;
        endFlag = true;
    }
}
