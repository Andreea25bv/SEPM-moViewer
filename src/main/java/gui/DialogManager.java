package gui;

/**
 * Created by Andreea on 13/12/2014.
 */

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialogs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * This class is responsible for creating Dialog boxes in the GUI
 */
@SuppressWarnings({"deprecation"})
public abstract class DialogManager {

    private static final Logger logger = LogManager.getLogger(DialogManager.class);


    public void createExceptionDialog(Exception e) {
        Dialogs.create()
                .title("Exception Dialog")
                .masthead("Oops something went wrong !")
                .message(e.getMessage())
                .showException(e);

    }

    public Action createConfirmationDialog(String message) {
        Action response = Dialogs.create()

                .title("Confirm Dialog ")
                .masthead("Confirm this Action")
                .message(message)
                .actions(org.controlsfx.dialog.Dialog.ACTION_YES, org.controlsfx.dialog.Dialog.ACTION_NO)
                .showConfirm();

        return response;
    }

    public Optional<String> createInputDialog(String message, String textinput) {
        Optional<String> response = Dialogs.create()
                .title("Input Dialog")
                .masthead("Your Input here")
                .message(message)
                .showTextInput(textinput);

        return response;

    }

    public void createInfoDialog(String message) {
        Dialogs.create()
                .title("Information Dialog")
                .masthead(null)
                .message(message)
                .showInformation();
    }

    public void createDialogForService(String title, Service service) {
        Dialogs.create()
                .title(title)
                .masthead(null)
                .showWorkerProgress(service);
    }


    public boolean dialogBoxConfirmation(Action response) {
        if (response == org.controlsfx.dialog.Dialog.ACTION_YES) {
            return true;
        } else {
            return false;
        }
    }


    public Optional<String> createInputBox(String message, List<String> list) {
        Optional<String> response = Dialogs.create()
                .title("Input Dialog")
                .masthead("Your input here")
                .message(message)
                .showChoices(list);
        return response;
    }

    public Optional<Pair<String,String>> subtitleDialog(String la, String co) {
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Subtitle Dialog");
        dialog.setHeaderText("Put, your Details for the Subtitles here!");
        dialog.setGraphic(new ImageView(loadImageFromPath(this.getClass().getClassLoader().getResource("pics/information.png").getPath().replaceAll("%20", " ").toString())));

        // Set the button types.
        ButtonType OkButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(OkButtonType, ButtonType.CANCEL);

        // Create the labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField lang = new TextField();
        lang.setText(la);
        TextField comment = new TextField();
        comment.setText(co);


        grid.add(new Label("Language:"), 0, 0);
        grid.add(lang, 1, 0);
        grid.add(new Label("Comment:"), 0, 1);
        grid.add(comment, 1, 1);

        // Enable/Disable button depending on whether  was entered.
        Node OkButton = dialog.getDialogPane().lookupButton(OkButtonType);

        // Do some validation (using the Java 8 lambda syntax).
        lang.textProperty().addListener((observable, oldValue, newValue) -> {
            OkButton.setDisable(newValue.trim().length() < 2);
        });

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a pair when the Ok-Button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == OkButtonType) {
                return new Pair<>(lang.getText(), comment.getText());
            }
            return null;
        });

        Optional<Pair<String,String>> result = dialog.showAndWait();

        return result;

    }

    private Image loadImageFromPath(String path) {
        Image img = null;
        File file = new File(path);
        if (file.exists()) {
            File photo = new File(file.toString());
            try {
                InputStream io = new FileInputStream(photo);
                img = new Image(io);
            } catch (FileNotFoundException e) {
                logger.error("Image file not found!");
            }
        } else {
            logger.info("No image file available available.");
        }
        return img;
    }
}
