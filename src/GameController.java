import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXTextArea;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import java.io.IOException;
import java.util.ArrayList;

/**
 * GameController class that derives from Controller and controls the game screen
 * @see Controller
 */
public class GameController extends Controller {
    /**
     * Member variable for the word panel of correctly guessed words
     */
    @FXML
    private JFXTextArea wordPanel;
    /**
     * Member variable for the guess box field
     */
    @FXML
    private JFXTextArea guessWordField;
    /**
     * Member variable for the letters to create words from label
     */
    @FXML
    private Label letters;
    /**
     * Member variable for the parent pane of the rippler
     */
    @FXML
    private Pane parentPane;
    /**
     * Member variable for the rippler that gives feedback on correct/incorrect guesses
     */
    @FXML
    private JFXRippler guessRippler;
    /**
     * Member variable for the child pane that the rippler is in
     */
    @FXML
    private Pane childPane;
    /**
     * Member variable for the score label
     */
    @FXML
    private Label scoreLabel;
    /**
     * Member variable for the time label
     */
    @FXML
    private Label timeLabel;
    /**
     * Member variable for the last guess
     */
    private String lastGuess;
    /**
     * Member variable for the player score
     */
    private int playerScore;
    /**
     * Member variable to store the correct guesses from the user
     */
    private ArrayList<String> correctGuesses;

    /**
     * Initialize method for GameController that sets the client's controller to this and sets up the GUI components
     */
    public void initialize() {
        getClient().setController(this);
        correctGuesses = new ArrayList<>();
        guessWordField.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        letters.setText(getClient().getLetters());
        scoreLabel.setText(scoreLabel.getText() + " 0");
        timeLabel.setText(timeLabel.getText() + " 0");
        playerScore = 0;
        guessRippler = new JFXRippler(childPane);
        parentPane.getChildren().add(guessRippler);
    }
    /**
     * Listener for when enter is pressed in the guess word field that sends the guess to the server
     * @param event the key event that caused triggered the listener
     */
    @FXML
    void enterPressed(KeyEvent event) {
        if (event.getEventType().equals(KeyEvent.KEY_PRESSED) && event.getCode().equals(KeyCode.ENTER) && guessWordField.getText() != null) {
            System.out.println("ENTER PRESSED");
            lastGuess = guessWordField.getText();
            getClient().sendMessage(String.format("%s,%s\n", Server.sendMessage.GUESS, guessWordField.getText()));
            guessWordField.setText("");
            parentPane.getChildren().remove(0);
            guessRippler = new JFXRippler(childPane);
            parentPane.getChildren().add(guessRippler);
        }
    }

    /**
     * Method for the client to call to let the controller know the game is over. Overrides the controller's method
     */
    @Override
    public void endGame() {
        Platform.runLater(() -> {
            try {
                switchScene("gameResultsFXML.fxml", "Results");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    /**
     * Method for the client to give the controller the results of a guess. Overrides Controller's method
     * @param score the score of the word
     */
    @Override
    public void guessResult(int score) {
        Platform.runLater(() -> {
            System.out.println(score);
            guessRippler.setEnabled(true);

            if(score>0 && !correctGuesses.contains(lastGuess)) {
                correctGuesses.add(lastGuess);
                wordPanel.setText(wordPanel.getText() + lastGuess);
                playerScore += score;
                scoreLabel.setText("Score: " + playerScore);
                guessRippler.setRipplerFill(new Color(0, 1, 0, 0));

                guessRippler.createManualRipple();
            } else {
                guessRippler.setRipplerFill(new Color(1, 0, 0, 0));
                guessRippler.createManualRipple();
            }
        });
    }
    /**
     * Method for the client to call when the server updates the time. Overrides the Controller's method
     * @param time the updated time from the client
     */
    @Override
    public void updateTimer(int time) {
        Platform.runLater(() -> timeLabel.setText("Time: " + time));
    }
}
