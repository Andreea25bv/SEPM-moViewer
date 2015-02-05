import dto.Movie;
import dto.Trailer;
import exception.MovieServiceException;
import exception.ServiceException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import service.impl.TrailerServiceImpl;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by skYeYe on 09.01.2015.
 */
public class TrailerServiceTest extends AbstractServiceTest {

    @Before
    public void setUpTrailer() throws ServiceException, MovieServiceException {
        movie = new Movie();
        movie = movieService.createMovie(movie);
    }

    @After
    public void tearDownTrailer() {
        movie = null;
        staff = null;
        role = null;
        person = null;
    }

    /**
     * This method tests the findTrailer Method for the movie Bad Boys 2
     * also testing, if all the trailers are in the dto
     */
    @Test
    public void testFindTrailersOfMovie() {
        //System.getProperties().put("org.apache.commons.logging.simplelog.defaultlog", "fatal");
        movie.setTitle("Bad Boys 2");
        String searchText = movie.getTitle() + " trailer";
        TrailerServiceImpl trailerService = new TrailerServiceImpl();
        List<WebElement> trailerList = trailerService.youtubeTrailerList(searchText);
        for(int i = 0; i < trailerList.size(); i++){
//            logger.info(trailerList.get(i).getText() +", "+ trailerList.get(i).getAttribute("href"));
        }
        assertThat(trailerService.getCurrentUrl(), equalTo("https://www.youtube.com/results?search_query=Bad+Boys+2+trailer"));
        List<Trailer> dtoTrailerList = trailerService.getAllTrailers(searchText);
        for(int i = 0; i < dtoTrailerList.size(); i++){
//            logger.info(dtoTrailerList.get(i).toString());
        }
        assertThat(trailerList.size(), equalTo(dtoTrailerList.size()));
    }
}
