public class PlayerStats {
    PlayerStats(int score, int numGamesPlayed, int h2hWins, int tourneyWins, String tier, int rank) {

    }

    PlayerStats(){
        setScore(0);
        setNumGamesPlayed(0);
        setH2hWins(0);
        setTourneyWins(0);
        setTier("Terrible");
        setRank(0);
    }


    public int getScore() {
        return Score;
    }

    public void setScore(int score) {
        Score = score;
    }

    public int getNumGamesPlayed() {
        return numGamesPlayed;
    }

    public void setNumGamesPlayed(int numGamesPlayed) {
        this.numGamesPlayed = numGamesPlayed;
    }

    public int getH2hWins() {
        return h2hWins;
    }

    public void setH2hWins(int h2hWins) {
        this.h2hWins = h2hWins;
    }

    public int getTourneyWins() {
        return tourneyWins;
    }

    public void setTourneyWins(int tourneyWins) {
        this.tourneyWins = tourneyWins;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    private int Score;
    private int numGamesPlayed;
    private int h2hWins;
    private int tourneyWins;
    private String tier;
    private int rank;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String username;
}
