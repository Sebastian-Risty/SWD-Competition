import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Game contains the base implementation of a match. Subclasses contain specific rule sets for the game mode
 */

public abstract class Game implements Runnable {
    /**
     * score corresponding to each letter position in alphabet
     */
    final static int[] alphabetScores = {1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10};
    /**
     * The name of the current game mode
     */
    private String gamemode;
    /**
     * The file object containing the list of scrambled letters
     * Will stay null if not used
     */
    private File filePath = null;
    /**
     * The index / line to grab a scramble from
     * If index is greater than indeces / lines, starts at first line again
     */
    private int fileIndex;
    /**
     * The selected word taken from wordsToChooseFrom
     */
    private String selectedWord;
    /**
     * Final List of letters used to make guesses
     */
    private final ArrayList<Character> letters = new ArrayList<>();
    /**
     * The list of words that can be used to generate the first part of letter scrambles.
     * If scrambles are read from a file, each line is stored as an element here.
     */
    ArrayList<String> wordsToChooseFrom = new ArrayList<>();
    /**
     * List of words that can be created from generated letters
     */
    private ArrayList<String> validWords = new ArrayList<>();
    /**
     * List of all words read in from words file
     */
    private final ArrayList<String> allWords = new ArrayList<>();
    /**
     * Frequency of each letter corresponding to each letter position in alphabet
     */
    private int[] letterFreq = new int[26];
    /**
     * True if the match is currently in progress
     */
    private boolean progressFlag = false;
    /**
     * True if the match has ended
     */
    private boolean endFlag = false;
    /**
     * True if the match has started
     */
    private boolean startFlag = false;
    /**
     * The number of clients currently connected to this game's lobby
     */
    private int numConnectedClients = 0;
    /**
     * The time to wait to allow more clients to join before match starts
     */
    private int countDownTime;
    /**
     * Flag for pre game lobby
     */
    private volatile boolean preGameLobbyFlag = false;
    /**
     * The time the lobby timer started
     */
    private long lobbyStartTime;
    /**
     * The time to wait before ending a match after it starts
     */
    private final int matchTime;

    /**
     * Getter for match time
     *
     * @return match time
     */
    public int getMatchTime() {
        return matchTime;
    }

    /**
     * Getter for pregame lobby flag
     *
     * @return the pregame lobby flag
     */
    public boolean getPreGameLobbyFlag() {
        return preGameLobbyFlag;
    }

    /**
     * Setter for pregame lobby flag
     *
     * @param preGameLobbyFlag flag for pregame lobby
     */
    public void setPreGameLobbyFlag(boolean preGameLobbyFlag) {
        this.preGameLobbyFlag = preGameLobbyFlag;
    }

    /**
     * Getter for lobby start time
     *
     * @return lobby start time
     */
    public long getLobbyStartTime() {
        return lobbyStartTime;
    }

    /**
     * Setter for lobby start time
     *
     * @param lobbyStartTime the lobby start time
     */
    public void setLobbyStartTime(long lobbyStartTime) {
        this.lobbyStartTime = lobbyStartTime;
    }

    /**
     * Getter for countdomn time
     *
     * @return countdown time
     */
    public int getCountDownTime() {
        return countDownTime;
    }

    /**
     * Setter for countdown time
     *
     * @param countDownTime the time count down
     */
    public void setCountDownTime(int countDownTime) {
        this.countDownTime = countDownTime;
    }

    /**
     * Boolean method for game in progress
     */
    public boolean isInProgress() {
        return progressFlag;
    }

    /**
     * Boolean method for game finished
     */
    public boolean isFinished() {
        return endFlag;
    }

    /**
     * Game constructor
     *
     * @param matchTime How long the match should be
     */
    public Game(int matchTime) {
        this.matchTime = matchTime;
    }

    /**
     * Boolean method for game start
     */
    public boolean hasStarted() {
        return startFlag;
    }

    /**
     * Game constructor
     *
     * @param matchTime How long the match should be
     * @param filePath  File object for scramble file
     * @param fileIndex Current index to read from scramble file
     */
    public Game(int matchTime, File filePath, int fileIndex) {
        this.matchTime = matchTime;
        this.filePath = filePath;
        this.fileIndex = fileIndex;
    }

    /**
     * Getter for the game mode
     *
     * @return game mode
     */
    public String getGamemode() {
        return gamemode;
    }

    /**
     * Setter for the game mode
     *
     * @param gamemode the game mode
     */
    public void setGamemode(String gamemode) {
        this.gamemode = gamemode;
    }

    /**
     * Getter for the letters list
     *
     * @return the letters list
     */
    public ArrayList<Character> getLetters() {
        return letters;
    }

