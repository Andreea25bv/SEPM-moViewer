import dto.Movie;
import dto.Subtitle;
import dto.SubtitleSearch;
import exception.MovieServiceException;
import exception.ServiceException;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import service.subtitle.WordCloud;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;

/**
 * Created by skYeYe on 25.01.2015.
 */
public class SubtitleServiceTest extends AbstractServiceTest {

    @Before
    public void setUpSubtitle() throws ServiceException, MovieServiceException {
        movie = new Movie();
        movie.setIdTMDB(5);
        movie.setIdIMDB(162222);
        movie.setPosterPath("/680X9apSqmAcebLg8evnnUeQNeI.jpg");
        movie = movieService.createMovie(movie);
        File arabicSub = new File((getClass().getClassLoader().getResource("CastAwayArabic.sub").getPath().replaceAll("%20", " ").toString()));
        String lang = subtitleService.addSubtitleLocal(movie,arabicSub);
        subtitleService.create(arabicSub.getName(),movie,lang, "local");
    }

    @After
    public void tearDownSubtitle() throws ServiceException {
        movie = null;
        subtitle = null;
    }

    /**
     * This method tests the addSubtitleLocal method together with the create-Methode of the subtitles.
     * @throws ServiceException
     */
    @Test
    public void testAddSubtitleLocalSuccessFull () throws ServiceException {
        int check1 = subtitleService.readSubtitlesForMovie(movie).size();
        File GermanSub = new File((getClass().getClassLoader().getResource("CastAwayGerman.srt").getPath().replaceAll("%20", " ").toString()));
        String t = subtitleService.addSubtitleLocal(movie, GermanSub);
        subtitleService.create(GermanSub.getName(),movie,t,"local");
        int check2 = subtitleService.readSubtitlesForMovie(movie).size();
        assertThat(check1, equalTo(check2 - 1));
    }

    /**
     * This method tests the addSubtitleLocal method if there would be an error in the Filepath of the
     * incoming file.
     * @throws ServiceException
     */
    @Test(expected = ServiceException.class)
    public void testAddSubtitleLocalFileNotExists() throws ServiceException {
        File i = new File("wrong directory");
        subtitleService.addSubtitleLocal(movie, i);
    }

    /**
     * This method tests the addsubtitleLocal method in reference to his fileName if it's contains
     * German, English, Italian or French.
     * @throws ServiceException
     */
    @Test
    public void testAddSubtitleLocalLang() throws ServiceException {
        File GermanSub = new File((getClass().getClassLoader().getResource("CastAwayGerman.srt").getPath().replaceAll("%20", " ").toString()));
        assertThat(subtitleService.addSubtitleLocal(movie, GermanSub), equalTo("German"));

        File EnglishSub = new File((getClass().getClassLoader().getResource("CastAwayEnglishImpaired.srt").getPath().replaceAll("%20", " ").toString()));
        assertThat(subtitleService.addSubtitleLocal(movie,EnglishSub), equalTo("English"));

        File ItalianSub = new File((getClass().getClassLoader().getResource("CastAwayItalian.srt").getPath().replaceAll("%20", " ").toString()));
        assertThat(subtitleService.addSubtitleLocal(movie,ItalianSub), equalTo("Italian"));

        File FrenchSub = new File((getClass().getClassLoader().getResource("CastAwayFrench.srt").getPath().replaceAll("%20", " ").toString()));
        assertThat(subtitleService.addSubtitleLocal(movie, FrenchSub), equalTo("French"));
    }

    /**
     * This methods test the create method in subtitle in reference to the language-length
     * @throws ServiceException
     */
    @Test(expected = ServiceException.class)
    public void testCreateSubtitleExceptionLang() throws ServiceException {
        subtitleService.create("filename", movie, "h", "");
    }

    /**
     * This methods test the searchSubtitle method in ServiceSubtitle
     * @throws ServiceException
     */
    @Test
    public void testSearchSubtitlesSuccessFull() throws ServiceException {
        subtitleService.login();
        List<SubtitleSearch> list = new ArrayList<SubtitleSearch>();
        int listsize1 = list.size();
        assertThat(listsize1 > 0, equalTo(false));
        list = subtitleService.searchSubtitles(movie);
        int listsize2 = list.size();
        assertThat(listsize2 > 0, equalTo(true));
        subtitleService.logout();
    }

