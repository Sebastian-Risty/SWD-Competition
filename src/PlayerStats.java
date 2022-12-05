/**
 * Holds information needed for player statistics
 */
public class PlayerStats {
    /**
     * Player's username
     */
    private String username;
    /**
     * Number of wins
     */
    private String totalWins;
    /**
     * Number of games played
     */
    private String totalGamesPlayed;
    /**
     * Number of one vs one (head to head) wins
     */
    private String OVOWins;
    /**
     * Number of one vs one games played
     */
    private String OVOGamesPlayed;
    /**
     * Number of battle royal wins
     */
    private String BRWins;
    /**
     * Number of battle royal games played
     */
    private String BRGamesPlayed;
    /**
     * Number of tournament wins
     */
    private String tournamentWins;
    /**
     * Number of tournament games played
     */
    private String tournamentsPlayed;

    /**
     * Returns username
     * @return username value
     */
    public String getUsername() {
        return username;
    }

    /**
     * sets username
     * @param username username value
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * returns total wins
     * @return totalWins value
     */
    public String getTotalWins() {
        return totalWins;
    }

    /**
     * sets total wins
     * @param totalWins total wins value
     */
    public void setTotalWins(String totalWins) {
        this.totalWins = totalWins;
    }

    /**
     * returns total games played
     * @return totalGamesPlayed value
     */
    public String getTotalGamesPlayed() {
        return totalGamesPlayed;
    }

    /**
     * sets total games played
     * @param totalGamesPlayed totalGamesPlayed value
     */
    public void setTotalGamesPlayed(String totalGamesPlayed) {
        this.totalGamesPlayed = totalGamesPlayed;
    }

    /**
     * returns one vs one wins
     * @return OVOWins value
     */
    public String getOVOWins() {
        return OVOWins;
    }

    /**
     * sets one vs one wins
     * @param OVOWins OVOWins value
     */
    public void setOVOWins(String OVOWins) {
        this.OVOWins = OVOWins;
    }

    /**
     * returns one vs one games played
     * @return OVOGamesPlayed
     */
    public String getOVOGamesPlayed() {
        return OVOGamesPlayed;
    }

    /**
     * sets one vs one games played
     * @param OVOGamesPlayed OVOGamesPlayed value
     */
    public void setOVOGamesPlayed(String OVOGamesPlayed) {
        this.OVOGamesPlayed = OVOGamesPlayed;
    }

    /**
     * Returns battle royal wins
     * @return BRWins value
     */
    public String getBRWins() {
        return BRWins;
    }

    /**
     * Sets battle royal wins
     * @param BRWins BRWins value
     */
    public void setBRWins(String BRWins) {
        this.BRWins = BRWins;
    }

    /**
     * Returns battle royal games played
     * @return BRGamesPlayed value
     */
    public String getBRGamesPlayed() {
        return BRGamesPlayed;
    }

    /**
     * Sets battle royal games played
     * @param BRGamesPlayed BRGamesPlayed value
     */
    public void setBRGamesPlayed(String BRGamesPlayed) {
        this.BRGamesPlayed = BRGamesPlayed;
    }

    /**
     * Returns tournament wins
     * @return tournament wins value
     */
    public String getTournamentWins() {
        return tournamentWins;
    }

    /**
     * Sets tournament wins
     * @param tournamentWins tournament wins value
     */
    public void setTournamentWins(String tournamentWins) {
        this.tournamentWins = tournamentWins;
    }

    /**
     * Returns number of tournaments played
     * @return tournamentsPlayed
     */
    public String getTournamentsPlayed() {
        return tournamentsPlayed;
    }

    /**
     * Sets tournaments played
     * @param tournamentsPlayed tournaments played value
     */
    public void setTournamentsPlayed(String tournamentsPlayed) {
        this.tournamentsPlayed = tournamentsPlayed;
    }

    /**
     * Sets username to input, all other parameters to "0"
     * @param username username value
     */
    public PlayerStats(String username) {
        this.username = username;
        this.totalWins = "0";
        this.totalGamesPlayed = "0";
        this.OVOWins = "0";
        this.OVOGamesPlayed = "0";
        this.BRWins = "0";
        this.BRGamesPlayed = "0";
        this.tournamentWins = "0";
        this.tournamentsPlayed = "0";
    }

}
