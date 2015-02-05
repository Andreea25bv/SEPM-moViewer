import dao.query.QueryStubBuilder;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by skYeYe on 02.12.2014.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        MovieDaoTest.class,
        PersonDaoTest.class,
        RatingDaoTest.class,
        SubtitleDaoTest.class,
        QueryStubBuilderTest.class
})
public class TestSuiteDao {
}
