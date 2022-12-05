public class Tournament implements Runnable {
    private final long hoursLive = 2;
    private final String name;
    private final long startTime;
    private boolean endFlag = false;

    public Tournament(String name, String startTime) {
        this.name = name;
        this.startTime = Long.parseLong(startTime);
    }

    public Tournament(String name) {
        this.name = name;
        startTime = System.currentTimeMillis();
    }

    public String getName() {
        return name;
    }

    public boolean checkEndFlag() {
        return endFlag;
    }

    @Override
    public void run() {
        while (((System.currentTimeMillis() - startTime) / 1000) < (hoursLive * 60 * 60)) ;
        endFlag = true;
    }
}
