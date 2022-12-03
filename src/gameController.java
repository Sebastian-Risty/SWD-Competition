import com.jfoenix.controls.JFXTextArea;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.awt.*;

public class gameController extends Controller {

    @FXML
    private JFXTextArea wordPanel;

    @FXML
    private JFXTextArea guessWordField;

    @FXML
    private Label letters;

    @FXML
    void enterPressed(KeyEvent event) {
        if(event.getEventType().equals(KeyEvent.KEY_PRESSED) && event.getCode().equals(KeyCode.ENTER)) {
            wordPanel.setText(wordPanel.getText() + guessWordField.getText());
            guessWordField.setText("");
            getClient().sendMessage(String.format("%s,%s\n", Server.sendMessage.GUESS, guessWordField.getText()));
        }
    }

    public void initialize() {
        getClient().setController(this);
    }

    @Override
    public void getLetters(String lettersIn) {
        letters.setText(lettersIn);
    }

    @Override
    public void endGame() {

    }

}
