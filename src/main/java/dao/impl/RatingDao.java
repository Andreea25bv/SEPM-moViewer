package dao.impl;

import dao.AbstactDaoManager;
import dao.IRatingDao;
import dto.Rating;
import exception.RatingPersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Ali Edan on ${25/11/2014}.
 */
//@Component
public class RatingDao extends AbstactDaoManager implements IRatingDao {

    private static final Logger logger = LogManager.getLogger();
    @Autowired
    private DriverManagerDataSource dataSource;

    @Override
    public Rating createRating(Rating rating) throws RatingPersistenceException {
        logger.debug("Entering createRatingMethod with parameters:" + rating);
        checkRatingValidation(rating);
        sql = "INSERT INTO Rating VALUES (?,?,?)";
        try {
            ps = dataSource.getConnection().prepareStatement(sql);
            ps.setInt(1, rating.getMid());
            ps.setDouble(2, rating.getRating());
            ps.setString(3, rating.getSource());

            ps.executeUpdate();

        } catch (SQLException s) {
            logger.error(s.getMessage());
            throw new RatingPersistenceException(s.getMessage(), s);
        }
        return rating;
    }

    @Override
    public List<Rating> readRatingById(Integer mid) throws RatingPersistenceException {
        logger.debug("Entering readRatingMethod with parameters:" + mid);
        if (mid == null) throw new IllegalArgumentException("Illegal Argument: mid can not be null");
        List<Rating> list = new ArrayList<Rating>();

        sql = "SELECT * FROM Rating WHERE mid = ?";

        try {
            ps = dataSource.getConnection().prepareStatement(sql);
            ps.setInt(1, mid);
            rs = ps.executeQuery();
            while(rs.next()){
                Rating r =  new Rating(rs.getInt(1), rs.getDouble(2), rs.getString(3));
                checkRatingValidation(r);
                list.add(r);
            }
        } catch (SQLException s) {
            logger.error(s.getMessage());
            throw new RatingPersistenceException(s.getMessage(), s);
        }
        return list;
    }

    @Override
    public Rating readRatingByIdSource(Integer mid, String source) throws RatingPersistenceException {
        //logger.debug("Entering readRatingMethod with parameters:" + mid);
        if (mid == null) throw new IllegalArgumentException("Illegal Argument: mid can not be null");
        Rating rating=null;

        sql = "SELECT * FROM Rating WHERE mid = ? AND source = ?";

        try {
            ps = dataSource.getConnection().prepareStatement(sql);
            ps.setInt(1, mid);
            ps.setString(2,source);
            rs = ps.executeQuery();
            while(rs.next()){
                 rating  =  new Rating(rs.getInt(1), rs.getDouble(2), rs.getString(3));

               }
        } catch (SQLException s) {
            logger.error(s.getMessage());
            throw new RatingPersistenceException(s.getMessage(), s);
        }
        checkRatingValidation(rating);
        return rating;
    }

    @Override
    public void updateRating(Rating rating) throws RatingPersistenceException {
        logger.debug("Entering updateRatingMethod with parameters:" + rating);
        checkRatingValidation(rating);
        sql = "UPDATE Rating SET rating = ? WHERE mid = ? AND source =?";
        try {
            ps = dataSource.getConnection().prepareStatement(sql);
            ps.setDouble(1, rating.getRating());
            ps.setString(3, rating.getSource());
            ps.setInt(2, rating.getMid());

            ps.executeUpdate();

        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RatingPersistenceException(e.getMessage(), e);
        }
    }

    @Override
    public void deleteRating(Rating rating) throws RatingPersistenceException {
        logger.debug("Entering deleteRatingMethod with parameters:" + rating);
        checkRatingValidation(rating);
        sql = "DELETE FROM Rating WHERE mid = ? AND source =?";
        try {
            ps = dataSource.getConnection().prepareStatement(sql);
            ps.setInt(1, rating.getMid());
            ps.setString(2, rating.getSource());
            ps.executeUpdate();

        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RatingPersistenceException(e.getMessage(), e);
        }
    }

    @Override
    public List<String> readAllSources() throws RatingPersistenceException {
        sql = " SELECT source FROM Rating";
        List<String> getAllSources = new ArrayList<String>();

        try {
            ps = dataSource.getConnection().prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                getAllSources.add(rs.getString(1));
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RatingPersistenceException(e.getMessage(), e);
        }
        return getResourcesList(getAllSources);
    }

    private List<String> getResourcesList(List<String> list) {
        List<String> check;
        List<String> restResources = new ArrayList<String>();

        check = list;
        Iterator<String> it = check.iterator();
        while (it.hasNext()) {
            String source = it.next();
            if (!restResources.contains(source)) {
                restResources.add(source);
            }
        }
        return restResources;
    }


    @Override
    public List<Rating> readAllRatings() throws RatingPersistenceException {
        sql = "SELECT * FROM Rating";
        List<Rating> l = new ArrayList<Rating>();
        try {
            ps = dataSource.getConnection().prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                l.add(new Rating(rs.getInt("mid"), rs.getDouble("rating"), rs.getString("source")));
            }

        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RatingPersistenceException(e.getMessage(), e);
        }
        return l;

    }


    /**
     * This method checks if one of the parameters of the rating object is null
     *
     * @param rating
     */
    private void checkRatingValidation(Rating rating) {
        if (rating == null) {
            throw new IllegalArgumentException("Rating object can not null");
        }
        if (rating.getMid() == null) {
            throw new IllegalArgumentException("Movie id can not be null");
        }
        if (rating.getRating() == null) {
            throw new IllegalArgumentException("Rating value can not be null");
        }
        if (rating.getSource() == null) {
            throw new IllegalArgumentException("Rating source can not be null");
        }
    }
}
