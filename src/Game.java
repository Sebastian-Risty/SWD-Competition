import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public abstract class Game implements Runnable { //TODO: make class abstract and children runnable maybe?
    private String gamemode;
    private ArrayList<Character> letters = new ArrayList<>();
    private ArrayList<String> validWords = new ArrayList<>();// TODO: gen score value for each word, read rules from some other class
    private ArrayList<String> allWords = new ArrayList<>();
    private int[] letterFreq = new int[26];
    private boolean progressFlag = false;
    private boolean endFlag = false;
    private int numConnectedClients = 0;

    public String getGamemode() {
        return gamemode;
    }

    public void setGamemode(String gamemode) {
        this.gamemode = gamemode;
    }

    public ArrayList<Character> getLetters() {
        return letters;
    }

    public int getNumConnectedClients() {
        return numConnectedClients;
    }

    public void changeProgressFlag() {
        if (progressFlag) {
            progressFlag = false;
        } else {
            progressFlag = true;
        }
    }

    public void changeEndFlag() {
        if (endFlag) {
            endFlag = false;
        } else {
            endFlag = true;
        }
    }

    public boolean isInProgress() {
        return progressFlag;
    }
    public boolean isFinished(){
        return endFlag;
    }

    public void clientConnected() {
        numConnectedClients++;
    }

    public void clientDisconnected() {
        numConnectedClients--;
    }

    @Override
    public void run() {
        System.out.println("INIT GAME START");
        initializeGame();
        System.out.println("INIT GAME DONE");
        try {
            pregameLobby();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        startGame();
    }

    private void initializeGame() {
        ArrayList<String> wordsToChooseFrom = new ArrayList<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("words"));

            String line = reader.readLine();
            while (line != null) {
                if (line.length() > 2 && line.length() < 9) {
                    allWords.add(line);
                    //TODO CAN CHANGE NUMBER OF LETTER HERE
                    if (line.length() < 6) {
                        wordsToChooseFrom.add(line);
                    }
                }
                line = reader.readLine();
            }
            reader.close();

            int selectedIndex = (int) (Math.random() * wordsToChooseFrom.size());

            String selectedWord = wordsToChooseFrom.get(selectedIndex);
            for (char letter : selectedWord.toCharArray()) {
                letters.add(letter);
            }

            int numAddedLetters = (int) (Math.random() * 4);
            for (int i = 0; i < numAddedLetters; i++) {
                letters.add((char) ((Math.random() * 26) + 97));
            }

            Collections.sort(letters);
            findValidWords();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    private int[] findLetterFreq(String word) {
        int[] freq = new int[26];
        for (char letter : word.toCharArray()) {
            freq[letter - 'a']++;
        }
        return freq;
    }

    // reference to lobby stored in ConnectedClient object, no longer needed

    public int guess(String guess) {
        if (validWords.contains(guess.toLowerCase())) {
            return 1; //TODO: calculate score of each word upon generation based on scoring method, return the value
        }
        return 0;
    }

    public abstract void pregameLobby() throws InterruptedException;

    public abstract void startGame();


    //TODO: rounds can be specified as special rule set in subclasses

}
