import dao.query.QueryStubBuilder;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Ali on 09/12/14.
 */
public class QueryStubBuilderTest extends AbstractDaoTest {

    @Test
    public void testFilterQueryBuilder1() {
        query = new QueryStubBuilder();
        query.addSelectField("*");
        query.addFromTable("Movie");
        query.addLeftJoinTable("Rating");
        query.addLeftJoinTable("(rating.mid = movie.mid)");
        query.addWhereAndCondition("(language = ? OR ? IS NULL)");
        query.addWhereAndCondition("(genre LIKE ? OR ? IS NULL)");

        assertThat(query.getQuery(), equalTo("SELECT * FROM Movie left outer Join Rating ON " +
                "(rating.mid = movie.mid) WHERE (language = ? OR ? IS NULL) AND (genre LIKE ? OR ? IS NULL) "));
    }

    @Test
    public void testFilterQueryBuilder2() {
        query = new QueryStubBuilder();
        query.addSelectField("*");
        query.addFromTable("Test");
        query.addWhereAndCondition("foo = 1");
        query.addWhereAndCondition("bar = 2");
        assertThat(query.getQuery(), equalTo("SELECT * FROM Test WHERE foo = 1 AND bar = 2 null"));
    }

    /**
     * Test if one of the Paramter is Null will throw IllegalStateException
     *
     * @throws IllegalStateException coy Table name is null
     */
    @Test(expected = IllegalStateException.class)
    public void testFilterQueryBuilder3ThrowsException() {


        query = new QueryStubBuilder();

        query.addSelectField("*");
        query.addWhereAndCondition("foo = 1");
        query.addWhereAndCondition("bar = 2");
        query.getQuery();
    }

    /**
     * Test if one of the Paramter is Null will throw IllegalStateException
     *
     * @throws IllegalStateException Select Statment is null
     */
    @Test(expected = IllegalStateException.class)
    public void testFilterQueryBuilder4ThrowsException() {


        query = new QueryStubBuilder();

        query.addFromTable("Movie");
        query.addWhereAndCondition("foo = 1");
        query.addWhereAndCondition("bar = 2");
        query.getQuery();
    }

    /**
     * Test if one of the Paramter is Null will throw IllegalStateException
     *
     * @throws IllegalStateException Condition is null
     */
    @Test//(expected = IllegalStateException.class)
    public void testFilterQueryBuilder5ThrowsException() {


        query = new QueryStubBuilder();

        query.addSelectField("*");
        query.addFromTable("Movie");
        query.addWhereAndCondition(null);
        query.getQuery();
    }
}
