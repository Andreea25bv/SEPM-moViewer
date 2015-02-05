package service.audiocontrol;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.ISleepListener;
import service.ISleepService;

/**
 * The SpeechRecognition Models provived, are tailored for a specifc User.
 * You need to adapt it for your own use with the adaption corpus in the resource folder:
 * lai.dict
 * lai.fileids
 * lai.transcription
 * lai.txt
 * You can find the guide on: http://cmusphinx.sourceforge.net/wiki/tutorialadapt
 * Created by Toan on 28.01.2015.
 */
public class SleepServiceImpl extends Service implements ISleepService {
    private Integer timeleft = 0;
    private Boolean started = false;
    private ISleepListener listener;
    private static final Logger logger = LogManager.getLogger(SleepServiceImpl.class);



    @Override
    public void startTimer(Integer minutes) {
        logger.info("Sleep Timer started.");
        timeleft = minutes;
        if(started == false) {
            this.start();
            started = true;
        }else{
            this.restart();
        }
    }

    @Override
    public void cancelTimer() {
        logger.info("Sleep Timer cancelled.");
        this.cancel();
        this.reset();
        timeleft = 0;
    }

    @Override
    public void setListener(ISleepListener listener) {
        logger.info("SetListener Method entered");
        this.listener = listener;
    }

    @Override
    protected Task createTask() {
        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                while(timeleft!=0) {
                    Thread.sleep(60000);
                    --timeleft;
                }
                listener.onTimerZero();
                return null;
            }
        };
    }

    public boolean isStarted(){
        return this.isRunning();
    }

    @Override
    public Integer getTime() {
        return timeleft;
    }
}
