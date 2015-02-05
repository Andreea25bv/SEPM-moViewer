package service;

import dto.Movie;
import exception.ServiceException;
import dto.Rating;

import java.util.List;

/**
 * Created by Andreea on 09/12/2014.
 */
public interface IRatingService {

    /**
     * This method persists a rating object and returns the same object with an id
     * @param r Rating object or null
     * @return r with id set (no hard copy)
     * @throws ServiceException
     */
    public Rating create (Rating r) throws ServiceException;

    /**
     * This method reads a rating from the database with an specific mid and returns it.
     * If no movie was found, null will be returned.
     * @param mid rating identifier(foreign key from Movie), can not be negative
     * @return Rating object or null if not found
     * @throws ServiceException
     */
    public Rating readRating(Integer mid, String source) throws ServiceException;

    /**
     * This method updates a rating object in the database.
     * @param r rating object or null
     * @throws ServiceException
     */
    public void updateRating(Rating r) throws ServiceException;

    /**
     * This method removes m from the database.
     * @param r rating object or null
     * @throws ServiceException
     */
    public void deleteRating(Rating r) throws ServiceException;


    /**
     * This method creates and persists a rating object with source set as "User" and returns the same object
     * @param mid id of the movie for which the rating exists
     * @param rating  numerical value of the rating
     * @return rating object which has been created
     * @throws ServiceException
     */
    public Rating createUpdateUserRating(Integer mid, Double rating ) throws ServiceException;

    /**
     * This method creates and persists a rating object with source set as "RottenTomato" and returns the same object
     * @param mid id of the movie for which the rating exists
     * @param rating  numerical value of the rating
     * @return rating object which has been created
     * @throws ServiceException
     */
    public Rating createUpdateTomatoRating(Integer mid, Double rating ) throws ServiceException;

    public List<Rating> readAllRatings() throws ServiceException;

    public List<Rating> readRatingsForMovie(Movie m) throws ServiceException;

}
