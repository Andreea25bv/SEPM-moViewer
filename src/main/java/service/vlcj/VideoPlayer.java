package service.vlcj;

import com.sun.jna.Memory;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.RenderCallback;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple video player
 */
public class VideoPlayer extends BorderPane {

    /**
     * Logger to log everything into file. See config file for more info
     */
    private static final Logger logger = LogManager.getLogger(VideoPlayer.class);
    /**
     * Pixel format
     */
    final private WritablePixelFormat<ByteBuffer> byteBgraInstance = PixelFormat.getByteBgraPreInstance();
    /**
     * Camera name
     */
    protected Label cameraHeader;
    //protected String cameraName = "[name]";
  //  protected String soundText = "[sound]";
    /**
     * Canvas, where you'll see video
     */
    protected RWImageView canvas;
    /**
     * Media player component
     */
    DirectMediaPlayer mp;
    /**
     * Pixel writer to write on WritableImage
     */
    private PixelWriter pixelWriter = null;
    /**
     * Inner pane, that will be put in BorderPane. ImageView will watch for its
     * (inner pane) sizes and will match them, not parent BorderPane, to prevent
     * overflow.
     */
    private StackPane inner;
    /**
     * Important player events (videoOutput, error etc)
     */
    private MediaPlayerEventListener mediaPlayerEventListener;

    /**
     * Default player.
     *
     * @param title camera name
     */
    public VideoPlayer(String title) {

        List<String> arguments = new ArrayList<String>();

        arguments.add("--no-plugins-cache");
        arguments.add("--no-snapshot-preview");
        arguments.add("--input-fast-seek");
    //    arguments.add("--no-video-title-show");
       // arguments.add("--no-sub-autodetect-file");
        arguments.add("--disable-screensaver");
        arguments.add("--network-caching");
        arguments.add("1000");
        arguments.add("--quiet");
        arguments.add("--quiet-synchro");
        arguments.add("--intf");
        arguments.add("dummy");
        arguments.add("--sout-keep");
        arguments.add("--verbose=2");

        canvas = new RWImageView(16, 16);
        pixelWriter = canvas.getPixelWriter();


        MediaPlayerFactory factory = new MediaPlayerFactory(arguments);

        mp = factory.newDirectMediaPlayer(
                new BufferFormatCallback() {
                    @Override
                    public BufferFormat getBufferFormat(final int width, final int height) {
                        logger.debug(String.format("New buffer format: %dx%d", width, height));

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                canvas.resize(width, height);
                                pixelWriter = canvas.getPixelWriter();
                            }
                        });

                        return new RV32BufferFormat(width, height);
                    }
                },
                new RenderCallback() {
                    @Override
                    public void display(DirectMediaPlayer mp, Memory[] memory, final BufferFormat bufferFormat) {
                        final ByteBuffer byteBuffer = memory[0].getByteBuffer(0, memory[0].size());

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                pixelWriter.setPixels(
                                        0,
                                        0,
                                        bufferFormat.getWidth(),
                                        bufferFormat.getHeight(),
                                        byteBgraInstance,
                                        byteBuffer,
                                        bufferFormat.getPitches()[0]);
                            }
                        });
                    }
                }
        );

        inner = new StackPane();
        inner.setMinSize(12, 8);
        this.setCenter(inner);
        this.setMinSize(80, 60);

        canvas.fitWidthProperty().bind(inner.widthProperty());
        canvas.fitHeightProperty().bind(inner.heightProperty());
        canvas.setPreserveRatio(true);


        this.setTop(cameraHeader);
        inner.getChildren().add(canvas);

        addVideoOutputEvents();
    }

    public DirectMediaPlayer getPlayer(){
        return this.mp;
    }

    /**
     *
     * @return time instance (in seconds)
     */
    public long getTime(){
        return this.mp.getTime();
    }

    /**
     *
     * @return length of media file in sec.
     */
    public long getLength(){
        return this.mp.getLength();
    }

    /**
     * position of playback (in %)
     * @return percentage of playback
     */
    public float getPosition(){

        return mp.getPosition();
    }

    /**
     * Start playing video with specified subitles
     *
     * @param movie_path video to play (can be anything)
     * @param subtitle_path subtitle file to be played with the movie
     */
    public void StartPlay(String movie_path, String subtitle_path) {
       logger.debug("Started Playing " + movie_path);

          Path path = Paths.get(subtitle_path);
          File subtitle = path.toFile();

        mp.playMedia(movie_path,"--sub-file="+String.valueOf(subtitle));
        mp.setSubTitleFile(subtitle);
    }

    /**
     * Start playing video without subitles
     *
     * @param movie_path video to play (can be anything)
     *
     */
    public void StartPlay(String movie_path) {
        logger.debug("Started Playing " + movie_path);

        mp.playMedia(movie_path);

    }

    /**
     * Set the external sub file to be played
     *
     * @param subtitle_path path to the sub file
     */
    public void setSubFile(String subtitle_path){
        Path path = Paths.get(subtitle_path);
        File subtitle = path.toFile();
        mp.setSubTitleFile(subtitle);
    }

    /**
     * skip video by given time (in ms)
     *
     * @param delta milliseconds to be skipped
     */
    public void skip(long delta) {
       // logger.debug("Skipped by " + delta);
      if((mp.getTime()+delta) > mp.getLength() ){
          jumpTo(0.99f); }
      else if((mp.getTime()+delta)<0) {
          jumpTo(0.01f); }
        else{
          mp.skip(delta);
      }

    }

    /**
     * jump to position (percentage)
     *
     * @param pos position to jump to (e.g 0.25 = 25 % of total length)
     */
    public void jumpTo(float pos) {
       // logger.debug("jump to " + pos);
        mp.setPosition(pos);

    }


    /**
     * Play/Pause video
     *
     */
    public void playpause() {
        logger.debug("Play/Paused button clicked ");

        if(mp.isPlaying()){
            mp.pause();}
        else{mp.play();}
    }



    /**
     * Stop play.
     */
    public void stop() {

        mp.stop();
    }

    /**
     * Release media player.
     */
    public void release() {
        this.stop();
        mp.release();
    }

    /**
     * Set player volume
     *
     * @param volume
     */
    public void setVolume(int volume) {
        mp.setVolume(volume);
    }

    /**
     * Disable sound.
     */
    public void mute() {
        mp.setVolume(0);
    }

    /**
     * Enable sound
     */
    public void unMute() {
        mp.setVolume(100);
    }

    /**
     * Reset volume. If no sound - enable, if sound - disable.
     */

    /**
     * Add some events when video output has been started.
     * <p/>
     * For example, mute sound. it is not possible to set volume BEFORE video
     * output.
     */
    private void addVideoOutputEvents() {
        mediaPlayerEventListener = new MediaPlayerEventAdapter() {

            @Override
            public void videoOutput(MediaPlayer mediaPlayer, int i) {
                logger.debug(cameraHeader.getText() + ": video output has been created.");
                //resetVolume();
            }

            @Override
            public void error(MediaPlayer mediaPlayer) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        stop();
                        logger.error(": error while playing video.");
                    }
                });
            }

            @Override
            public void playing(MediaPlayer mediaPlayer) {
                logger.debug(cameraHeader.getText() + ": playing event");
            }
        };


        mp.addMediaPlayerEventListener(mediaPlayerEventListener);
    }
}