package service.audiocontrol;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.ISoundListener;
import service.IVoiceRecognizeService;
import sphinx.api.myLiveSpeechRecognizer;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Toan on 22.01.2015.
 */
public class VoiceServiceImpl implements IVoiceRecognizeService {

    private Configuration configuration;
    private String modelDirectory;
    private String dictionary;
    private String grammarPath;
    private String grammarName;
    private HashMap<String,ISoundListener> listeners;
    private Boolean stop;
    private myLiveSpeechRecognizer recognizer;
    private static final Logger logger = LogManager.getLogger(VoiceServiceImpl.class);

    public void setModelDirectory(String modelDirectory) {
        this.modelDirectory = modelDirectory;
    }

    public void setGrammarPath(String grammarPath) {
        this.grammarPath = grammarPath;
    }

    public void setDictionary(String dictionary) {
        this.dictionary = dictionary;
    }

    public void setGrammarName(String grammarName) {
        this.grammarName = grammarName;
    }

    /*@Override
    public void startVoice() {
        if(!isStarted){
            this.start();
        }else{
            this.restart();
        }
    }

    @Override
    public void cancelVoice() {
        this.cancel();
        this.reset();
    }

    @Override
    public Boolean isStarted() {
        return this.isRunning();
    }*/

    @Override
    public void addListener(ISoundListener sl, String command) {
        listeners.put(command,sl);
    }


    @Override
    public void init() throws ServiceException {
        stop = false;
        listeners = new HashMap<String,ISoundListener>();
        configuration = new Configuration();
        configuration.setDictionaryPath(dictionary);
        configuration.setAcousticModelPath(modelDirectory);
        configuration.setUseGrammar(true);
        configuration.setGrammarPath(grammarPath);
        configuration.setGrammarName(grammarName);
        try {
            recognizer = new myLiveSpeechRecognizer(configuration);
        } catch (IOException e) {
            logger.error("Couldnt initialize myLiveSpeechRecognizer");
            throw new ServiceException(e.getMessage(),e);
        }
    }

    @Override
    public void startVoice() {
        logger.debug("voiceservice");
        recognizer.startRecognition(true);
        stop = false;
        while (!stop) {
            SpeechResult result = recognizer.getResult();
            logger.debug("command: " + result.getHypothesis());
            ISoundListener sl = listeners.get(result.getHypothesis());
            if (sl != null) {
                sl.onSound();
                if("play movie".equals(result.getHypothesis())){
                    stop = true;
                }
            }
        }
        recognizer.stopRecognition();
    }

    @Override
    public void stopVoice() {
        stop = true;
    }


    /*@Override
    protected Task<Void> createTask() {
        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                logger.debug("voiceservice");
                recognizer.startRecognition(true);

                Boolean stop = false;

                while (!stop) {
                    SpeechResult result = recognizer.getResult();
                    logger.debug("command: " + result.getHypothesis());
                    ISoundListener sl = listeners.get(result.getHypothesis());
                    if (sl != null) {
                        sl.onSound();
                        stop = true;
                    }
                }

                recognizer.stopRecognition();
                return null;
            }
        };
    }*/




}
