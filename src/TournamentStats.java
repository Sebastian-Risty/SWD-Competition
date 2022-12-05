/**
 * Contains logic needed to maintain the statistics of a tournament
 *
 * @see java.lang.Comparable
 */
public class TournamentStats implements Comparable<TournamentStats> {
    /**
     * Name of tournament
     */
    private String username;
    /**
     * Number of games the user has left in the tournament
     */
    private int tournamentGamesLeft;
    /**
     * Number of tournament games won
     */
    private int tournamentWins;

    /**
     * Sets username, tournamentWins, and tournamentGamesLeft
     *
     * @param username            username value
     * @param tournamentWins      tournamentWins value
     * @param tournamentGamesLeft tournamentGamesLeft value
     */
    public TournamentStats(String username, String tournamentWins, String tournamentGamesLeft) {
        this.username = username;
        this.tournamentWins = Integer.parseInt(tournamentWins);
        this.tournamentGamesLeft = Integer.parseInt(tournamentGamesLeft);
    }

    /**
     * Sets username, sets tournamentWins and tournamentGamesLeft to 0
     *
     * @param username username value
     */
    public TournamentStats(String username) {
        this.username = username;
        this.tournamentWins = 0;
        this.tournamentGamesLeft = 5;
    }

    /**
     * returns username
     *
     * @return username value
     */
    public String getUsername() {
        return username;
    }

    /**
     * sets username
     *
     * @param username username value
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * returns tournament games left
     *
     * @return tournamentGamesLeft
     */
    public int getTournamentGamesLeft() {
        return tournamentGamesLeft;
    }

    /**
     * sets tournament games left
     *
     * @param tournamentGamesLeft tournament games left
     */
    public void setTournamentGamesLeft(int tournamentGamesLeft) {
        this.tournamentGamesLeft = tournamentGamesLeft;
    }

    /**
     * returns tournament games won
     *
     * @return tournamentWins
     */
    public int getTournamentWins() {
        return tournamentWins;
    }

    /**
     * sets tournament wins
     *
     * @param tournamentWins tournament wins value
     */
    public void setTournamentWins(int tournamentWins) {
        this.tournamentWins = tournamentWins;
    }

    /**
     * Compares two tournamentStats objects
     *
     * @param tournamentStats the tournamentStats object to be compares with
     * @return Positive if inputted object has more wins, negative if not. If equal, returns positive if inputted
     * object has more tournament games left, negative if not
     */
    @Override
    public int compareTo(TournamentStats tournamentStats) {
        if (this.tournamentWins == tournamentStats.tournamentWins) {
            return tournamentStats.tournamentGamesLeft - this.tournamentGamesLeft;
        } else {
            return tournamentStats.tournamentWins - this.tournamentWins;
        }
    }
}