    /**
     * This method tests the addSubtitleExtern method with successfully parameter
     * @throws ServiceException
    */
    @Test
    public void testAddSubtitleExternallySuccessFull() throws ServiceException {
        subtitleService.login();
        SubtitleSearch s = new SubtitleSearch("English", "sub", "yes", "http://dl.opensubtitles.org/en/download/file/src-api/vrf-6f3ff6e397/sid-ts1enpec94e7egsq5v3g4vlq16/21713.gz", "21713");
        subtitleService.addSubtitleExtern(s,movie);
        subtitleService.logout();
    }

    /**
     * This method tests the addSubtitleExtern method without login() & logout();
     * @throws ServiceException
     */
    @Test(expected = NullPointerException.class)
    public void testAddSubtitleExternallyException() throws ServiceException {
        SubtitleSearch s = new SubtitleSearch("English", "srt", "no", "dd", "");
        subtitleService.addSubtitleExtern(s,movie);
    }


    /**
     * This method tests the update method of the SubtitleService
     * @throws ServiceException
     */
    @Test
    public void testUpdateSubtitle() throws ServiceException {

        List<Subtitle> list = subtitleService.readSubtitlesForMovie(movie);
        subtitle = new Subtitle();
        subtitle = list.get(0);
        subtitle.setLanguage("haha");
        String lang = subtitle.getLanguage();
        subtitleService.update(movie,subtitle);
        List<Subtitle> list2 = subtitleService.readSubtitlesForMovie(movie);
        String lang2 = list.get(0).getLanguage();
        assertThat(lang, equalTo(lang2));
    }

    /**
     * This method test the update method of SubtitleService in reference to the ServiceException if the
     * length of the language would be smaller than 2.
     * @throws ServiceException
     */
    @Test(expected = ServiceException.class)
    public void testUpdateSubtitleException() throws ServiceException {
        List<Subtitle> list = subtitleService.readSubtitlesForMovie(movie);
        subtitle = new Subtitle();
        subtitle = list.get(0);
        subtitle.setLanguage("h");
        subtitleService.update(movie, subtitle);
    }

    /**
     * This method tests the readSubtitleForMovie-Method if Movie equals null
     * @throws ServiceException
     */
    @Test(expected = ServiceException.class)
    public void testReadSubtitlesFailedNull() throws ServiceException {
        subtitleService.readSubtitlesForMovie(null);
    }

    /**
     * This method tests the readSubtitleMovie-Method
     * @throws ServiceException
     */
    @Test
    public void testReadSubtitlesSuccessFull() throws ServiceException {
        List<Subtitle> check = subtitleService.readSubtitlesForMovie(movie);
        assertThat(check.size(), equalTo(1));
    }

    /**
     * This methods tests the delete Method of the SubtitleService
     * @throws ServiceException
     */
    @Test
    public void testDeleteSubtitle() throws ServiceException {
        List<Subtitle> check = subtitleService.readSubtitlesForMovie(movie);
        for (Subtitle s : check) {
                subtitleService.delete(s);
        }

        List<Subtitle> check2 = subtitleService.readSubtitlesForMovie(movie);
        assertThat(check.size(),equalTo(check2.size()+1));
    }

    /**
     * This methods test the commonSortedList method in ServiceSubtitle
     * @throws ServiceException
     */
    @Test
    public void testCommonSortedList() throws ServiceException {
        File EnglishImpairedSub = new File((getClass().getClassLoader().getResource("CastAwayEnglishImpaired.srt").getPath().replaceAll("%20", " ").toString()));
        String lang = subtitleService.addSubtitleLocal(movie, EnglishImpairedSub);
        subtitleService.create(EnglishImpairedSub.getName(),movie,lang, "local");
        List<Subtitle> check = subtitleService.readSubtitlesForMovie(movie);
        subtitle = check.get(1);
        List<WordCloud> wc = new ArrayList<WordCloud>();
        assertThat(wc.size() > 0, equalTo(false));
        wc = subtitleService.commonSortedList(subtitle);
        assertThat(wc.size() > 0, equalTo(true));
    }
}