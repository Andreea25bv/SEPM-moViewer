import dto.Movie;
import dto.Rating;
import exception.MovieServiceException;
import exception.ServiceException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by skYeYe on 25.01.2015.
 */
public class RatingServiceTest extends AbstractServiceTest {

    @Before
    public void setUpRating() throws ServiceException, MovieServiceException {
        movie = new Movie();
        movie.setPosterPath("/680X9apSqmAcebLg8evnnUeQNeI.jpg");
        movie = movieService.createMovie(movie);
        rating = new Rating(movie.getMid(), 5.0, "RottenTomato");
        rating = ratingService.create(rating);
    }

    @After
    public void tearDownRating() {
        movie = null;
        rating = null;
    }

    /**
     * This method tests the createRating method
     *
     * @throws ServiceException
     */
    @Test
    public void testCreateRating() throws ServiceException {
        assertThat(ratingService.readRating(rating.getMid(), "RottenTomato").getMid(), equalTo(rating.getMid()));
    }

    /**
     * This method tests the updateRating method
     *
     * @throws ServiceException
     */
    @Test
    public void testUpdateRating() throws ServiceException {
        rating.setRating(6.0);
        ratingService.updateRating(rating);
        assertThat(ratingService.readRating(rating.getMid(), "RottenTomato").getRating(), equalTo(rating.getRating()));
    }

    /**
     * This method tests the deleteRating method
     *
     * @throws ServiceException
     */
    @Test
    public void testDeleteRating() throws ServiceException {
        List<Rating> ratings = ratingService.readAllRatings();
        assertThat(ratingService.readAllRatings().size(), equalTo(ratings.size()));
        for(Rating rating : ratings){
            ratingService.deleteRating(rating);
        }
        assertThat(ratingService.readAllRatings().size(), equalTo(0));
    }

    /**
     * This method tests the createUpdateUserRating method
     *
     * @throws ServiceException
     */
    @Test
    public void testCreateUpdateUserRating() throws ServiceException {
        ratingService.createUpdateUserRating(movie.getMid(), 6.0);

        rating = new Rating(movie.getMid(), 6.0, "User");
        rating = ratingService.create(rating);

        assertThat(ratingService.readRating(rating.getMid(), "User").getRating(), equalTo(rating.getRating()));

    }

    /**
     * This method tests the createUpdateUserRating method by inserting movieId null, which should fail
     *
     * @throws ServiceException
     */
    @Test(expected=ServiceException.class)
    public void testCreateUpdateUserRatingShouldFail() throws ServiceException {
        ratingService.createUpdateUserRating(-1, 5.0);
    }

    /**
     * This method tests the createUpdateTomatoRating method
     *
     * @throws ServiceException
     */
    @Test
    public void testCreateUpdateTomatoRating() throws ServiceException {
        rating = ratingService.createUpdateTomatoRating(movie.getMid(), 4.0);

        logger.info(ratingService.readRating(rating.getMid(), "RottenTomato").getRating());
        logger.info(rating.getRating());

        assertThat(ratingService.readRating(rating.getMid(), "RottenTomato").getRating(), equalTo(rating.getRating()));
    }

    /**
     * This method tests the readRatingsForMovie method
     *
     * @throws ServiceException
     */
    @Test
    public void testReadRatingsFromMovie() throws ServiceException {
        List<Rating> ratings = ratingService.readRatingsForMovie(movie);
        rating = new Rating(movie.getMid(), 6.0, "User");
        rating = ratingService.create(rating);
        assertThat(ratingService.readRatingsForMovie(movie).size(), equalTo(ratings.size() + 1));
    }
}
