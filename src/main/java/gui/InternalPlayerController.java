package gui;

import dto.Movie;
import dto.Subtitle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.IVoiceRecognizeService;
import service.vlcj.VideoPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import service.ISoundListener;
import service.ISoundRecognizeService;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.ResourceBundle;

public class InternalPlayerController implements Initializable {

    private static final Logger logger = LogManager.getLogger(InternalPlayerController.class);
    private MainGuiController mainGuiController;
    private DetailMovieGuiController detail;
    private Movie selectedFile = null;
    private String sub_path=null;
    // private List<Subtitle> subList;
    @Autowired
    private ISoundRecognizeService soundService;
    @Autowired
    private IVoiceRecognizeService voiceService;

   @FXML
    public Slider timeSlider;
    @FXML
    public Slider volumeSlider;
    @FXML
    public Label subLabel;
    @FXML
    private Button reset;

    @FXML
    private Button playpause;

    @FXML
    public BorderPane pane;

    @FXML
    public Label timeLabel;

    @FXML
    public MenuButton SubMenu;

    @FXML
    public GridPane gridPane;

    @FXML
    private Button fwdButton;
    @FXML
    private Button ffwdButton;
    @FXML
    private Button rwdButton;
    @FXML
    private Button frwdButton;
    @FXML
    private ImageView play=new ImageView();
    @FXML
    private ImageView stop =new ImageView();
    @FXML
    private ImageView fwd =new ImageView();
    @FXML
    private ImageView rwd =new ImageView();
    @FXML
    private ImageView ffwd =new ImageView();
    @FXML
    private ImageView frwd =new ImageView();


    private VideoPlayer player;


    @FXML
    public void resetMedia(){
      player.stop();
      timeSlider.setValue(0.0);
    }


    @FXML
    public void playMedia() {
        player = new VideoPlayer("moViewer Player");
        this.setListeners();
         gridPane.getChildren().add(player);
      //my media
        String media =  this.selectedFile.getPath();
        player.StartPlay(media);
        //player.setVolume(100);
    }

    @FXML
    public void playMediaWithSubs(Subtitle sub) {
        player = new VideoPlayer("moViewer Player");
        this.setListeners();
        gridPane.getChildren().add(player);
        //  File subtitle = path.toFile();
        String media =  this.selectedFile.getPath();
        subLabel.setText("   "+sub.getLanguage()+"("+sub.getComment()+")");

        String sub_path = this.getClass().getClassLoader().getResource("subtitles/").getPath().replaceAll("%20", " ").toString() + sub.getLanguagePath();
        if (System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0 && sub_path.indexOf("/") == 0){
            sub_path = sub_path.substring(1).replace("/", "\\");
        }

        player.StartPlay(media, sub_path);

        //player.setVolume(100);
    }

    @FXML
    public void stopMedia() {
        if (player != null) {
            player.release();
        }
    }

    public void playpauseMedia(){
        player.playpause();
    }


    public void bigSkipForward(){
        player.skip(600000);
    }
    public void BigSkipBack(){
        player.skip(-600000);
    }
    public void smallSkipForward(){
        player.skip(30000);
    }
    public void smallSkipBack(){
        player.skip(-30000);
    }

    public void setMovie(Movie movie, MainGuiController maingui) {
        mainGuiController = maingui;
        this.selectedFile = movie;
    }

    public void setMovie(Movie movie, DetailMovieGuiController detail) {
        this.detail = detail;
        this.selectedFile = movie;
}
    public void setSubMenu(List<Subtitle> subList, MainGuiController maingui){
        mainGuiController = maingui;

        // no subtitles
        MenuItem noSubs = new MenuItem("No Subtitles");
        noSubs.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                player.getPlayer().setSpu(-1);
                subLabel.setText("No Subtitles");
            }
        });
        SubMenu.getItems().add(noSubs);

        //all available subs
        for(Subtitle s : subList){
           MenuItem i = new MenuItem(s.getLanguage());

            i.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent t) {
                    String sub_path = this.getClass().getClassLoader().getResource("subtitles/").getPath().replaceAll("%20", " ").toString() + s.getLanguagePath();
                    if (System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0 && sub_path.indexOf("/") == 0){
                        sub_path = sub_path.substring(1).replace("/", "\\");
                    }
                    logger.debug("Subtitle-Path:"+sub_path);
                    player.setSubFile(sub_path);
                    subLabel.setText(s.getLanguage()+" ("+ s.getComment()+")");
                }
            });
            SubMenu.getItems().add(i);


        }
    }

    public void cancelServices(){
        this.voiceService.stopVoice();

        this.soundService.cancelWhistle();

    }



