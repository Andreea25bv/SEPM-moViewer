import dto.*;
import exception.MoviePersistenceException;
import exception.PersonPersistenceException;
import exception.RatingPersistenceException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by skYeYe on 01.12.2014.
 */
public class MovieDaoTest extends AbstractDaoTest {

    @Before
    public void setUpMovie() throws PersonPersistenceException {
        person = new Person(null, "jon doe", "biography", "/xxPMucou2wRDxLrud8i2D4dsywh.jpg");
        person = personDao.createPerson(person);
        role = new Role("testRole", person);
        staff = new ArrayList<Role>();
        staff.add(role);
        movie = new Movie(null, 5, 5, "testPath", false, "testTitle", 50, 1999, "/680X9apSqmAcebLg8evnnUeQNeI.jpg", "german", "500000", "700000",
                "plotTest", "testTagLine", "listOfSimilarFilms", "testGenre", staff);
    }

    @After
    public void tearDownMovie(){
        movie = null;
        staff = null;
        role = null;
        person = null;
    }

    /**
     * This Method test the createMovie with valid Movie Object
     *
     * @throws MoviePersistenceException
     */
    @Test
    public void testCreateValidMovie() throws MoviePersistenceException{
        movie = movieDao.createMovie(movie);
        assertThat(movieDao.readMovieById(false, movie.getMid()).getMid(), equalTo(movie.getMid()));
    }

    /**
     * This Method test the createMovie method by inserting some null Parameters in the object Movie
     *
     * @throws MoviePersistenceException if a Problem occurred
     * @throws IllegalArgumentException by inserting null parameters
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateInvalidMovie() throws MoviePersistenceException{
        movie = null;
        movie = movieDao.createMovie(movie);
    }

    /**
     * This Method test the ReadMovieMethod, when updating language
     *
     * @throws MoviePersistenceException
     */
    @Test
    public void testReadValidMovie() throws MoviePersistenceException{
        movie.setLanguage("someUnknownLanguage");
        movie = movieDao.createMovie(movie);
        assertThat(movieDao.readMovieById(false, movie.getMid()).getLanguage(), equalTo(movie.getLanguage()));
    }

    /**
     * This Method test the ReadMovieMethod, when Staff is set to null
     *
     * @throws MoviePersistenceException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testReadMovieShouldFail() throws MoviePersistenceException {
        movie.setStaff(null);
        movie = movieDao.createMovie(movie);
    }

    /**
     * This Method test the updateMovie by inserting new movie then update it and then check the
     * Language parameters
     *
     * @throws MoviePersistenceException
     */
    @Test
    public void testUpdateValidMovie() throws MoviePersistenceException{
        movie.setFavorite(true);
        movieDao.createMovie(movie);
        movieDao.updateMovie(movie);
        logger.info(movie.getFavorite());
        assertThat(movieDao.readMovieById(false, movie.getMid()).getFavorite(), equalTo(movie.getFavorite()));
    }


    /**
     * This Method sets a movie to ignored, deletes all non ignored movies
     * and asserts, that a single movie is left
     *
     * @throws MoviePersistenceException
     */
    @Test
    public void testDeleteAllMovies() throws MoviePersistenceException{
        movieDao.setMovieIgnored(movie);
        List<Movie> ignored = movieDao.readAllMovies(true);
        logger.info(ignored.size());
        List<Movie> movies = movieDao.readAllMovies(false);
        logger.info(movies.size());
        for(int i = 0; i < movies.size(); i++){
            movieDao.deleteMovie(movies.get(i));
        }
        Iterator<Movie> movieIterator = movies.iterator();
        while(movieIterator.hasNext()){
            Movie m = movieIterator.next();
            movieIterator.remove();
        }
        assertThat(movieDao.readAllMovies(true).size(), equalTo(ignored.size() + movies.size()));
    }

    /**
     * This method insert first movie and search for the title and genre
     * returns list of movie, which should have only one movie
     *
     * @throws MoviePersistenceException in case of problem by inserting movie
     */
    @Test
    public void testFind1() throws MoviePersistenceException {
        movie.setFavorite(true);

        FilterParameter params = new FilterParameter();
        int check = movieDao.find(params).size();
        movie.setTitle("checkTitle");
        movieDao.createMovie(movie);


        assertThat(movieDao.find(params).size(), equalTo(check + 1));
    }

    /**
     * This method insert first movie and search for the title and genre
     * for the given rating values will return nothing coz this movie
     * has no rating to connected to the movie table
     * returns empty list of movies
     *
     * @throws MoviePersistenceException in case of problem by inserting movie
     */
    @Test
    public void testFind2() throws MoviePersistenceException {
        movie.setFavorite(true);
        movieDao.createMovie(movie);

        FilterParameter params = new FilterParameter();
        params.setTitle("testTitle");
        params.setGenre("testGenre");
        params.setRatingFrom(1.0);
        params.setRatingTill(2.0);

        assertThat(movieDao.find(params).size(), equalTo(0));
    }


    /**
     * this method test the find method by inserting new movie and giving rate for
     * the movie and search for title and genre and rating is between 1.0 and 4.0
     * returns empty list movie coz the searched movie has rating bigger than
     * the given average
     *
     * @throws MoviePersistenceException  in case of problem by inserting movie
     * @throws RatingPersistenceException in case of problem by inserting rating
     */
    @Test
    public void testFind3() throws MoviePersistenceException, RatingPersistenceException {
        movie.setFavorite(true);
        Movie m = null;
        m = movieDao.createMovie(movie);
        ratingDao.createRating(new Rating(m.getMid(), 5.0, "userRating"));

        FilterParameter params = new FilterParameter();
        params.setTitle("testTitle");
        params.setGenre("testGenre");
        params.setRatingFrom(1.0);
        params.setRatingTill(4.0);

        assertThat(movieDao.find(params).size(), equalTo(0));
    }
    /**
     * This Method test the deleteMovie method by inserting movie then search for it and check
     * the size before and after. In case the movie-table is empty, assert that movie-table is empty.
     *
     * @throws MoviePersistenceException
     */
    @Test
    public void testDeleteValidMovie() throws MoviePersistenceException{
        Integer movieNotDeleted = movieDao.readAllMovies(false).size();
        movie = movieDao.createMovie(movie);
        assertThat(movieDao.readAllMovies(false).size(), equalTo(movieNotDeleted + 1));
        movieDao.deleteMovie(movie);
        assertThat(movieDao.readAllMovies(false).size(), equalTo(movieNotDeleted));
    }
}