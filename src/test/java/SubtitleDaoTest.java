import dto.Movie;
import dto.Person;
import dto.Role;
import dto.Subtitle;
import exception.PersonPersistenceException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import exception.MoviePersistenceException;
import exception.SubtitlePersistenceException;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;

/**
 * Created by skYeYe on 01.12.2014.
 */
public class SubtitleDaoTest extends AbstractDaoTest {

    @Before
    public void setUpSubtitle() throws PersonPersistenceException {
        person = new Person(null, "jon doe", "biography", "photoPath");
        person = personDao.createPerson(person);
        role = new Role("testRole", person);
        staff = new ArrayList<Role>();
        staff.add(role);
        movie = new Movie(null, 5, 5, "testPath", false, "testTitle", 50, 1999, "posterPath", "german", "500000", "700000",
                "plotTest", "testTagLine", "listOfSimilarFilms", "testGenre", staff);
    }

    @After
    public void tearDownSubtitle(){
        movie = null;
        staff = null;
        role = null;
        person = null;
    }

    /**
     * This Method is to test the createSubtitle method by inserting new Subtitle and then search for it in the DB and
     * compare the Results.
     *
     * @throws MoviePersistenceException  if a Problem occurred
     * @throws SubtitlePersistenceException if a Problem occurred
     */
    @Test
    public void testCreateSubtitleThrowsNoException() throws MoviePersistenceException, SubtitlePersistenceException {
        movie = movieDao.createMovie(movie);
        subtitle = new Subtitle(null, movie.getMid(), "english", "what, you", "testPath", 300, "testCommon", 10000);
        subtitle = subtitleDao.createSubtitle(subtitle);
        int subtitleId = 0;
        for(int i = 0; i < subtitleDao.readSubtitle(movie.getMid()).size(); i++){
            subtitleId = subtitleDao.readSubtitle(movie.getMid()).get(i).getSid();
        }
        assertThat(subtitleId, equalTo(subtitle.getSid()));
    }

    /**
     * This Method test the createSubtitle when we try to insert null object
     *
     * @throws SubtitlePersistenceException if a Problem occurred
     * @throws IllegalArgumentException by inserting null object
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateSubtitleThrowsException() throws SubtitlePersistenceException {
        subtitle = new Subtitle(null, null, null, null, null, null, null, null);
        subtitleDao.createSubtitle(subtitle);
    }

    /**
     * This Method test the createSubtitle by inserting Subtitle with null Parameter
     *
     * @throws SubtitlePersistenceException if a Problem occurred
     * @throws IllegalArgumentException by inserting null Parameters.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateSubtitleThrowsExceptionMovieIdNull() throws SubtitlePersistenceException {
        subtitle = new Subtitle(null, null, "english", "What,why,....", "testPath", 300, "TestCommon", 20000);
        subtitleDao.createSubtitle(subtitle);
    }

    /**
     * This Method test the updateSubtitle by updating null object of Subtitle
     *
     * @throws SubtitlePersistenceException if a Problem occurred
     * @throws IllegalArgumentException by updating null objects
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateSubtitleThrowsException() throws SubtitlePersistenceException {
        subtitle = new Subtitle(null, null, null, null, null, null, null, null);
        subtitleDao.updateSubtitle(subtitle);
    }

    /**
     * This Method is to test the updateSubtitle by comparing the Results
     *
     * @throws SubtitlePersistenceException if a Problem occurred
     * @throws MoviePersistenceException if a Problem occurred
     */
    @Test
    public void testUpdateSubtitleThrowsNoException() throws SubtitlePersistenceException, MoviePersistenceException {
        movie = movieDao.createMovie(movie);
        subtitle = new Subtitle(null, movie.getMid(), "english", "what, you", "testPath", 300, "testCommon", 10000);
        subtitleDao.createSubtitle(subtitle);
        subtitle.setLanguage("french");
        subtitleDao.updateSubtitle(subtitle);
        String subtitleLanguage = null;
        for(int i = 0; i < subtitleDao.readSubtitle(movie.getMid()).size(); i++){
            subtitleLanguage = subtitleDao.readSubtitle(movie.getMid()).get(i).getLanguage();
        }
        assertThat(subtitleLanguage, equalTo(subtitle.getLanguage()));
    }

