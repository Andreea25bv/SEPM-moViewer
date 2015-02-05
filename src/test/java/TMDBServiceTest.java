import dto.Movie;
import exception.ServiceException;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by markus on 25.01.15.
 */
public class TMDBServiceTest extends AbstractServiceTest {

    /**
     * This Method test the APIService, by searching for the Movie
     * Bad Boys II, with Parameters Year = 2003, Language = en
     *
     * @throws exception.ServiceException
     */
    @Test
    public void testAPIService() throws ServiceException {
        Movie m = new Movie();
        m.setTitle("Bad Boys II");
        List<Movie> movies;
        movies = api.search(m.getTitle(), 2003, "en", false, 1);
        assertThat(movies.get(0).getTitle(), equalTo(m.getTitle()));
    }

    /**
     * This Method test the getPersonByID of TMBD, by searching for the Person 1
     * on TMBD, which should equal to George Lucas
     *
     * @throws ServiceException
     */
    @Test
    public void testGetPersonByTMDBID() throws ServiceException {
        assertThat(api.getPersonByTMDBID(1).getName(), equalTo("George Lucas"));
    }

    /**
     * Test will fail because this id is not taken yet.
     * @throws ServiceException
     */
    @Test (expected = ServiceException.class)
    public void testGetPersonByTMDBID1() throws ServiceException {
        assertThat(api.getPersonByTMDBID(1249816498).getName(), equalTo("George Lucas"));
    }

    /**
     * This Method test the getMovieByID of TMBD, by searching for the Movie
     * Bad Boys II, with Parameters Year = 2003, Language = en
     * Assert that the Title is equal by searching for the IdTMDB
     *
     * @throws ServiceException
     */
    @Test
    public void testGetMovieByTMDBID() throws ServiceException {
        Movie movie = new Movie();
        movie.setTitle("The Mosquito Coast");

        List<Movie> movies;
        movies = api.search(movie.getTitle(), 1986, "en", false, 1);
        movie.setIdTMDB(movies.get(0).getIdTMDB());
        assertThat(api.getMovieByTMDBID(movie.getIdTMDB(), "").getTitle(), equalTo(movie.getTitle()));
    }

    /**
     * This Method test the searchAppend Method, by searching for the Movie
     * Bad Boys II, with Parameters Year = 2003, Language = en
     *
     * @throws ServiceException
     */
    @Test
    public void testSearchAppend0() throws ServiceException {
        Movie m = new Movie();
        m.setTitle("Cast Away");
        List<Movie> movies;
        movies = api.searchAppend(m.getTitle(), 2000, "en", false, 1);
        assertThat(movies.get(0).getTitle(), equalTo(m.getTitle()));
    }

    /**
     * Search hits the result in the first try, no iteration
     * @throws ServiceException
     */
    @Test
    public void testSearchAppend1() throws ServiceException {
        List<Movie> movies;
        movies = api.searchAppend("Cast Away ijefqeiufqö", 2000, "en", false, 0);
        assertThat(movies.get(0).getTitle(), equalTo("Cast Away"));
    }

    /**
     * Search iterates over the string and finds the proper name.
     * And it fails.
     * @throws ServiceException
     */
    @Test
    public void testSearchAppend2() throws ServiceException {
        List<Movie> movies;
        movies = api.searchAppend("Pirates of the Carribean 1", 0, "en", false, 0);
        logger.info(movies.get(0).getTitle());
        assertThat(movies.get(0).getTitle(), equalTo("Pirates of the Airwaves"));
    }
}
