package service;

import dto.Trailer;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Created by skYeYe on 24.01.2015.
 */
public interface ITrailerService {

    /**
     * Gets a list of videos from youtube
     *
     * @param searchText is the text for searching on Youtube
     * @return a list of Webelements of Youtube
     */
    public List<WebElement> youtubeTrailerList(String searchText);

    /**
     * Sets the list for the dto Trailer
     *
     * @param searchText is the text for searching on Youtube
     * @return a list of video-movie-trailers
     */
    public List<Trailer> getAllTrailers(String searchText);
}
