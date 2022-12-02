import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Game implements Runnable {
    private ArrayList<ClientCommunication> playerList = new ArrayList<>();
    private ArrayList<Character> letters = new ArrayList<>();
    private ArrayList<String> validWords = new ArrayList<>();
    private ArrayList<String> allWords = new ArrayList<>();
    private int[] letterFreq = new int[26];

    public ArrayList<Character> getLetters() {
        return letters;
    }

    @Override
    public void run() {
        initializeGame();
        playGame();
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

    public void addPlayer(ClientCommunication player) {
        playerList.add(player);
    }

    public void guess(Player player, String guess) {
        if (validWords.contains(guess.toLowerCase())) {
            player.addScore(1);
        }
    }

    public void endRound(Player player) {

    }

}
