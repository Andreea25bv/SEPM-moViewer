package service;

import dto.Movie;
import dto.Person;
import exception.ServiceException;

import java.util.ArrayList;

/**
 * Created by Ali Edan on ${25/11/2014}.
 */
public interface ITMDBService {

    /**
     * Retreives detailed metadata from a given TMDB-ID.
     *
     * @param id The ID to download the data for
     * @param lang The language in which the metadata (if found) will be downloaded.
     * @return The movie that was requested.
     */
    public Movie getMovieByTMDBID(int id, String lang) throws ServiceException;

    /**
     * Will retrieve details of a person according to her/his TMDBID.
     * @param id The TMDBID of the person.
     * @return The Person-DTO which will contain name, biography and path to image.
     */
    public Person getPersonByTMDBID(int id) throws ServiceException;

    /**
     *
     * @param title The Title of the movie to look for
     * @param year The year of the publication of the movie (set to 0 to ignore)
     * @param lang The language of the metadate
     * @param nsfw If pornography should be included in the search
     * @param page The page of the search results to return (set to 0)
     * @return List of matches (best first)
     * @throws ServiceException If something happens within the api (search or connection-error)
     */
    public ArrayList<Movie> search(String title, int year, String lang, boolean nsfw, int page) throws ServiceException;
    //add additional api methdods here

    /**
     * Works similar to search but will try to get a better match and applies heuristics and guessing in the search.
     */
    public ArrayList<Movie> searchAppend(String title, int year, String lang, boolean nsfw, int page) throws ServiceException;
}
