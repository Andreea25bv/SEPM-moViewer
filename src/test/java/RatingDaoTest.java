import dto.Movie;
import dto.Person;
import dto.Rating;
import dto.Role;
import exception.MoviePersistenceException;
import exception.PersonPersistenceException;
import exception.RatingPersistenceException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by skYeYe on 01.12.2014.
 */
public class RatingDaoTest extends AbstractDaoTest {

    @Before
    public void setUpRating() throws PersonPersistenceException {
        person = new Person(null, "jon doe", "biography", "photoPath");
        person = personDao.createPerson(person);
        role = new Role("testRole", person);
        staff = new ArrayList<Role>();
        staff.add(role);
        movie = new Movie(null, 5, 5, "testPath", false, "testTitle", 50, 1999, "posterPath", "german", "500000", "700000",
                "plotTest", "testTagLine", "listOfSimilarFilms", "testGenre", staff);
    }

    @After
    public void tearDownRating(){
        movie = null;
        staff = null;
        role = null;
        person = null;
    }

    /**
     * This Method is to test the createRating method by inserting new Rating and
     * search for it.
     *
     * @throws RatingPersistenceException if a Problem occurred
     */
    @Test
    public void testCreateValidRating() throws RatingPersistenceException, MoviePersistenceException {
        movie = movieDao.createMovie(movie);
        rating = new Rating(movie.getMid(), 5.0, "testSource");
        rating = ratingDao.createRating(rating);
        assertThat(ratingDao.readRatingById(rating.getMid()).get(0).getMid(), equalTo(rating.getMid()));
    }

    /**
     * This Method test the creatRating by inserting null object
     *
     * @throws RatingPersistenceException if a Problem occurred
     * @throws IllegalArgumentException by inserting null object
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateInvalidRating() throws RatingPersistenceException {
        rating = ratingDao.createRating(null);
    }


    /**
     * This Method test the readRatingById method by inserting new Rating object and then
     * search for it and test if the found one Equals to the inserted one.
     *
     * @throws RatingPersistenceException if a Problem occurred
     */
    @Test
      public void testReadValidRating() throws RatingPersistenceException, MoviePersistenceException {
        movie = movieDao.createMovie(movie);
        rating = new Rating(movie.getMid(), 5.0, "testSource");
        rating = ratingDao.createRating(rating);
        assertThat(ratingDao.readRatingById(rating.getMid()).get(0).getRating(), equalTo(rating.getRating()));
    }

    /**
     * This Method test the readRatingById by giving an non existing mid and search for it.
     *
     * @throws RatingPersistenceException if a Problem occurred
     * @throws RatingPersistenceException by searching for null objects
     */
    @Test(expected = IndexOutOfBoundsException.class)
    public void testReadInvalidRating() throws RatingPersistenceException, MoviePersistenceException {
        movie = movieDao.createMovie(movie);
        rating = new Rating(movie.getMid(), 5.0, "testSource");
        Integer lastMovieId = 0, lastMovieIdActiv = 0, lastMovieIdInactive = 0;
        for (int i = 0; i < movieDao.readAllMovies(false).size(); i++) {
            lastMovieIdActiv = movieDao.readAllMovies(false).get(i).getMid();
        }
        for (int i = 0; i < movieDao.readAllMovies(true).size(); i++) {
            lastMovieIdInactive = movieDao.readAllMovies(true).get(i).getMid();
        }
        if(lastMovieIdActiv <= lastMovieIdInactive) lastMovieId = lastMovieIdInactive;
        else lastMovieId = lastMovieIdActiv;
        logger.info(movie.toString());
        logger.info(rating.toString());
        ratingDao.readRatingById(lastMovieId + 1).get(0).getMid();
    }

    /**
     * This Method test the updateRating method by inserting new Rating then update searching for it in the DB
     * and compare the results.
     *
     * @throws RatingPersistenceException if a Problem occurred
     */
    @Test
    public void testUpdateValidRating() throws RatingPersistenceException, MoviePersistenceException {
        movie = movieDao.createMovie(movie);
        rating = new Rating(movie.getMid(), 5.0, "testSource");
        rating = ratingDao.createRating(rating);
        rating.setRating(15.0);
        ratingDao.updateRating(rating);
        assertThat(ratingDao.readRatingById(rating.getMid()).get(0).getRating(), equalTo(rating.getRating()));
    }


    /**
     * This Method to test the updateRating method by trying to update null object
     *
     * @throws RatingPersistenceException if a Problem occurred
     * @throws IllegalArgumentException by updating null object
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateInvalidRating() throws RatingPersistenceException {
        ratingDao.updateRating(null);
    }


    /**
     * This Method is to test, if an deleted rating is readable, which should fail
     *
     * @throws RatingPersistenceException if a Problem occurred
     * @throws MoviePersistenceException if a Problem occurred
     */
    @Test(expected = IndexOutOfBoundsException.class)
    public void testDeleteValidRating() throws RatingPersistenceException, MoviePersistenceException {
        movie = movieDao.createMovie(movie);
        rating = new Rating(movie.getMid(), 5.0, "testSource");
        rating = ratingDao.createRating(rating);
        ratingDao.deleteRating(rating);
        ratingDao.readRatingById(rating.getMid()).get(0).getRating();
    }

    /**
     * This Method is to test the readRatingBySource method
     *
     * @throws RatingPersistenceException if a Problem occurred
     * @throws MoviePersistenceException if a Problem occurred
     */
    @Test
    public void testReadRatingByIdSource() throws RatingPersistenceException, MoviePersistenceException {
        movie = movieDao.createMovie(movie);
        rating = new Rating(movie.getMid(), 5.0, "testSource");
        rating = ratingDao.createRating(rating);
        assertThat(ratingDao.readRatingByIdSource(rating.getMid(), rating.getSource()).getSource(), equalTo(rating.getSource()));
    }

    /**
     * Test the method by getting all saved sources from the DB
     * first getting the number of the sources which we have in the DB then add new
     * Rating and check the size should be + 1
     *
     * @throws RatingPersistenceException
     * @throws MoviePersistenceException
     */
    @Test
    public void testReadAllSources() throws RatingPersistenceException, MoviePersistenceException {
        int sizeList = ratingDao.readAllSources().size();
        movieDao.createMovie(movie);
        rating = new Rating(movie.getMid(), 6.0, "testSource1");
        ratingDao.createRating(rating);

        assertThat(ratingDao.readAllSources().size(), equalTo(sizeList + 1));

    }

    /**
     * This Method is to test the readAllRatingsBySource method
     * by adding a new source and asserting that the list size of Ratings has incremented
     *
     * @throws RatingPersistenceException if a Problem occurred
     * @throws MoviePersistenceException if a Problem occurred
     */
    @Test
    public void testReadAllRatings() throws RatingPersistenceException, MoviePersistenceException {
        List<Rating> temp = ratingDao.readAllRatings();
        movie = movieDao.createMovie(movie);
        rating = new Rating(movie.getMid(), 5.0, "testSource");
        rating = ratingDao.createRating(rating);
        assertThat(ratingDao.readAllRatings().size(), equalTo(temp.size()+1));
    }
}
