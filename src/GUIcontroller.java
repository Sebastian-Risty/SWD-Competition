import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GUIcontroller {
    @FXML
    private ImageView background;
    @FXML
    void QPMbutton(ActionEvent event) {

    }
    @FXML
    void initialize() {
        background.setImage(new Image("/images/LoadScreenBackground.png"));
    }
}
