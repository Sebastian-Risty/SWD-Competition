import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;

public class GUIcontroller {
    @FXML
    private ImageView background;
    @FXML
    private JFXButton QPMbutton;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    void QPMbutton(ActionEvent event) {

    }
    @FXML
    void initialize() {
        //background.setImage(new Image());new Image("/images/LoadScreenBackground.png")
        //anchorPane.setBackground(new Background(new BackgroundImage(new Image("/images/LoadScreenBackground.png"))));
    }

    public void QPMbuttonListner(ActionEvent actionEvent) {
    }
}
