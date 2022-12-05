/**
 * Stores information related to tournament
 *
 * @see java.lang.Runnable
 */
public class Tournament implements Runnable {
    /**
     * Name of the tournament
     */
    private final String name;
    /**
     * Time the tournament started
     */
    private final long startTime;
    /**
     * false if the tournament is running, true if not
     */
    private boolean endFlag = false;

    /**
     * Sets tournament name and start time
     *
     * @param name      what name will be set to
     * @param startTime what startTime will be set to
     */
    public Tournament(String name, String startTime) {
        this.name = name;
        this.startTime = Long.parseLong(startTime);
    }

    /**
     * Sets tournament name
     *
     * @param name name's value
     */
    public Tournament(String name) {
        this.name = name;
        startTime = System.currentTimeMillis();
    }

    /**
     * returns name
     *
     * @return name of tournament
     */
    public String getName() {
        return name;
    }

    /**
     * returns true if tournament is still running, false if not
     *
     * @return endFlag endFlag value
     */
    public boolean checkEndFlag() {
        return endFlag;
    }

    /**
     * Run method waits from tournament start time until designated time, then ends tournament
     */
    @Override
    public void run() {
        long hoursLive = 2;
        while (((System.currentTimeMillis() - startTime) / 1000) < (hoursLive * 60 * 60)) ;
        endFlag = true;
    }
}
