public class TournamentStats implements Comparable<TournamentStats> {
    private String username;
    private int tournamentGamesLeft;
    private int tournamentWins;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTournamentGamesLeft() {
        return tournamentGamesLeft;
    }

    public void setTournamentGamesLeft(int tournamentGamesLeft) {
        this.tournamentGamesLeft = tournamentGamesLeft;
    }

    public int getTournamentWins() {
        return tournamentWins;
    }

    public void setTournamentWins(int tournamentWins) {
        this.tournamentWins = tournamentWins;
    }


    public TournamentStats(String username, String tournamentWins, String tournamentGamesLeft) {
        this.username = username;
        this.tournamentWins = Integer.parseInt(tournamentWins);
        this.tournamentGamesLeft = Integer.parseInt(tournamentGamesLeft);
    }

    public TournamentStats(String username) {
        this.username = username;
        this.tournamentWins = 0;
        this.tournamentGamesLeft = 0;
    }

    @Override
    public int compareTo(TournamentStats tournamentStats) {
        if (this.tournamentWins == tournamentStats.tournamentWins) {
            return tournamentStats.tournamentGamesLeft - this.tournamentGamesLeft;
        } else {
            return tournamentStats.tournamentWins - this.tournamentWins;
        }
    }
}
