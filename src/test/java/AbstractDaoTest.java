import dao.IMovieDao;
import dao.IPersonDao;
import dao.IRatingDao;
import dao.ISubtitleDao;
import dao.query.QueryStubBuilder;
import dto.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by skYeYe on 30.11.2014.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-test-config.xml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AbstractDaoTest {

    protected static final Logger logger = LogManager.getLogger();

    protected Movie movie;
    protected Person person;
    protected Subtitle subtitle;
    protected List<Role> staff;
    protected Role role;
    protected Rating rating;
    protected QueryStubBuilder query;

    @Autowired
    protected IPersonDao personDao;
    @Autowired
    protected IMovieDao movieDao;
    @Autowired
    protected ISubtitleDao subtitleDao;
    @Autowired
    protected IRatingDao ratingDao;
}