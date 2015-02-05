package gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import service.ISleepListener;
import service.ISleepService;

import java.io.IOException;

/**
 * Created by Toan on 13.01.2015.
 */
public class SleepGuiController extends DialogManager {
    @FXML
    private TextField input;
    @FXML
    private ChoiceBox action;
    @FXML
    private Button startButton;
    @FXML
    private Label info;

    @Autowired
    private ISleepService sleepService;

    private String choice;
    private static final Logger logger = LogManager.getLogger(SleepGuiController.class);

    @FXML
    public void initialize() {
        logger.info("initialize method called");
        action.setItems(FXCollections.observableArrayList(
                "End Moviewer", "Shutdown Pc"));
        action.getSelectionModel().select(0);
        choice = "End Moviewer";
        action.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
                choice = t1;
            }
        });
        if(sleepService.isStarted()){
            info.setText("Timer: "+sleepService.getTime() + " mins left.");
        }
}


    @FXML
    public void startTimer(){
        logger.info("startTimer method entered");
        if(sleepService.isStarted()){
            this.createInfoDialog("Timer is already running, cancel it first.");
        }else{
            Integer time = 0;
            if(input.getText().isEmpty()){
                this.createInfoDialog("Please enter minutes first.");
            }else{
                try{
                    time = Integer.parseInt(input.getText());
                }catch (NumberFormatException e){
                    this.createInfoDialog("Please enter minutes (must be numbers.)");
                }
                if(time == 0){
                    this.createInfoDialog("Please enter minutes (cant be 0.)");
                }else{
                    Stage stage = (Stage) startButton.getScene().getWindow();
                    // do what you have to do
                  if("End Moviewer".equals(choice)){
                      sleepService.setListener(new ISleepListener() {
                          @Override
                          public void onTimerZero() {
                              System.exit(1);
                          }
                      });
                  }else if("Shutdown Pc".equals(choice)){
                      sleepService.setListener(new ISleepListener() {
                          @Override
                          public void onTimerZero() {
                              String shutdownCommand = null;
                              String operatingSystem = System.getProperty("os.name");
                              logger.info(operatingSystem);
                                //shutdown on MAX OS X may not work, because of security mechanism, on windows 7 it works fine
                              if ("LINUX".equals(operatingSystem.toUpperCase()) || "MAC OS X".equals(operatingSystem.toUpperCase())) {
                                  shutdownCommand = "shutdown -h now";
                              }
                              else if ("WINDOWS 7".equals(operatingSystem.toUpperCase()) || "WINDOWS 8".equals(operatingSystem.toUpperCase())) {
                                  shutdownCommand = "shutdown.exe -s -t 0";
                              }
                              else {
                                  logger.info("Unsupported operating system.");
                              }

                              try {
                                  if(shutdownCommand!=null){
                                      logger.info("shutdowncommand:" +shutdownCommand);
                                      Runtime.getRuntime().exec(shutdownCommand);
                                  }
                              } catch (IOException e) {
                                  logger.error(e);
                              }
                          }

                      });
                  }
                    sleepService.startTimer(time);
                    this.createInfoDialog("Timer with " + time + " minutes started!");
                    logger.debug("Timer with " + time + " minutes started!");
                    stage.close();
                }
            }
        }
    }

    @FXML
    public void cancelTimer(){
        logger.info("cancelTimer method called.");
        if(sleepService.isStarted()) {
            sleepService.cancelTimer();
            this.createInfoDialog("Timer cancelled.");
            info.setText("Timer is not running.");
        } else {
            this.createInfoDialog("Timer is not running.");
        }
    }

}