    /**
     * Getter for number of connected clients
     *
     * @return number of connected clients
     */
    public int getNumConnectedClients() {
        return numConnectedClients;
    }

    /**
     * Method for changing the progress flag
     */
    public void changeProgressFlag() {
        progressFlag = !progressFlag;
    }

    /**
     * Method for changing the end flag
     */
    public void changeEndFlag() {
        endFlag = !endFlag;
    }

    /**
     * method for changing the start flag
     */
    public void changeStartFlag() {
        startFlag = !startFlag;
    }

    /**
     * Method for decrementing number of connected clients when one disconnects
     */
    public void clientDisconnected() {
        numConnectedClients--;
    }

    /**
     * Increments client count when client connects
     */
    public void clientConnected() {
        numConnectedClients++;
    }

    /**
     * builds game differently depending on if File object exists
     */
    @Override
    public void run() {
        if (filePath == null) {
            normalGame();
        } else {
            TAGame();
        }
        try {
            pregameLobby();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        startGame();
    }

    /**
     * Generate first portion of letters from an existing word
     * Add additional randomly generated letters
     * Scramble the letters
     */
    private void normalGame() {
        readAllWords();
        selectWord();
        generateLetters();
        addAdditionalLetters();
        Collections.sort(letters);
        findValidWords();
    }

    /**
     * Read from word file and display it as read
     */
    private void TAGame() {
        readLetterFile();
        readAllWords();
        selectedWord = wordsToChooseFrom.get(fileIndex);
        generateLetters();
        findValidWords();
    }

    /**
     * Read lines from scramble file
     */
    private void readLetterFile() {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            String line = reader.readLine();
            while (line != null) {
                wordsToChooseFrom.add(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read lines from words file
     */
    private void readAllWords() {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("words"));
            String line = reader.readLine();
            while (line != null) {
                if (line.length() > 2 && line.length() < 9) {
                    allWords.add(line);
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Build list of words to choose from of words of length > 5
     * Pick a random word from the list and update selected word
     */
    private void selectWord() {
        for (String word : allWords) {
            if (word.length() > 5) {
                wordsToChooseFrom.add(word);
            }
        }
        int selectedIndex = (int) (Math.random() * wordsToChooseFrom.size());

        selectedWord = wordsToChooseFrom.get(selectedIndex);
    }

    /**
     * Create list of letters from selected word
     */
    private void generateLetters() {
        for (char letter : selectedWord.toCharArray()) {
            letters.add(letter);
        }
    }

    /**
     * Generate random letters
     */
    private void addAdditionalLetters() {
        int numAddedLetters = (int) (Math.random() * 4);
        for (int i = 0; i < numAddedLetters; i++) {
            letters.add((char) ((Math.random() * 26) + 97));
        }
    }

    /**
     * Generate list of words that are made of generated letters
     */
    private void findValidWords() {
        validWords = new ArrayList<>();
        letterFreq = findLetterFreq(String.valueOf(letters).replaceAll("[,\\s\\[\\]]", ""));
        for (String word : allWords) {
            int[] tempFreq = findLetterFreq(word);
            if (findMatch(tempFreq)) {
                validWords.add(word);
            }
        }
    }

    /**
     * findMatch looks for word matches
     *
     * @param tempFreq frequency of each letter
     * @return true or false whether it is a match
     */
    private boolean findMatch(int[] tempFreq) {
        for (int i = 0; i < 26; i++) {
            if (letterFreq[i] == 0 && tempFreq[i] > 0) {
                return false;
            } else if (letterFreq[i] < tempFreq[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Method for finding the frequency of a letter
     *
     * @param word the word to find letter frequency of
     * @return freq the frequencies of all the letters
     */
    private int[] findLetterFreq(String word) {
        int[] freq = new int[26];
        for (char letter : word.toCharArray()) {
            freq[letter - 'a']++;
        }
        return freq;
    }

    /**
     * Used to calculate the score of a clients guess if it is valid
     *
     * @param guess The clients guess
     * @return Integer value representing the score of the guess
     * Each letter has a score and the length of the word is accounted for
     */
    public int guess(String guess) {
        if (validWords.contains(guess.toLowerCase())) {
            int scoreSum = 0;
            for (Character c : guess.toCharArray()) {
                scoreSum += alphabetScores[Character.toUpperCase(c) - 65];
            }
            scoreSum += guess.length();
            return scoreSum;
        }
        return 0;
    }

    /**
     * Abstract method used to employ rule set for how clients should wait in lobby
     *
     * @throws InterruptedException If thread is interrupted
     */
    public abstract void pregameLobby() throws InterruptedException;

    /**
     * Abstract method used to employ rule set of how long a match should last etc.
     */
    public abstract void startGame();

    public String getTournamentName() {
        return "";
    }
}
