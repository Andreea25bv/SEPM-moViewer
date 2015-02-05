package dao;

import dto.Rating;
import exception.RatingPersistenceException;

import java.util.List;

/**
 * Created by Toan on 26.11.2014.
 */
public interface IRatingDao {

    /**
     * This method is to insert new Rating for Film in the DB
     *
     * @param rating
     * @return rating
     * @throws RatingPersistenceException if a Problem occurred
     */
    public Rating createRating(Rating rating) throws RatingPersistenceException;

    /**
     * This method is to search for rating for a given Movie id
     *
     * @param mid
     * @return list of ratings for movie
     * @throws RatingPersistenceException if a Problem occurred
     */
    public List<Rating> readRatingById(Integer mid) throws RatingPersistenceException;

    /**
     * This method is to search for rating for a given Movie id
     *
     * @param mid
     * @return rating
     * @throws RatingPersistenceException if a Problem occurred
     */
    public Rating readRatingByIdSource(Integer mid,String source) throws RatingPersistenceException;

    /**
     * This method is to update rating
     *
     * @param rating
     * @return rating
     * @throws RatingPersistenceException if a Problem occurred
     */
    public void updateRating(Rating rating) throws RatingPersistenceException;

    /**
     * This method is to delete a given rating
     *
     * @param rating
     * @throws RatingPersistenceException if a Problem occurred
     */
    public void deleteRating(Rating rating) throws RatingPersistenceException;

    /**
     * This method is to get all the Sources which we have in the DB
     * belong to rating class
     *
     * @return list of all Sources
     * @throws RatingPersistenceException
     */
    public List<String> readAllSources() throws RatingPersistenceException;

    /**
     * This method gives full information of the Ratings saved in the DB
     *
     * @return list of all  saved ratings
     * @throws RatingPersistenceException if a Problem occurred
     */
    public List<Rating> readAllRatings() throws RatingPersistenceException;

}
