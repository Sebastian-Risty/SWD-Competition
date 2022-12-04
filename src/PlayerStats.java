public class PlayerStats {
    private String username;
    private String totalWins;
    private String totalGamesPlayed;
    private String OVOWins;
    private String OVOGamesPlayed;
    private String BRWins;
    private String BRGamesPlayed;
    private String tournamentWins;
    private String tournamentsPlayed;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTotalWins() {
        return totalWins;
    }

    public void setTotalWins(String totalWins) {
        this.totalWins = totalWins;
    }

    public String getTotalGamesPlayed() {
        return totalGamesPlayed;
    }

    public void setTotalGamesPlayed(String totalGamesPlayed) {
        this.totalGamesPlayed = totalGamesPlayed;
    }

    public String getOVOWins() {
        return OVOWins;
    }

    public void setOVOWins(String OVOWins) {
        this.OVOWins = OVOWins;
    }

    public String getOVOGamesPlayed() {
        return OVOGamesPlayed;
    }

    public void setOVOGamesPlayed(String OVOGamesPlayed) {
        this.OVOGamesPlayed = OVOGamesPlayed;
    }

    public String getBRWins() {
        return BRWins;
    }

    public void setBRWins(String BRWins) {
        this.BRWins = BRWins;
    }

    public String getBRGamesPlayed() {
        return BRGamesPlayed;
    }

    public void setBRGamesPlayed(String BRGamesPlayed) {
        this.BRGamesPlayed = BRGamesPlayed;
    }

    public String getTournamentWins() {
        return tournamentWins;
    }

    public void setTournamentWins(String tournamentWins) {
        this.tournamentWins = tournamentWins;
    }

    public String getTournamentsPlayed() {
        return tournamentsPlayed;
    }

    public void setTournamentsPlayed(String tournamentsPlayed) {
        this.tournamentsPlayed = tournamentsPlayed;
    }

    public PlayerStats(String username, String totalWins, String totalGamesPlayed, String OVOWins, String OVOGamesPlayed,
                       String BRWins, String BRGamesPlayed, String tournamentWins, String tournamentsPlayed) {
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
