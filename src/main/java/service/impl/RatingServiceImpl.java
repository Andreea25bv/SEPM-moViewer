package service.impl;

import dao.IMovieDao;
import dao.IRatingDao;
import dto.Movie;
import dto.Rating;
import exception.MoviePersistenceException;
import exception.RatingPersistenceException;
import exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import service.IRatingService;

import java.util.List;

/**
 * Created by Andreea on 09/12/2014.
 */
public class RatingServiceImpl implements IRatingService {

    private static final Logger logger = LogManager.getLogger(MovieServiceImpl.class);

    @Autowired
    private IRatingDao ratingDao;
    @Autowired
    private IMovieDao movieDao;

    @Override
    public Rating create(Rating r) throws ServiceException {
        validateRating(r);

        try {
            r = ratingDao.createRating(r);
            Movie m = movieDao.readMovieById(false, r.getMid());
            m.getRatingList().add(r);

        } catch (RatingPersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage(), e);
        } catch (MoviePersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage(), e);
        }

        return r;
    }

    @Override
    public Rating readRating(Integer mid, String source) throws ServiceException {
        if (!ratingExists(mid, source)) {
            throw new ServiceException("Rating for Movie with this mid does not exist");
        }
        Rating r = null;
        if (mid < 0) throw new ServiceException("mid can't be smaller than zero");
        try {
            r = ratingDao.readRatingByIdSource(mid, source);
        } catch (RatingPersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage(), e);
        }

        return r;
    }

    @Override
    public void updateRating(Rating r) throws ServiceException {
        if (!ratingExists(r.getMid(), r.getSource())) {
            throw new ServiceException("Rating for Movie with this mid does not exist");
        }

        validateRating(r);

        try {
            ratingDao.updateRating(r);
        } catch (RatingPersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void deleteRating(Rating r) throws ServiceException {
        if (!ratingExists(r.getMid(), r.getSource())) {
            throw new ServiceException("Rating with this mid does not exist");
        }

        try {
            ratingDao.deleteRating(r);

            Movie m = movieDao.readMovieById(false, r.getMid());
            m.getRatingList().remove(r);
            movieDao.updateMovie(m);
        } catch (RatingPersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage(), e);
        } catch (MoviePersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage(), e);
        }
    }


    private void validateRating(Rating r) throws ServiceException {
        if (r == null) {
            throw new ServiceException("Rating cannot be null");
        }
        if (r.getMid() < 0) {
            throw new ServiceException("Movie id cannot be negative");
        }
        if (r.getSource().isEmpty()) {
            throw new ServiceException("Rating source cannot be empty");
        }

    }

    private boolean ratingExists(Integer mid, String source) throws ServiceException {

        List<Rating> list = null;
        try {
            list = ratingDao.readAllRatings();
        } catch (RatingPersistenceException e) {
            e.printStackTrace();
        }
        for (Rating r : list) {
            if (r.getMid() == mid && r.getSource().equals(source)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public Rating createUpdateUserRating(Integer mid, Double rating) throws ServiceException {
        Rating r = new Rating(mid, rating, "User");

        if (ratingExists(r.getMid(), r.getSource())) {
            this.updateRating(r);
        } else {
            this.create(r);
        }

        return r;
    }

    @Override
    public Rating createUpdateTomatoRating(Integer mid, Double rating) throws ServiceException {
        Rating r = new Rating(mid, rating, "RottenTomato");

        if (ratingExists(r.getMid(), r.getSource())) {
            this.updateRating(r);
        } else {
            this.create(r);
        }

        return r;
    }

    @Override
    public List<Rating> readAllRatings() throws ServiceException {
        try {
            return ratingDao.readAllRatings();
        } catch (RatingPersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage(), e);
        }

    }

    @Override
    public List<Rating> readRatingsForMovie(Movie m) throws ServiceException {

        try {
            return ratingDao.readRatingById(m.getMid());
        } catch (RatingPersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage(), e);
        }
    }
}
