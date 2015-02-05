package gui;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by markus on 07.01.15.
 */
public class ShowPictureController extends DialogManager implements Initializable {

    private static final Logger logger = LogManager.getLogger(ShowPictureController.class);
    @FXML
    Stage stage;
    @FXML
    private ImageView picture;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        picture.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if (t.getClickCount() >= 1 && stage != null) {
                    stage.close();
                }
            }
        });

    }

    public void setImage(Image image) {
        if (image != null) {
            logger.debug("Showing picture.");
            this.picture.setImage(image);
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
