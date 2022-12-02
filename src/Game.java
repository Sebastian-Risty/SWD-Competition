import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public abstract class Game implements Runnable { //TODO: make class abstract and children runnable maybe?
    private ArrayList<Character> letters = new ArrayList<>();
    private ArrayList<String> validWords = new ArrayList<>();// TODO: gen score value for each word, read rules from some other class
    private ArrayList<String> allWords = new ArrayList<>();
    private int[] letterFreq = new int[26];
    private boolean inProgress = false;
    private int connectedClients = 0;

    public ArrayList<Character> getLetters() {
        return letters;
    }

    public boolean isInProgress(){return inProgress;}
    public void clientConnected(){connectedClients++;}
    public void clientDisconnected(){connectedClients--;}

    @Override
    public void run() {
        initializeGame();
        //playGame();
    }

    private void initializeGame() {
        ArrayList<String> sixLetterWordPool = new ArrayList<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("words"));

            String line = reader.readLine();
            while (line != null) {
                if (line.length() > 2 && line.length() < 7) {
                    allWords.add(line);
                    //TODO CAN CHANGE NUMBER OF LETTER HERE
                    if (line.length() == 6) {
                        sixLetterWordPool.add(line);
                    }
                }
                line = reader.readLine();
            }
            reader.close();

            int selectedIndex = (int) (Math.random() * sixLetterWordPool.size());

            String selectedWord = sixLetterWordPool.get(selectedIndex);
            for (char letter : selectedWord.toCharArray()) {
                letters.add(letter);
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

    public abstract void closeLobby(); // TODO: depending on inherited class, flip flag once x clients connect, timer optional

    // reference to lobby stored in ConnectedClient object, no longer needed

    public int guess(String guess) {
        if (validWords.contains(guess.toLowerCase())) {
            return 1; //TODO: calculate score of each word upon generation based on scoring method, return the value
        }
        return 0;
    }


    //TODO: rounds can be specified as special rule set in subclasses

}