    /**
     * This Method test the updateSubtitle by updating the parameters of an Subtitle object but not the Id
     *
     * @throws SubtitlePersistenceException if a Problem occurred
     * @throws MoviePersistenceException if a Problem occurred
     */
    @Test
    public void testUpdateSubtitleThrowsNOExceptionUpdateAllP() throws SubtitlePersistenceException, MoviePersistenceException {
        movie = movieDao.createMovie(movie);
        subtitle = new Subtitle(null, movie.getMid(), "english", "what, you", "testPath", 300, "testCommon", 10000);
        subtitleDao.createSubtitle(subtitle);
        subtitle.setLanguage("french");
        subtitle.setComment("Update Common");
        subtitle.setCommonWords("TestCommonWord");
        subtitle.setLanguagePath("Update Subtitle Path");
        subtitle.setNumberOfWords(300000);
        subtitleDao.updateSubtitle(subtitle);
        Subtitle UpdatedSubtitle = null;
        for(int i = 0; i < subtitleDao.readSubtitle(movie.getMid()).size(); i++){
            UpdatedSubtitle = subtitleDao.readSubtitle(movie.getMid()).get(i);
        }
        assertThat(UpdatedSubtitle.getLanguage(), equalTo(subtitle.getLanguage()));
        assertThat(UpdatedSubtitle.getComment(), equalTo(subtitle.getComment()));
        assertThat(UpdatedSubtitle.getCommonWords(), equalTo(subtitle.getCommonWords()));
        assertThat(UpdatedSubtitle.getLanguagePath(), equalTo(subtitle.getLanguagePath()));
        assertThat(UpdatedSubtitle.getNumberOfWords(), equalTo(subtitle.getNumberOfWords()));
    }

    /**
     * This Method test the deleteSubtitle by inserting one then setting the mid null and try to delete it.
     *
     * @throws SubtitlePersistenceException if a Problem occurred
     * @throws MoviePersistenceException if a Problem occurred
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteSubtitleThrowsException() throws SubtitlePersistenceException, MoviePersistenceException {
        movie = movieDao.createMovie(movie);
        subtitle = new Subtitle(null, movie.getMid(), "english", "what, you", "testPath", 300, "testCommon", 10000);
        subtitleDao.createSubtitle(subtitle);
        subtitle.setMid(null);
        subtitleDao.deleteSubtitle(subtitle);
    }

    /**
     * This Method test the deleteSubtitle by inserting and then deleting it and then searching for it and testing
     *
     * @throws SubtitlePersistenceException
     * @throws MoviePersistenceException
     */
    @Test
    public void testDeleteSubtitleThrowsNoException() throws SubtitlePersistenceException, MoviePersistenceException {
        movie = movieDao.createMovie(movie);
        subtitle = new Subtitle(null, movie.getMid(), "english", "what, you", "testPath", 300, "testCommon", 10000);
        subtitle = subtitleDao.createSubtitle(subtitle);
        subtitleDao.deleteSubtitle(subtitle);
        int subtitleId;
        for(int i = 0; i < subtitleDao.readSubtitle(movie.getMid()).size(); i++){
            subtitleId = subtitleDao.readSubtitle(movie.getMid()).get(i).getSid();
            if(subtitleId == subtitle.getSid()){
                fail("Subtitle should not exit, because it has been deleted");
            }
        }
    }

    /**
     * This Method test the readSubtitle by inserting subtitle and checking the size of the DB size by searching
     * this the given id.
     *
     * @throws MoviePersistenceException
     * @throws SubtitlePersistenceException
     */
    @Test
    public void testReadSubtitleThrowsNoException () throws MoviePersistenceException, SubtitlePersistenceException {
        movie = movieDao.createMovie(movie);
        subtitle = new Subtitle(null, movie.getMid(), "English", "what, you", "testPath", 300, "testCommon", 10000);
        int subtitleListSize = subtitleDao.readSubtitle(movie.getMid()).size();
        subtitleDao.createSubtitle(subtitle);
        subtitleListSize++;
        assertThat(subtitleDao.readSubtitle(movie.getMid()).size(), equalTo(subtitleListSize));
    }

    /**
     * This Method test the readSubtitle by reading null object of Subtitle
     *
     * @throws SubtitlePersistenceException if a Problem occurred
     */
    @Test(expected = IllegalArgumentException.class)
    public void testReadSubtitleThrowsExceptionMidNull () throws SubtitlePersistenceException{
        subtitleDao.readSubtitle(null);
    }

    /**
     * This Method test the readSubtitle by inserting 2 subtitle and checking the size of the DB size by searching
     * this the given id.
     *
     * @throws MoviePersistenceException
     * @throws SubtitlePersistenceException
     */
    @Test
    public void testReadSubtitleThrowsNoException2 () throws MoviePersistenceException, SubtitlePersistenceException {
        movie = movieDao.createMovie(movie);
        int subtitleListSize = subtitleDao.readSubtitle(movie.getMid()).size();

        subtitle = new Subtitle(null, movie.getMid(), "english", "what, you", "testPath", 300, "testCommon", 10000);
        subtitle = subtitleDao.createSubtitle(subtitle);
        subtitleListSize++;
        assertThat(subtitleDao.readSubtitle(movie.getMid()).size(), equalTo(subtitleListSize));

        subtitle = new Subtitle(null, movie.getMid(), "french", "what, you", "testPath", 300, "testCommon", 10000);
        subtitle = subtitleDao.createSubtitle(subtitle);
        subtitleListSize++;
        assertThat(subtitleDao.readSubtitle(movie.getMid()).size(), equalTo(subtitleListSize));
    }
}
