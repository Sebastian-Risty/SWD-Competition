public class PlayerStats {
    private String username;
    private int totalWins;
    private int totalGamesPlayed;
    private int OVOWins;
    private int OVOGamesPlayed;
    private int BRWins;
    private int BRGamesPlayed;
    private int tournamentWins;
    private int tournamentsPlayed;

    public PlayerStats(String username, int totalWins, int totalGamesPlayed, int OVOWins, int OVOGamesPlayed,
                       int BRWins, int BRGamesPlayed, int tournamentWins, int tournamentsPlayed) {
        this.username = username;
        this.totalWins = totalWins;
        this.totalGamesPlayed = totalGamesPlayed;
        this.OVOWins = OVOWins;
        this.OVOGamesPlayed = OVOGamesPlayed;
        this.BRWins = BRWins;
        this.BRGamesPlayed = BRGamesPlayed;
        this.tournamentWins = tournamentWins;
        this.tournamentsPlayed = tournamentsPlayed;
    }

    public PlayerStats(String username) {
        this.username = username;
        this.totalWins = 0;
        this.totalGamesPlayed = 0;
        this.OVOWins = 0;
        this.OVOGamesPlayed = 0;
        this.BRWins = 0;
        this.BRGamesPlayed = 0;
        this.tournamentWins = 0;
        this.tournamentsPlayed = 0;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTotalWins() {
        return totalWins;
    }

    public void setTotalWins(int totalWins) {
        this.totalWins = totalWins;
    }

    public int getTotalGamesPlayed() {
        return totalGamesPlayed;
    }

    public void setTotalGamesPlayed(int totalGamesPlayed) {
        this.totalGamesPlayed = totalGamesPlayed;
    }

    public int getOVOWins() {
        return OVOWins;
    }

    public void setOVOWins(int OVOWins) {
        this.OVOWins = OVOWins;
    }

    public int getOVOGamesPlayed() {
        return OVOGamesPlayed;
    }

    public void setOVOGamesPlayed(int OVOGamesPlayed) {
        this.OVOGamesPlayed = OVOGamesPlayed;
    }

    public int getBRWins() {
        return BRWins;
    }

    public void setBRWins(int BRWins) {
        this.BRWins = BRWins;
    }

    public int getBRGamesPlayed() {
        return BRGamesPlayed;
    }

    public void setBRGamesPlayed(int BRGamesPlayed) {
        this.BRGamesPlayed = BRGamesPlayed;
    }

    public int getTournamentWins() {
        return tournamentWins;
    }

    public void setTournamentWins(int tournamentWins) {
        this.tournamentWins = tournamentWins;
    }

    public int getTournamentsPlayed() {
        return tournamentsPlayed;
    }

    public void setTournamentsPlayed(int tournamentsPlayed) {
        this.tournamentsPlayed = tournamentsPlayed;
    }

}
