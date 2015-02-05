package dao.query;

import dto.FilterParameter;
import exception.MoviePersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Created by Ali on 09/12/14.
 */
public class FilterQuery {

    private static final Logger logger = LogManager.getLogger(QueryStubBuilder.class);
    private final FilterParameter params;
    private QueryStubBuilder query;

    /**
     * This method call the methods from the class QueryStubBuilder
     * to build the needed Query for the Find Method in MovieDao
     *
     * @param params
     */
    public FilterQuery(FilterParameter params) {
        query = new QueryStubBuilder();
        query.addSelectField("Distinct(m.mid)");
        query.addSelectField("m.idTMDB");
        query.addSelectField("m.idIMDB");
        query.addSelectField("m.path");
        query.addSelectField("m.favorite");
        query.addSelectField("m.title");
        query.addSelectField("m.runTime");
        query.addSelectField("m.year");
        query.addSelectField("m.posterPath");
        query.addSelectField("m.language");
        query.addSelectField("m.budget");
        query.addSelectField("m.revenue");
        query.addSelectField("m.plot");
        query.addSelectField("m.tagLine");
        query.addSelectField("m.similarFilms");
        query.addSelectField("m.genre");
        query.addSelectField("m.ignore");


        query.addFromTable("Movie m");
        query.addLeftJoinTable("Rating r");
        query.addLeftJoinTable("(m.mid = r.mid)");
        query.addWhereAndCondition("(LCASE(m.title) LIKE ? OR ? IS NULL)");
        query.addWhereAndCondition("(LCASE(m.language) LIKE  ? OR ? IS NULL)");
        query.addWhereAndCondition("((r.rating >= ? OR ? IS NULL)");
        query.addWhereAndCondition("(r.rating <= ? OR ? IS NULL))");
        query.addWhereAndCondition("(LCASE(m.genre) LIKE ? OR ? IS NULL)");
        query.addWhereAndCondition("(m.year = ? OR ? IS NULL)");
        query.addWhereAndCondition("(m.runtime <= ? OR ? IS NULL)");
        query.addWhereAndCondition("(LCASE(r.source) = ? OR ? IS NULL)");
        query.addWhereAndCondition("(m.favorite = ? OR ? IS NULL)");
        query.addSortTable();


        this.params = params;
    }

    /**
     * This method is to set Null where Integer is expected in case
     * that the Integer value is null
     *
     * @param ps    (PreparedStatement)
     * @param pos   (Position of the parameter in the PreparedStatement)
     * @param value (Value of the parameter)
     * @throws SQLException if a problem occur
     */
    private static void setIntOrNull(PreparedStatement ps, int pos,
                                     Integer value) throws SQLException {
        if (value != null) {
            ps.setInt(pos, value);
        } else {
            ps.setNull(pos, java.sql.Types.INTEGER);
        }
    }

    private static void setDoubleOrNull(PreparedStatement ps, int pos,
                                        Double value) throws SQLException {
        if (value != null) {
            ps.setDouble(pos, value);
        } else {
            ps.setNull(pos, Types.DOUBLE);
        }
    }

    /**
     * This method is to set Null where Boolean is expected in case
     * that the Boolean value is null
     *
     * @param ps    (PreparedStatement)
     * @param pos   (Position of the parameter in the PreparedStatement)
     * @param value (Value of the parameter)
     * @throws SQLException if a problem occur
     */
    private static void setBooleanOrNull(PreparedStatement ps, int pos,
                                         Boolean value) throws SQLException {
        if (value != null) {
            ps.setBoolean(pos, value);
        } else {
            ps.setNull(pos, Types.BOOLEAN);
        }
    }

    /**
     * This method is to set the PreparedStatement with it is Parameters
     * in case there is problem by building the Query the method will
     * throw SQlException which is wrapped into MoviePersistenceException
     *
     * @param connection
     * @return ps (the built PreparedStatement)
     * @throws MoviePersistenceException
     */
    public PreparedStatement getPreparedStatement(Connection connection)
            throws MoviePersistenceException {
        try {

            PreparedStatement ps = connection.prepareStatement(query.getQuery());
            if (params.getRatingFrom() == null && params.getRatingTill() == null) {

            } else if (params.getRatingFrom() ==
                    0.0 && params.getRatingTill() == 10.0) {
                params.setRatingFrom(null);
                params.setRatingTill(null);
            }

            // logger.debug(params);

            ps.setString(1, getLikeString(params.getTitle()));
            ps.setString(2, getLikeString(params.getTitle()));

            ps.setString(3, getLikeString(params.getLanguage()));
            ps.setString(4, getLikeString(params.getLanguage()));
            setDoubleOrNull(ps, 5, params.getRatingFrom());
            setDoubleOrNull(ps, 6, params.getRatingFrom());
            setDoubleOrNull(ps, 7, params.getRatingTill());
            setDoubleOrNull(ps, 8, params.getRatingTill());


            ps.setString(9, getLikeString(params.getGenre()));
            ps.setString(10, getLikeString(params.getGenre()));


            setIntOrNull(ps, 11, params.getYear());
            setIntOrNull(ps, 12, params.getYear());

            setIntOrNull(ps, 13, params.getRunTime());
            setIntOrNull(ps, 14, params.getRunTime());

            ps.setString(15, params.getSourceRating());
            ps.setString(16, params.getSourceRating());

            setBooleanOrNull(ps, 17, params.getFavorite());
            setBooleanOrNull(ps, 18, params.getFavorite());





            logger.debug(ps);

            return ps;
        } catch (SQLException e) {
            throw new MoviePersistenceException(e.getMessage(), e);
        }
    }

    /**
     * This method is to build the Like Statement in the Query for the Strings
     * which uses the LIKE Statement
     *
     * @param s
     * @return string ready to set in the query for the LIKE Statement
     */
    private String getLikeString(String s) {
        if (s == null)
            return null;

        return "%" + s + "%";
    }


}
