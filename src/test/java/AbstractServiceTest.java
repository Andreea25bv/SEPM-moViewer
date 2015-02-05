import dto.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import service.*;

import java.util.List;

/**
 * Created by skYeYe on 14.12.2014.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-test-config.xml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AbstractServiceTest {

    protected static final Logger logger = LogManager.getLogger();

    protected Movie movie;
    protected Person person;
    protected Subtitle subtitle;
    protected List<Role> staff;
    protected Role role;
    protected Rating rating;

    @Autowired
    protected IMovieService movieService;
    @Autowired
    protected ICrawlerService crawler;
    @Autowired
    protected IRatingService ratingService;
    @Autowired
    protected IRottenTomatoService tomatoService;
    @Autowired
    protected ISubtitleService subtitleService;
    @Autowired
    protected ITMDBService api;
}
