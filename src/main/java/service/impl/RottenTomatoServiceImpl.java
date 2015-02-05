package service.impl;

import com.omertron.rottentomatoesapi.RottenTomatoesApi;
import com.omertron.rottentomatoesapi.RottenTomatoesException;
import com.omertron.rottentomatoesapi.model.RTMovie;
import com.omertron.rottentomatoesapi.model.Review;
import exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.IRottenTomatoService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Andreea on 13/12/2014.
 */
public class RottenTomatoServiceImpl implements IRottenTomatoService {

    private final Logger logger = LogManager.getLogger(RottenTomatoServiceImpl.class.getName());

    private final String key = "n4sgrjncp5ujuhk6kwmz7rxe";
    private RottenTomatoesApi rtapi;
    private int limit = 10;

    public RottenTomatoServiceImpl() throws ServiceException {
        try {
            rtapi = new RottenTomatoesApi(key);
        } catch (RottenTomatoesException e) {
            e.printStackTrace();
            logger.fatal("Connection to TMDB failed.");
            throw new ServiceException("Connection to RottenTomatoes failed!");
        }
    }

    @Override
    public RTMovie searchMovie(String title, int year) throws ServiceException {

        try {
            // Searching
            String query = title + year;
            List<RTMovie> result_list = rtapi.getMoviesSearch(query);
            return this.selectBestFitMovie(result_list, title, year);

        } catch (RottenTomatoesException e) {
            logger.error(e);
            throw new ServiceException("Rotten tomatoes Movie search failed!");
        }
    }

    @Override
    public Double getMovieScore(RTMovie movie) throws ServiceException {
        Double score = -1.0;

        try {
            String score_str = movie.getRatings().get("critics_score");
            score = Double.valueOf(score_str);
        } catch (NumberFormatException e) {
            logger.error(e);
            throw new ServiceException("Rotten tomatoes score can not be parsed!");
        } catch (NullPointerException e) {
            logger.error(e);
            throw new ServiceException("No RottenTomatoes Score available!");
        }
        // divide by 10 because of Rating range  of 0-10.

        if (score < 0) {
            throw new ServiceException("No RottenTomatoes Score available!");
        }
        return score / 10;
    }

    @Override
    public List<String> getMovieReview(RTMovie movie) throws ServiceException {
        List<String> reviews = new ArrayList<String>();
        try {
            List<Review> rw = rtapi.getMoviesReviews(movie.getId());
            logger.debug("RT-Reviews available: "+rw.size());
            List<Integer> seen = new ArrayList<Integer>();
            if (rw.size() < limit) {
                limit = rw.size();
            }
            for (int i = 0; i < limit; i++) {
                int rand = new Random().nextInt(limit);
                if (seen.contains(rand)){
                    i--;
                } else {
                    reviews.add(rw.get(rand).getQuote());
                    seen.add(rand);
                }
            }
        } catch (RottenTomatoesException e) {
            logger.error(e);
            throw new ServiceException("Rotten tomatoes error!");
        } catch (NullPointerException e) {
            logger.error(e);
            throw new ServiceException("No RottenTomatoReview available!");
        }

        return reviews;
    }

    private RTMovie selectBestFitMovie(List<RTMovie> result_list, String title, int year) {
        int limit = 5;
        if (result_list.size() < 5) {
            limit = result_list.size();
        }

        if (result_list.isEmpty()) {
            return null;
        }
        for (int i = 0; i < limit; i++) {
            if (result_list.get(i).getTitle().equals(title) && result_list.get(i).getYear() == year) {
                return result_list.get(i);
            }
        }

        return result_list.get(0);
    }

    @Override
    public int getLimit() {
        return limit;
    }
}
