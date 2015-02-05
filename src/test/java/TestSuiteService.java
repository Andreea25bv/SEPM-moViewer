import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by skYeYe on 23.01.2015.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        CrawlerServiceTest.class,
        TMDBServiceTest.class,
        MovieServiceTest.class,
        TrailerServiceTest.class,
        RatingServiceTest.class,
        RottenTomatoServiceTest.class,
        SubtitleServiceTest.class
        //VLCJServiceTest.class
})
public class TestSuiteService {
}