private void setListeners (){
    // update player according to slider position
    timeSlider.valueProperty().addListener(ov -> {
        if (timeSlider.isValueChanging()) {
            // multiply duration by percentage calculated by slider position
            player.jumpTo((float) (timeSlider.getValue() / 100.0));
        }
        if (timeSlider.isPressed()) {
            player.jumpTo((float) (timeSlider.getValue() / 100.0));
        }

    });

    // update volume according to slider position
    volumeSlider.valueProperty().addListener(ov -> {
        if (volumeSlider.isValueChanging()) {
            player.setVolume((int)volumeSlider.getValue());
        }
        if(volumeSlider.isPressed()) {
            player.setVolume((int)volumeSlider.getValue());
        }

    });

    //update slider position according to playback
    player.getPlayer().addMediaPlayerEventListener( new MediaPlayerEventAdapter(){

        @Override
        public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {
            Platform.runLater(new Runnable() {
                public void run() {
                    timeSlider.setValue(newPosition*100);
                    Duration currentTime = Duration.ofMillis(player.getTime());
                    Duration totalTime = Duration.ofMillis(player.getLength());

                   timeLabel.setText(formatTime(currentTime, totalTime));
                }
            });
        }
    } );

    soundService.setListener(new ISoundListener() {
        @Override
        public void onSound() {
            Boolean play = player.getPlayer().isPlaying();
            player.playpause();
            soundService.setIsPlaying(!play);
        }
    });

    voiceService.addListener(new ISoundListener(){
        @Override
        public void onSound() {
            player.playpause();
        }
    },"play movie");

    voiceService.addListener(new ISoundListener(){
        @Override
        public void onSound() {
            stopMedia();
        }
    },"clear movie");

    //multiple repeating ... is bad (useability)
    /*voiceService.addListener(new ISoundListener(){
        @Override
        public void onSound() {
            double a = volumeSlider.getValue();
            a = a + volumeSlider.getMax()*0.1;
            if(a > volumeSlider.getMax()){
                a = volumeSlider.getMax();
            }
            logger.debug("max" +volumeSlider.getMax());
            logger.debug("min" +volumeSlider.getMin());
            logger.debug("a" + a);

            player.setVolume((int) a);
            logger.debug("value" +volumeSlider.getValue());
        }
    },"volume up");

    voiceService.addListener(new ISoundListener(){
        @Override
        public void onSound() {
            double a = volumeSlider.getValue();
            a = a - volumeSlider.getMax()*0.1;
            if(a < volumeSlider.getMin()){
                a = volumeSlider.getMin();
            }
            logger.debug("max" +volumeSlider.getMax());
            logger.debug("min" +volumeSlider.getMin());
            logger.debug("a" + a);
            player.setVolume((int) a);
            logger.debug("value" +volumeSlider.getValue());
        }
    },"volume down");*/

    voiceService.addListener(new ISoundListener(){
        @Override
        public void onSound() {
            bigSkipForward();
        }
    },"mighty skip");


    voiceService.addListener(new ISoundListener(){
        @Override
        public void onSound() {
            smallSkipForward();
        }
    },"small skip");

    voiceService.addListener(new ISoundListener(){
        @Override
        public void onSound() {
            smallSkipBack();
        }
    },"small backwards");

    voiceService.addListener(new ISoundListener(){
        @Override
        public void onSound() {
            BigSkipBack();
        }
    },"mighty backwards");

    voiceService.addListener(new ISoundListener(){
        @Override
        public void onSound() {
            player.mute();
        }
    },"sound off");

    voiceService.addListener(new ISoundListener(){
        @Override
        public void onSound() {
            player.unMute();
        }
    },"sound on");


}


    private static String formatTime(Duration elapsed, Duration duration) {

        long longElapsed = (long)Math.floor(elapsed.toMillis());
            longElapsed = longElapsed/1000; // convert back to seconds
            long elapsedHours = longElapsed / (60 * 60);
            long elapsedMinutes = (longElapsed - elapsedHours*60*60)/60;
            long elapsedSeconds = longElapsed - (elapsedHours * 60 * 60) - (elapsedMinutes * 60);


         long longDuration = (long)Math.floor(duration.toMillis());
            longDuration = longDuration/1000; // convert back to seconds
            long durationHours = longDuration / (60 * 60);
            long durationMinutes = (longDuration - durationHours*60*60)/60;
            long durationSeconds = longDuration - (durationHours * 60 * 60) -( durationMinutes * 60);

                return String.format("%d:%02d:%02d/%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        this.play.setImage((loadImageFromPath(this.getClass().getClassLoader().getResource("pics/button-pp.png").getPath().replaceAll("%20", " ").toString() )));
        this.stop.setImage((loadImageFromPath(this.getClass().getClassLoader().getResource("pics/button-stop.png").getPath().replaceAll("%20", " ").toString()  )));
        this.fwd.setImage((loadImageFromPath(this.getClass().getClassLoader().getResource("pics/button-fwd.png").getPath().replaceAll("%20", " ").toString() )));
        this.rwd.setImage((loadImageFromPath(this.getClass().getClassLoader().getResource("pics/button-rwd.png").getPath().replaceAll("%20", " ").toString() )));
        this.ffwd.setImage((loadImageFromPath(this.getClass().getClassLoader().getResource("pics/button-ff.png").getPath().replaceAll("%20", " ").toString() )));
        this.frwd.setImage((loadImageFromPath(this.getClass().getClassLoader().getResource("pics/button-fb.png").getPath().replaceAll("%20", " ").toString() )));

        playpause.setGraphic(play);
        reset.setGraphic(stop);
        ffwdButton.setGraphic(ffwd);
        frwdButton.setGraphic(frwd);
        fwdButton.setGraphic(fwd);
        rwdButton.setGraphic(rwd);

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
