package service;

import exception.ServiceException;

/**
 * Created by Toan on 15.01.2015.
 */
public interface ISoundRecognizeService {
    /**
     * This methods starts the whistle recognition thread.
     * Datachunks are read and saved in WAVE format. Then it will be tested if it fits
     * certain criteria for a whistle. (with musicg-1.4.2.0 lib)
     * Calls listeners if a whistle is recognized.
     * Init() has to be called before this method
     */
    public void startWhistle();

    /**
     * calls cancel method from service
     */
    public void cancelWhistle();

    /**
     * calls restart method from service
     */
    public void restartWhistle();

    /**
     * calls isRunning method from service;
     * @return true if service is running
     */
    public Boolean isStarted();

    /**
     * this method sets the isPaused variable
     * @param paused true if paused, false it not
     */
    public void setIsPlaying(Boolean paused);

    /**
     * This method sets a listener (implements IWhistleListener). Its onSound() method will be called
     * when a whistle is recognized froun sound input.
     */
    public void setListener(ISoundListener wl);

    /**
     * This method initializes the TargetDataLine and throws an exception if something
     * goes wrong: Audiosystem is not line supported or line is not available.
     */
    public void init() throws ServiceException;

    /**
     * Sets clap true/false
     * if clap is true, the service will recognize claps
     * if clap is false, the service will recognize whistle
     * clap is set false by default, (doesnt work properly with environmental influence)
     * @param clap
     */
    public void setClap(boolean clap);

    /*public void setModelDirectory(String modelDirectory);

    public void setGrammarPath(String grammarPath) ;

    public void setGrammarName(String grammarName);

    public void setDictionary(String dictionary);*/


}
