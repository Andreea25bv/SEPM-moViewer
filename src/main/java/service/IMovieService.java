package service;

import dto.FilterParameter;
import dto.Movie;

import java.io.File;
import java.util.List;

import exception.MovieServiceException;
import exception.ServiceException;

/**
 * Created by Toan on 02.12.2014.
 */
public interface IMovieService {

    /**
     * This method validates a movie object and pass it to the persistence layer if the validation
     * is successful. Otherwise a service exception will be thrown with a description of the variable, that failed
     * to validate. If an problem occurs in the persistence layer, the persistence exception will be
     * caught and a new service exception will be thrown. In case the persistence is successful, the same movie
     * object will be returned, but with an id (generated from database) and the movie object is in the
     * database stored.
     *
     * @param m Movie object or null
     * @return m with id set (no hard copy)
     * @throws ServiceException
     */
    public Movie createMovie(Movie m) throws ServiceException, MovieServiceException;

    /**
     * This method reads a movie from the database with an specific id and returns it.
     * If no movie was found, null will be returned.
     *
     * @param mid movie identifier, cant be smaller than zero
     * @return movie object or null if not found
     * @throws ServiceException
     */
    public Movie readMovie(Integer mid) throws ServiceException;

    /**
     * This method updates an movie object in the database.
     *
     * @param m movie object or null
     * @throws ServiceException
     */
    public void updateMovie(Movie m) throws ServiceException, MovieServiceException;

    /**
     * This method removes m from the database.
     *
     * @param m with id set or null
     * @throws ServiceException
     */
    public void deleteMovie(Movie m) throws ServiceException;

    public void setMovieIgnored(Movie m) throws ServiceException;

    /**
     * This method reads all movies in the database and returns a list with all movies (ignore flag=false)
     *
     * @return List of all movies in database or empty list
     * @throws ServiceException
     */
    public List<Movie> readAllMovies(Boolean ignored) throws ServiceException;

    /**
     * Shortcut for ignored = false.
     * @return
     * @throws ServiceException
     */
    public List<Movie> readAllMovies() throws ServiceException;


    /**
     * This method plays the movie with an external player (VLC)
     *
     * @param m or null
     * @throws ServiceException
     */
    public void playMovie(Movie m) throws ServiceException;

    /**
     * This method plays the movie with an external player (VLC) with subtitles
     *
     * @param m or null
     * @param sub_path subtitle path (cannot be null or empty)
     * @throws ServiceException
     */
    public void playMovieWithSubs(Movie m,String sub_path) throws ServiceException;


    /**
     * This method is the for the Filter search user gives Paramters
     * the method search for these para and gives the result back if the
     * Para. are all null the method return list of all movies which
     * have rating value in the DB if a movie without Rating will be ignored
     *
     * @param params (FilterParameter)
     * @return list<Movie> </Movie>
     * @throws ServiceException there will be only Exception if there is
     * problem with the DB
     */
    public List<Movie> find(FilterParameter params) throws ServiceException;

    /**
     * This method is to get all Kind of Resources of Rating which we have
     * in the DB for the DropDownBox the method return list of String without
     * Duplication.
     *
     * @return list<String></String> (all Sources without duplicate)
     * @throws ServiceException in generall there is no Exceptions
     * to be thrown onlz if there is problem in the DB
     */
    public List<String> readAllSources() throws ServiceException;

    public void addToFavorite(Movie m) throws ServiceException;

    public void removeFromFavorite(Movie m) throws ServiceException;

    public boolean isFavourite(Movie m) throws ServiceException;

    /**
     * This represent art of search bz calling all the movies which we have
     * in the DB and evaluate them one after the other with the given user
     * Parameters plus the zear of the Movie and if this Movie as Fav. from
     * user and if the rating is high becomes the movie more scores.
     * the Result list must not be the perfect Result but it is
     * agood Result.
     *
     * @param ?????
     * @return list<Movies></Movies> list of the best 5 moives
     *
     * @throws ServiceException if there is problem with the  maps
     * or if the Movie is null
     */
    public List<Movie> heuristic(String title,String name,String genre, Integer year)
            throws ServiceException;

    /**
     * This method is to search for a random movie for a given Parameters
     * use the same parameters like heuristic and with the Random number
     * choose one of the returned list from the heuristic method as
     * our random movie.
     *
     * @param
     * @return Movie object
     * @throws ServiceException if there is problem with the  maps
     * or if the Movie is null
     */
    public Movie random(String title,String name,String genre, Integer year)
            throws ServiceException;

    /**
     * This method shows the Movie in the Explorer
     *
     * @param m or null
     * @throws ServiceException
     */
    public void showMovieExplorer(Movie m) throws ServiceException;

    /**
     * This method shows the next Movie in the DetailMovieGuiController
     *
     * @param m or null
     * @throws ServiceException
     */
    public Movie showNextMovie(Movie m) throws ServiceException;

    /**
     * This method shows the previous Movie in the DetailMovieGuiController
     *
     * @param m or null
     * @throws ServiceException
     */
    public Movie showPreviousMovie(Movie m) throws ServiceException;

    /**
     * This method is to change a poster for a selected movie
     *
     * @param m, in, out
     * @throws ServiceException
     */
    public void changePoster(Movie m, File in, File out) throws ServiceException;

}
