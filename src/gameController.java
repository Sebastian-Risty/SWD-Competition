import com.jfoenix.controls.JFXTextArea;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class gameController {

    @FXML
    private JFXTextArea wordPanel;

    @FXML
    private JFXTextArea guessWordField;

    @FXML
    void enterPressed(KeyEvent event) {
        if(event.getEventType().equals(KeyEvent.KEY_PRESSED) && event.getCode().equals(KeyCode.ENTER)) {
            wordPanel.setText(wordPanel.getText() + guessWordField.getText());
            guessWordField.setText("");
        }
    }

    public void initialize() {
    }

}
