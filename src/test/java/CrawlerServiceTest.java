import org.junit.Test;
import service.ICrawlerService;
import service.impl.CrawlerServiceImpl;

import java.io.File;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
/**
 * Created by skYeYe on 16.12.2014.
 */
public class CrawlerServiceTest extends AbstractServiceTest {

    /**
     * Test will essentially run a rudimentary user-story.
     * Scanner is set to a path, then a scan is performed,
     * a check is made if the scan contained the right file first
     * and if there are more than two files in the scanned movie-list.
     * Then the scanned-list is cleared.
     */
    @Test
    public void testCrawler0() {
        Path path = new File("./").toPath();
        crawler.setPath(path);
        crawler.scan();
        logger.info(crawler.getMovies());
        assertThat(crawler.getMovies().get(0).getTitle(), equalTo("Bad Boys 2"));
        assertThat(crawler.getMovies().size() > 2, equalTo(true));
        crawler.clear();
    }

    /**
     * Test is only to get 100% coverage. Disregard if it does not work for your
     * implementation.
     */
    @Test
    public void testCrawler1() {
        ICrawlerService service = new CrawlerServiceImpl("./");
        Path path = new File("./").toPath();
        crawler.setPath(path);
        crawler.setFile("Cast Away.wmv");
        crawler.scan();
        logger.info(crawler.getMovies());
        assertThat(crawler.getMovies().get(0).getTitle(), equalTo("Cast Away"));
        crawler.clear();    }
}
