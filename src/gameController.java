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

public class gameController extends Controller {
    @FXML
    private JFXTextArea wordPanel;
    @FXML
    private JFXTextArea guessWordField;
    @FXML
    private Label letters;
    @FXML
    private Pane parentPane;
    @FXML
    private JFXRippler guessRippler;
    @FXML
    private Pane childPane;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label timeLabel;
    private String lastGuess;
    private int playerScore;
    private ArrayList<String> correctGuesses;

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

    @Override
    public void endGame() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    switchScene("gameResultsFXML.fxml", "Results");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void guessResult(int score) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                System.out.println(score);
                guessRippler.setEnabled(true);

                if(score>0 && !correctGuesses.contains(lastGuess)) {
                    correctGuesses.add(lastGuess);
                    wordPanel.setText(wordPanel.getText() + lastGuess);
                    playerScore += score;
                    scoreLabel.setText("Score: " + playerScore);
                    guessRippler.setRipplerFill(new Color(0, 1, 0, 0));

                    guessRippler.createManualRipple();
                }
                else {
                    guessRippler.setRipplerFill(new Color(1, 0, 0, 0));
                    guessRippler.createManualRipple();
                }
            }
        });
    }
}
