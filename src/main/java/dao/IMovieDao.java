package dao;

import dto.FilterParameter;
import dto.Movie;
import exception.MoviePersistenceException;

import java.util.List;

/**
 * Created by Toan on 26.11.2014.
 */
public interface IMovieDao {

    /**
     * This method is called to insert new Movie in the DB
     *
     * @param m
     * @throws MoviePersistenceException if there is Problem occurred
     * @Return the method returns the created Object (Movie)
     */
    public Movie createMovie(Movie m) throws MoviePersistenceException;

    /**
     * This method is to update the given Object (Movie) with the new given Parameters
     * but not the Movie id
     *
     * @param ignore, mid
     * @return the updated movie Object
     * @throws MoviePersistenceException if there is Problem occurred
     */
    public Movie readMovieById(Boolean ignore, Integer mid) throws MoviePersistenceException;

    /**
     * This method is to set the given Movie as ignored the movie will not be really deleted from the BD
     *
     * @param m
     * @throws MoviePersistenceException if there is Problem occurred
     */
    public void updateMovie(Movie m) throws MoviePersistenceException;

    /**
     * This method deletes a given movie (identified by id).
     *
     * @param m
     * @return The found movie Object otherwise returns null
     * @throws MoviePersistenceException if there is Problem occurred
     */
    public void deleteMovie(Movie m) throws MoviePersistenceException;

    /**
     * Will ignore a movie, i.e. not show it in any list and ignore it in future adding-attempts.
     * @param m The movie to ignore
     * @throws MoviePersistenceException If the movie cannot be found or could not be updated/ignored.
     */
    public void setMovieIgnored(Movie m) throws MoviePersistenceException;

    /**
     * This method gives full information of the movies which saved in the DB with the criteria if
     * the movie ignored or not
     *
     * @param ignore
     * @return list of alle  saved movies with the given Boolean value.
     * @throws MoviePersistenceException if there is Problem occurred
     */
    public List<Movie> readAllMovies(Boolean ignore) throws MoviePersistenceException;

    /**
     * This method is for the search method for the Movies with the given criteria if one of the
     * one of the search criteria is null the method set it as true and gives all the possible
     * matches.
     *
     * @param params
     * @return List of all the movies which match the given criteria
     * @throws MoviePersistenceException if there is Problem occurred
     */
    public List<Movie> find(FilterParameter params) throws MoviePersistenceException;

}
