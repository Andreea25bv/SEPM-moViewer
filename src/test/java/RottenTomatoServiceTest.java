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
public class RottenTomatoServiceTest extends AbstractServiceTest {

    @Before
    public void setUpTomato() throws ServiceException, MovieServiceException {
        movie = new Movie();
        movie.setTitle("Pitch Black");
        movie.setYear(2000);
        movie.setPosterPath("/680X9apSqmAcebLg8evnnUeQNeI.jpg");
        movie = movieService.createMovie(movie);
        rating = new Rating(movie.getMid(), 5.0, "RottenTomatos");
        rating = ratingService.create(rating);
    }

    @After
    public void tearDownTomato() {
        movie = null;
        rating = null;
    }

    /**
     * This method tests the getMovieScore and the searchMovie method of RottenTomatoService
     *
     * @throws ServiceException
     */
    @Test
    public void testGetMovieScoreAndSearchMovie() throws ServiceException {
        rating = ratingService.createUpdateTomatoRating(movie.getMid(),
                tomatoService.getMovieScore(tomatoService.searchMovie(movie.getTitle(), movie.getYear())));
        assertThat(ratingService.readRating(movie.getMid(), "RottenTomato").getRating(), equalTo(rating.getRating()));
    }


    /**
     * This method tests the getMovieReview method, that gives 5 reviews back from rottenTomatos
     *
     * @throws ServiceException
     */
    @Test
    public void testGetMovieReview() throws ServiceException {
        List<String> rating = tomatoService.getMovieReview(tomatoService.searchMovie(movie.getTitle(), movie.getYear()));
        logger.info(rating.toString());
        assertThat(rating.size(), equalTo(tomatoService.getLimit()));
    }
}
