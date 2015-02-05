package service;


import com.omertron.rottentomatoesapi.model.RTMovie;
import exception.ServiceException;

import java.util.List;

/**
 * Created by Andreea on 13/12/2014.
 */
public interface IRottenTomatoService {

    public RTMovie searchMovie(String title, int year) throws ServiceException;
    public Double getMovieScore(RTMovie movie ) throws ServiceException;
    public List<String> getMovieReview(RTMovie movie) throws ServiceException;
    public int getLimit();

}
