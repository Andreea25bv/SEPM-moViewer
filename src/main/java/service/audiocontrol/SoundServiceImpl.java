package service.audiocontrol;

import com.musicg.wave.Wave;
import com.musicg.wave.WaveHeader;
import com.musicg.wave.WaveTypeDetector;
import exception.ServiceException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import service.ClapDetecter;
import service.ISoundListener;
import service.ISoundRecognizeService;
import service.IVoiceRecognizeService;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Toan on 20.01.2015.
 */

public class SoundServiceImpl extends Service<Void> implements ISoundRecognizeService {
    private static final Logger logger = LogManager.getLogger(SoundServiceImpl.class);
    private Boolean clap;
    private Boolean isPlaying;
    private TargetDataLine line;
    private Integer pass;
    private ISoundListener listener;
    private List<Date> recent;
    private WaveHeader wavehead;
    @Autowired
    private IVoiceRecognizeService voiceService;

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws ServiceException {
                int iterations = 0;
                //Setting up audio input from microphone, if available
                try {
                    line.open(getAudioFormat());
                } catch (LineUnavailableException e) {
                    logger.error(e.getMessage());
                    return null;
                }
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                int numBytesRead;
                byte[] data = new byte[line.getBufferSize()];
                line.start();
                //100000 shouldnt be reached
                for (iterations = 0; iterations < 100000; iterations++) {
                    if (isCancelled()) {
                        break;
                    }
                    //reading from microphone and saving in data
                    numBytesRead = line.read(data, 0, data.length);
                    out.write(data, 0, numBytesRead);
                    /********************************************************
                     saving captured data to file tmp.wav (for testing)
                     ********************************************************
                    File save = new File("tmp.wav");
                    try {
                        AudioInputStream ais = new AudioInputStream(
                                new ByteArrayInputStream(data), getAudioFormat(),
                                data.length / getAudioFormat().getFrameSize()
                        );
                        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, save);
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                        return null;
                    }
                    //read saved data with headers, to generate wave object
                    //write/read process was to get wave headers.

                    Wave wave = new Wave("tmp.wav");
                    *********************************************************/

                    //read waveheader from valid wav instead of read/write (performance)
                    Wave wave = new Wave(wavehead,data);

                    WaveTypeDetector waveTypeDetector = new WaveTypeDetector(wave);
                    ClapDetecter clapDetecter = new ClapDetecter(wave);

                    logger.info(iterations + ": " + waveTypeDetector.getWhistleProbability());

                    //recent was intended to use for recognizing long whistle songs
                    if (recent.size() == 2 || recent.size() == 1) {
                        logger.debug("sound detected:" + " clap=" + clap + "[" + recent.size() + "]");
                        if (listener != null && clap == true) {
                            //Whistle and clap sequence recognized

                            listener.onSound();
                            logger.debug("isPlaying: " + isPlaying);
                            if(isPlaying == false){
                                line.close();
                                voiceService.startVoice();
                                Boolean wait = true;
                                while (wait) {
                                    try {
                                        line.open(getAudioFormat());
                                        line.start();
                                        wait = false;
                                    } catch (LineUnavailableException e) {
                                        //logger.debug("wait ...");
                                    }
                                }
                            }
                            /**********************************************************
                             this part will result in heapspace error
                             recognizer cant be initialized twice or more often like this
                             **********************************************************
                            if (isPlaying == false) {
                                try {
                                    line.close();
                                    recognizer = new myLiveSpeechRecognizer(config);
                                } catch (IOException e) {
                                    logger.error("Couldnt initialize LiveSpeechRecognizer");

                                }

                                recognizer.startRecognition(true);
                                Boolean stop = false;

                                while (!stop) {
                                    SpeechResult result = recognizer.getResult();
                                    logger.debug("command: " + result.getHypothesis());
                                    if (result.getHypothesis().equals("play movie")) {
                                        stop = true;
                                        listener.onSound();
                                    }
                                }

                                recognizer.stopRecognition();
                                Boolean wait = true;
                                while (wait) {
                                    try {
                                        line.open(getAudioFormat());
                                        line.start();
                                        wait = false;
                                    } catch (LineUnavailableException e) {
                                        logger.debug("wait ...");
                                    }
                                }
                            }
                             **********************************************************/
                        }
                        //empty recent list and switching recognition mode (whistle/clap)
                        recent = new ArrayList<Date>();
                        if (clap) {
                            clap = false;
                        } else {
                            clap = true;
                        }
                    } else if (clap) {
                        if (clapDetecter.getClapProbability() > 0) {
                            addTimeStamp();
                        } else {
                            //if a clap doenst follow a whistle shortly, it will not be recognized
                            pass++;
                            if (pass == 6) {
                                clap = false;
                                pass = 0;
                            }
                        }
                    } else {
                        if (waveTypeDetector.getWhistleProbability() > 0) {
                            addTimeStamp();
                        }
                    }

                }
                return null;
            }
        };
    }

    private void addTimeStamp(){
        Date current;
        current = new Date();
        if (recent.size() == 0) {
            recent.add(current);
        } else if (current.getTime() - recent.get(recent.size() - 1).getTime() < 700) {
            recent.add(current);
        } else {
            recent = new ArrayList<Date>();
        }
        /*if (recent.size() > 0) {
            System.out.println("lalalalal");
        }*/ //whistle song recognition isnt needed anymore, thread will trigger when a whistle and a clap (max 2-3s delay) is recognized
    }

    @Override
    public void startWhistle() {
        logger.info("Sound Recognition started ...");
        this.start();
    }

    @Override
    public void cancelWhistle() {
        logger.info("Cancelling Sound Recognition ...");
        this.cancel();
        this.reset();
    }

    @Override
    public void restartWhistle() {
        logger.info("Sound Recognition restarted ...");
        this.restart();
    }

    @Override
    public Boolean isStarted() {
        return this.isRunning();
    }

    @Override
    public void setIsPlaying(Boolean playing) {
        isPlaying = playing;
    }


    @Override
    public void setListener(ISoundListener wl) {
        listener = wl;
    }

    @Override
    public void init() throws ServiceException {
        logger.info("Initialising SoundService ...");
        recent = new ArrayList<Date>();
        clap = false;
        isPlaying = false;
        pass = 0;
        listener = null;
        initLine();
        //Wave wave = new Wave("veryimportantHEADER.wav");
        //Wave wave = new Wave("target/classes/veryimportantHEADER.wav");
        Wave wave = new Wave((getClass().getClassLoader().getResource("veryimportantHEADER.wav").getPath().replaceAll("%20", " ").toString()));
        wavehead = wave.getWaveHeader();
    }

    private void initLine() throws ServiceException {
        logger.info("Initialize audio line");
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, getAudioFormat());
        if (!AudioSystem.isLineSupported(info)) {
            logger.error("Audiosystem is not Line supported");
            throw new ServiceException("Audiosystem is not Line supported");
        }
        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(getAudioFormat());
        } catch (LineUnavailableException ex) {
            logger.error("Line is not available.");
            throw new ServiceException("Line is not available.");
        }
    }

    @Override
    public void setClap(boolean clap) {
        this.clap = clap;
    }

    private AudioFormat getAudioFormat() {
        float sampleRate = 44100; //8000,11025,16000,22050,44100
        int sampleSizeInBits = 16; //8,16
        int channels = 1; //1,2
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

}
