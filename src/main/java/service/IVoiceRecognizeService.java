package service;

import exception.ServiceException;

import java.io.IOException;

/**
 * Created by Toan on 22.01.2015.
 */
public interface IVoiceRecognizeService {
    /**
     * calls start method from service if not started yet, if already startet then restart will be called.
     */
    //public void startVoice();

    /**
     * calls cancel method from service. and also resets the service.
     */
    //public void cancelVoice();


    /**
     * checks if service is running, calls isRunning() method.
     * @return true if service is running.
     */
    //public Boolean isStarted();

    /**
     * This method sets a listener (implements IWhistleListener). Its onSound() method will be called
     * when a whistle is recognized froun sound input. depending on the command(key) a specific listener will be called.
     * @param sl listener, does programmed action,
     */
    public void addListener(ISoundListener sl,String command);

    /**
     * sets grammar for voice recognition service.
     * @param grammar
     */
    //public void setGrammar(String grammar);

    /**
     * initiates the object, sets configuration
     */
    public void init() throws ServiceException;

    /**
     * Starts speechrecognition engine from cmusphinx
     * a loop will read speech commands and then calls listeners
     * if a stop command (play movie, exit movie) is read then the loop will be exited and the audioline will be closed
     */
    public void startVoice();

    /**
     * sets stop to true, stops voicereognition loop.
     */
    public void stopVoice();

}
