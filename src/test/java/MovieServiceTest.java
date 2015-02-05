import dto.FilterParameter;
import dto.Movie;
import exception.MovieServiceException;
import exception.ServiceException;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;

import static junit.framework.Assert.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by skYeYe on 04.12.2014.
 */
public class MovieServiceTest extends AbstractServiceTest {

    @Before
    public void setUpMovie() throws ServiceException, MovieServiceException {
        movie = new Movie();
        movie.setPosterPath("/680X9apSqmAcebLg8evnnUeQNeI.jpg");
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
     * This method tests the Create and Read Method of Movie Service
     *
     * @throws ServiceException
     */
    @Test
    public void testCreateValidMovie() throws ServiceException {
        assertThat(movieService.readMovie(movie.getMid()).getMid(), equalTo(movie.getMid()));
    }

    /**
     * This method tests if a valid movie is correctly added to favourites
     *
     * @throws ServiceException
     */
    @Test
    public void testAddToFavoriteValidMovie() throws ServiceException, MovieServiceException {
        movie = movieService.createMovie(movie);
        //at first the movie is not favorite
        assertThat(movieService.readMovie(movie.getMid()).getFavorite(), equalTo(false));
        assertThat(movie.getFavorite(), equalTo(false));

        //now the method that makes the movie favorite is called
        movieService.addToFavorite(movie);

        //the movie (both object, and DB entry) should have favorite=true
        assertThat(movieService.readMovie(movie.getMid()).getFavorite(), equalTo(true));
        assertThat(movie.getFavorite(), equalTo(true));

    }

    /**
     * This method tests if a valid movie is correctly removed from favourites
     *
     * @throws ServiceException
     */
    @Test
    public void testRemoveFromFavoriteValidMovie() throws ServiceException, MovieServiceException {
        movie = movieService.createMovie(movie);
        //at first the movie is not favorite
        assertThat(movieService.readMovie(movie.getMid()).getFavorite(), equalTo(false));
        assertThat(movie.getFavorite(), equalTo(false));

        //now the method that makes the movie favorite is called
        movieService.addToFavorite(movie);
        //the movie (both object, and DB entry) should have favorite=true
        assertThat(movieService.readMovie(movie.getMid()).getFavorite(), equalTo(true));
        assertThat(movie.getFavorite(), equalTo(true));

        //now the method that removes movie feom favorite is called
        movieService.removeFromFavorite(movie);
        //the movie (both object, and DB entry) should have favorite=false
        assertThat(movieService.readMovie(movie.getMid()).getFavorite(), equalTo(false));
        assertThat(movie.getFavorite(), equalTo(false));

    }

    /**
     * This method tests the update Movie, by updating the Language, Runtime and Year
     *
     * @throws ServiceException
     * @throws MovieServiceException
     */
    @Test
    public void testUpdateMovie() throws ServiceException, MovieServiceException {
        movie.setLanguage("de");
        movie.setRunTime(100);
        movie.setYear(2000);
        movieService.updateMovie(movie);
        assertThat(movieService.readMovie(movie.getMid()).getLanguage(), equalTo(movie.getLanguage()));
    }

    /**
     * This method tests the update Movie, by updating the Language to empty String which should fail
     *
     * @throws ServiceException
     * @throws MovieServiceException
     */
    @Test(expected = MovieServiceException.class)
    public void testUpdateMovieShouldFail() throws ServiceException, MovieServiceException {
        movie.setLanguage("");
        movieService.updateMovie(movie);
    }

    /**
     * This method tests the delete method, by deleting all the movies inside the database.
     * After deleting all movies, the readAllMovies Method should be empty.
     *
     * @throws ServiceException
     * @throws MovieServiceException
     */
    @Test
    public void testDeleteMovie() throws ServiceException {
        List<Movie> movies = movieService.readAllMovies(false);
        for (int i = 0; i < movies.size(); i++) {
            movieService.deleteMovie(movies.get(i));
        }
        Iterator<Movie> movieIterator = movies.iterator();
        while(movieIterator.hasNext()){
            Movie m = movieIterator.next();
            movieIterator.remove();
        }
        assertThat(movieService.readAllMovies().size(), equalTo(0));
    }

    /**
     * This method tests the setting of favourite-state feature of a movie
     *
     * @throws ServiceException
     */
    @Test
    public void testMovieFavorite() throws ServiceException {
        movieService.addToFavorite(movie);
        String compare = null;
        if(movieService.isFavourite(movie)) compare = "YES";
        assertThat(compare, equalTo(movie.getIsFavorite()));
        movieService.removeFromFavorite(movie);
        assertThat(movieService.readMovie(movie.getMid()).getIsFavorite(), equalTo(movie.getIsFavorite()));
    }

    /**
     * This method tests the setting of ignored-state of a movie
     *
     * @throws ServiceException
     */
    @Test
    public void testSetMovieIgnored() throws ServiceException {
        List<Movie> ignoredMovies = movieService.readAllMovies(true);
        movieService.setMovieIgnored(movie);
        //logger.info(movieService.readMovie(movie.getMid()).toString());
        assertThat(movieService.readAllMovies(true).size(), equalTo(ignoredMovies.size()+1));
    }

    /**
     * This method tests the showNextMovie and the showPreviousMovie method
     *
     * @throws ServiceException
     * @throws MovieServiceException
     */
    @Test(expected = ServiceException.class)
    public void testShowNextMovie() throws ServiceException, MovieServiceException {
        Movie nextMovie = new Movie();
        try {
            nextMovie.setTitle("nextMovie");
            nextMovie = movieService.createMovie(nextMovie);
            assertThat(movieService.showNextMovie(movie).getTitle(), equalTo(nextMovie.getTitle()));
            assertThat(movieService.showPreviousMovie(nextMovie).getTitle(), equalTo(movie.getTitle()));
        }catch(Exception e){
            fail("the test shouldn't fail here");
        }
        movieService.showNextMovie(nextMovie);
    }

    /**
     * This method tests the find method searching for movie when rating from smaller
     * than rating till
     *
     * @throws ServiceException
     */
    @Test(expected = ServiceException.class)
    public void testFindServiceMethod1() throws ServiceException {
        FilterParameter params = new FilterParameter();
        params.setRatingFrom(5.0);
        params.setRatingTill(3.0);
        movieService.find(params);

    }

    /**
     * This method tests the find method when i search for filme with
     * year smaller than 1800 will throw exception
     *
     * @throws ServiceException
     */
    @Test(expected = ServiceException.class)
    public void testFindServiceMethod2() throws ServiceException {
        FilterParameter params = new FilterParameter();
        params.setRatingFrom(5.0);
        params.setRatingTill(6.0);
        params.setYear(5);
        movieService.find(params);

    }

    /**
     * This method tests the heuristic method when all parameters
     * are null there will be an exception
     *
     * @throws ServiceException
     */
    @Test(expected = ServiceException.class)
    public void testHeuristicService1() throws ServiceException {
        movieService.heuristic(null, null, null, null);
    }

    /**
     * This method tests the heuristic method when all parameters
     * are null there will be an exception
     *
     * @throws ServiceException
     */
    @Test
    public void testHeuristicService2() throws ServiceException, MovieServiceException {

        Movie nextMovie = new Movie();
        try {
            nextMovie.setTitle("appppppppppppp");
            nextMovie.setGenre("newGenre");
           /*
            List<Role> staff = new ArrayList<>();
            person = new Person(null, "MEMEME", "biography", "/xxPMucou2wRDxLrud8i2D4dsywh.jpg");
            person = .createPerson(person);
            role = new Role("testRole", person);
            staff = new ArrayList<Role>();
            staff.add(role);
            Movie nextMovie = new Movie(null, 5, 5, "testPath", false, "testTitle", 50, 1999, "/680X9apSqmAcebLg8evnnUeQNeI.jpg", "german", "500000", "700000",
                    "plotTest", "testTagLine", "listOfSimilarFilms", "testGenre", staff);
*/
            nextMovie = movieService.createMovie(nextMovie);
            List<Movie> check = movieService.heuristic(nextMovie.getTitle(), "newGenre", " ", null);
            assertThat(check.size(), equalTo(1));
        } catch (Exception e) {
            fail("test is failed ");
        }

    }



    /**
     * This method tests the changePoster method, first the target/test-classes/hauptuni_orig.png is reset.
     * Then a movie-object is created with the testing/hauptuni_orig.png picture. After that the orig
     * picture is compared with the target/test-classes/hauptuni_orig.png picture. Then the picture in
     * target/test-classes/hauptuni_orig.png is to be converted with changePoster methode with
     * testing/hauptuni_umk.png picture. At the end, the testing/hauptuni_umk.png picture has to match with the
     * target/test-classes/hauptuni_orig.png to check if replacement worked. At the end, we assert that
     * the posterPath of the picture hasn't changed.
     *
     * @throws ServiceException
     * @throws MovieServiceException
     */
    @Test
    public void testChangePoster() throws ServiceException, MovieServiceException, IOException {
        File testingOrig = null;
        if(System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0) {
            testingOrig = new File("testing/hauptuni_orig.png");
        }
        if(System.getProperty("os.name").toLowerCase().indexOf("linux") >= 0) {
            testingOrig = new File("testing/hauptuni_orig.png");
        }
        String path = MovieServiceTest.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " ");
        File pictureToReset = null;
        if(System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0) {
            pictureToReset = new File(path.substring(1) + "hauptuni_orig.png");
        }
        if(System.getProperty("os.name").toLowerCase().indexOf("linux") >= 0) {
            pictureToReset = new File(path + "hauptuni_orig.png");
        }
        FileUtils.copyFile(testingOrig, pictureToReset);
        byte[] oImage = Files.readAllBytes(testingOrig.toPath());
        movie.setPosterPath(testingOrig.getAbsolutePath());
        movie.setLanguage("de");
        movie.setRunTime(100);
        movie.setYear(2000);
        movieService.updateMovie(movie);
        File targetOrig = new File((getClass().getClassLoader().getResource("hauptuni_orig.png").getPath().replaceAll("%20", " ").toString()));
        byte[] imageCompare = Files.readAllBytes(targetOrig.toPath());
        assertThat(oImage, equalTo(imageCompare));

        File testingUmk = null;
        if(System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0) {
            testingUmk = new File("testing/hauptuni_umk.png");
        }
        if(System.getProperty("os.name").toLowerCase().indexOf("linux") >= 0) {
            testingUmk = new File("testing/hauptuni_umk.png");
        }
        byte[] uImage = Files.readAllBytes(testingUmk.toPath());
        movieService.changePoster(movie, testingUmk, targetOrig); //inImage substitutes outImage
        targetOrig = new File((getClass().getClassLoader().getResource("hauptuni_orig.png").getPath().replaceAll("%20", " ").toString()));
        imageCompare = Files.readAllBytes(targetOrig.toPath());
        assertThat(uImage, equalTo(imageCompare));

        //assertThat moviePosterPath didn't change
        assertThat(movieService.readMovie(movie.getMid()).getPosterPath(), equalTo(movie.getPosterPath()));
    }
}
