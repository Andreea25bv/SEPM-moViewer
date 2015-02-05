import dto.Movie;
import exception.MovieServiceException;
import exception.ServiceException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * Created by skYeYe on 23.01.2015.
 */
public class VLCJServiceTest extends AbstractServiceTest {

    @Before
    public void setUpMovie() throws ServiceException, MovieServiceException {
        movie = new Movie();
        movie = movieService.createMovie(movie);
    }

    @After
    public void tearDownMovie() {
        movie = null;
        staff = null;
        role = null;
        person = null;
    }

    /**
     * This method tests which operating system is running
     *
     * @throws exception.ServiceException
     */
    @Test
    public void testPlayMovieAndShowMovieExplorer() throws ServiceException {
        File file = new File(this.getClass().getClassLoader().getResource("Sunshine.avi").getPath().replaceAll("%20", " ").toString());
//        logger.info(file.getAbsoluteFile());
//        logger.info(file.getAbsolutePath());
        movie.setPath(file.getAbsolutePath());
        movieService.playMovie(movie);
        if(System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0) {
            movieService.showMovieExplorer(movie);
        }
    }
}
