package service;

/**
 * Created by Toan on 28.01.2015.
 */
public interface ISleepService {
    /**
     * starts timerservice, it will call listener after timeleft ist zero
     * @param minutes integer as minutes to sleep
     */
    public void startTimer(Integer minutes);

    /**
     * stops the timer and resets timeleft
     */
    public void cancelTimer();

    /**
     * Setter method for listener
     * @param listener setted by sleepguicontroller
     */
    public void setListener(ISleepListener listener);

    /**
     * calls isRunning method from javafxservice
     * @return true if it is running, false if not
     */
    public boolean isStarted();

    /**
     * getter method.
     * @return inital time;
     */
    public Integer getTime();
}
