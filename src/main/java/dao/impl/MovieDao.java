package dao.impl;

import dao.AbstactDaoManager;
import dao.IMovieDao;
import dao.IPersonDao;
import dao.query.FilterQuery;
import dto.FilterParameter;
import dto.Movie;
import dto.Person;
import dto.Role;
import exception.MoviePersistenceException;
import exception.PersonPersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Toan on 29.11.2014.
 */
//@Component
public class MovieDao extends AbstactDaoManager implements IMovieDao {
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private IPersonDao personDao;
    @Autowired
    private DriverManagerDataSource dataSource;

    @Override
    public Movie createMovie(Movie m) throws MoviePersistenceException {
        logger.debug("Entering createMovieMethod with parameters:" + m);
        checkMovieValidation(m);
        sql = "INSERT INTO Movie VALUES (NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,false)";
        try {
            ps = dataSource.getConnection().prepareStatement(sql);
            ps.setInt(1, m.getIdTMDB());
            ps.setInt(2, m.getIdIMDB());
            ps.setString(3, m.getPath());
            ps.setBoolean(4, m.getFavorite());
            ps.setString(5, m.getTitle());
            ps.setInt(6, m.getRunTime());
            ps.setInt(7, m.getYear());
            ps.setString(8, m.getPosterPath());
            ps.setString(9, m.getLanguage());
            ps.setString(10, m.getBudget());
            ps.setString(11, m.getRevenue());
            ps.setString(12, m.getPlot());
            ps.setString(13, m.getTagLine());
            ps.setString(14, m.getSimilarFilms());
            ps.setString(15, m.getGenre());

            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            rs.next();
            m.setMid(rs.getInt(1));

            Person person;
            for (Role role : m.getStaff()) {
                try {
                    person = personDao.getPersonByTmdbid(role.getPerson());
                } catch (PersonPersistenceException e) {
                    try {
                        person = personDao.createPerson(role.getPerson());
                    } catch (PersonPersistenceException e1) {
                        throw new MoviePersistenceException("Person could not be added (via connect).");
                    }
                }

                try {
                    role.setPerson(person);
                    personDao.connect(m, role);
                } catch (PersonPersistenceException e) {
                    throw new MoviePersistenceException("Connection failed for: " + person.getName());
                }
            }

        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new MoviePersistenceException(e.getMessage());
        }
        if (m.getMid() == null) throw new IllegalArgumentException("Movie Id can not be null");

        this.downloadPoster(m);

        return m;
    }

    @Override
    public Movie readMovieById(Boolean ignore, Integer mid) throws MoviePersistenceException {
        logger.debug("Entering readMovieByIdMethod with parameters:" + mid);
        if (mid == null) throw new IllegalArgumentException("Illegal Argument: mid can not be null");
        if (ignore == null) throw new IllegalArgumentException("Illegal Argument: ignore can not be null");
        Movie m = null;

        sql = "SELECT * FROM Movie WHERE ignore=? AND mid=?";

        try {
            ps = dataSource.getConnection().prepareStatement(sql);
            ps.setBoolean(1, ignore);
            ps.setInt(2, mid);
            rs = ps.executeQuery();
            while (rs.next()) {
                m = getMovieFromSet(rs);
            }
            sql = "SELECT * FROM movie_has_person NATURAL JOIN person WHERE mid=?";
            ps = dataSource.getConnection().prepareStatement(sql);
            ps.setInt(1, mid);
            rs = ps.executeQuery();
            List<Role> staff = new ArrayList<Role>();
            while (rs.next()) {
                Person p = new Person(rs.getInt("pid"), rs.getString("name"), rs.getInt("tmdbid"), rs.getString("biography"), rs.getString("photopath"));
                Role role = new Role(rs.getString("role"), p);
                staff.add(role);
            }
            if (m != null) {
                m.setStaff(staff);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new MoviePersistenceException(e.getMessage());
        }
        checkMovieValidation(m);
        return m;
    }

    @Override
    public void updateMovie(Movie m) throws MoviePersistenceException {
        logger.debug("Entering updateMovieMethod with parameters:" + m);
        checkMovieValidation(m);
        if (m.getMid() == null) throw new IllegalArgumentException("Movie Id can not be null");
        sql = "UPDATE Movie SET idTMDB=? ,idIMDB=?, path=?, favorite=?, title=?, " +
                "runTime=?, year=?, posterPath=?, language=?, budget=?, revenue=?, plot=?, tagLine=?, " +
                "similarFilms=?, genre=? WHERE mid=?";
        try {
            ps = dataSource.getConnection().prepareStatement(sql);
            ps.setInt(1, m.getIdTMDB());
            ps.setInt(2, m.getIdIMDB());
            ps.setString(3, m.getPath());
            ps.setBoolean(4, m.getFavorite());
            ps.setString(5, m.getTitle());
            ps.setInt(6, m.getRunTime());
            ps.setInt(7, m.getYear());
            ps.setString(8, m.getPosterPath());
            ps.setString(9, m.getLanguage());
            ps.setString(10, m.getBudget());
            ps.setString(11, m.getRevenue());
            ps.setString(12, m.getPlot());
            ps.setString(13, m.getTagLine());
            ps.setString(14, m.getSimilarFilms());
            ps.setString(15, m.getGenre());
            ps.setInt(16, m.getMid());

            ps.executeUpdate();

        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new MoviePersistenceException(e.getMessage());
        }
    }

    @Override
    public void deleteMovie(Movie m) throws MoviePersistenceException {
        checkMovieValidation(m);
        logger.debug("Entering deleteMovieMethod with parameters:" + m.getMid()+", '"+m.getTitle()+"'");
        if (m.getMid() == null) throw new IllegalArgumentException("Movie Id can not be null");
        try {
            sql = "DELETE FROM MOVIE_HAS_PERSON WHERE MID=?";
            ps = dataSource.getConnection().prepareStatement(sql);
            ps.setInt(1, m.getMid());
            ps.executeUpdate();

            sql = "DELETE FROM RATING WHERE MID=?";
            ps = dataSource.getConnection().prepareStatement(sql);
            ps.setInt(1, m.getMid());
            ps.executeUpdate();

            sql = "DELETE FROM Subtitle WHERE MID=?";
            ps = dataSource.getConnection().prepareStatement(sql);
            ps.setInt(1, m.getMid());
            ps.executeUpdate();

            sql = "DELETE FROM Movie WHERE mid=?";
            ps = dataSource.getConnection().prepareStatement(sql);
            ps.setInt(1, m.getMid());
            ps.executeUpdate();

            File f = new File(this.getClass().getClassLoader().getResource("poster/").getPath().replaceAll("%20", " ").toString() + m.getPosterPath());
            if (f.exists()) {
                f.delete();
            }

        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new MoviePersistenceException(e.getMessage());
        }
    }

    @Override
    public List<Movie> readAllMovies(Boolean ignore) throws MoviePersistenceException {
        logger.debug("Entering readAllMoviesMethod");
        if (ignore == null) throw new MoviePersistenceException("Illegal Argument: ignore can not be null");

        List<Movie> result = new ArrayList<Movie>();
        sql = "SELECT * FROM Movie WHERE ignore = ?";
        try {
            ps = dataSource.getConnection().prepareStatement(sql);
            ps.setBoolean(1, ignore);
            rs = ps.executeQuery();
            while (rs.next()) {
                Movie m = getMovieFromSet(rs);

                sql = "SELECT * FROM movie_has_person NATURAL JOIN person WHERE mid=?";
                PreparedStatement pss = dataSource.getConnection().prepareStatement(sql);
                pss.setInt(1, m.getMid());
                ResultSet rss = pss.executeQuery();
                List<Role> staff = new ArrayList<Role>();
                while (rss.next()) {
                    Person p = new Person(rss.getInt("pid"), rss.getString("name"), rss.getString("biography"), rss.getString("photopath"));
                    Role role = new Role(rss.getString("role"), p);
                    staff.add(role);
                }
                if (m != null) {
                    m.setStaff(staff);
                }

                result.add(m);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new MoviePersistenceException(e.getMessage());
        }
        return result;
    }

    @Override
    public void setMovieIgnored(Movie m) throws MoviePersistenceException {
        if (m != null) {

            logger.debug("Entering setMovieIgnoredMethod with parameters:" + m);

            if (m.getMid() != null && m.getMid() != 0) {
                sql = "UPDATE Movie SET ignore=? WHERE mid=?";
                try {
                    ps = dataSource.getConnection().prepareStatement(sql);
                    ps.setBoolean(1, true);
                    ps.setInt(2, m.getMid());
                    ps.executeUpdate();

                } catch (SQLException e) {
                    logger.error(e.getMessage());
                    throw new MoviePersistenceException(e.getMessage());
                }
            } else {
                sql = "INSERT INTO MOVIE ( PATH , IGNORE ) VALUES (?, ?)";
                try {
                    ps = dataSource.getConnection().prepareStatement(sql);
                    ps.setString(1, m.getPath());
                    ps.setBoolean(2, true);

                    ps.executeUpdate();
                    rs = ps.getGeneratedKeys();
                    rs.next();
                    m.setMid(rs.getInt(1));
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                    throw new MoviePersistenceException(e.getMessage());
                }
            }
        }
    }

    @Override
    public List<Movie> find(FilterParameter params) throws MoviePersistenceException {
        logger.info("searching for movie or movies with Specific Criteria " + params.getTitle());

        List<Movie> resultList = new ArrayList<Movie>();

        try {

            if (params.getRatingFrom() != null && params.getRatingTill() == null) {
                params.setRatingTill(params.getRatingFrom());

            } else if (params.getRatingFrom() == null && params.getRatingTill() != null) {
                params.setRatingFrom(params.getRatingTill());
            }

            FilterQuery fqb = new FilterQuery(params);
            PreparedStatement ps = fqb.getPreparedStatement(dataSource.getConnection());
            rs = ps.executeQuery();
            while (rs.next()) {
                Movie m = null;
                m = new Movie();
                m.setMid(rs.getInt("mid"));
                m.setIdTMDB(rs.getInt("idtmdb"));
                m.setIdIMDB(rs.getInt("idimdb"));
                m.setPath(rs.getString("path"));
                m.setFavorite(rs.getBoolean("favorite"));
                m.setTitle(rs.getString("title"));
                m.setRunTime(rs.getInt("runTime"));
                m.setYear(rs.getInt("year"));
                m.setPosterPath(rs.getString("posterPath"));
                m.setLanguage(rs.getString("language"));
                m.setBudget(rs.getString("budget"));
                m.setRevenue(rs.getString("revenue"));
                m.setPlot(rs.getString("plot"));
                m.setTagLine(rs.getString("tagLine"));
                m.setSimilarFilms(rs.getString("similarFilms"));
                m.setGenre(rs.getString("genre"));


                resultList.add(m);


            }
        } catch (SQLException s) {
            logger.error(s.getMessage());
            throw new MoviePersistenceException(s.getMessage(), s);
        }
        return resultList;
    }

    /**
     * This method checks if one of the parameters of the movie object is null
     *
     * @param m The movie to be checked.
     */
    private void checkMovieValidation(Movie m) {
        if (m == null) {
            throw new IllegalArgumentException("Movie object can not be null");
        }
        if (m.getIdIMDB() == null) {
            throw new IllegalArgumentException("Movie IdIMDB can not be null");
        }
        if (m.getIdTMDB() == null) {
            throw new IllegalArgumentException("Movie IdTMDB can not be null");
        }
        if (m.getPath() == null) {
            throw new IllegalArgumentException("Movie path can not be null");
        }
        if (m.getFavorite() == null) {
            throw new IllegalArgumentException("Movie favorite can not null");
        }
        if (m.getTitle() == null) {
            throw new IllegalArgumentException("Movie title can not be null");
        }
        if (m.getRunTime() == null) {
            throw new IllegalArgumentException("Movie runTime can not be null");
        }
        if (m.getYear() == null) {
            throw new IllegalArgumentException("Movie year can not be null");
        }
        if (m.getPosterPath() == null) {
            throw new IllegalArgumentException("Poster path can not be null");
        }
        if (m.getLanguage() == null) {
            throw new IllegalArgumentException("Movie language can not be null");
        }
        if (m.getBudget() == null) {
            throw new IllegalArgumentException("Movie budget can not be null");
        }
        if (m.getRevenue() == null) {
            throw new IllegalArgumentException("Movie revenue can not be null");
        }
        if (m.getPlot() == null) {
            throw new IllegalArgumentException("Movie plot can not be null");
        }
        if (m.getTagLine() == null) {
            throw new IllegalArgumentException("Movie tagLine can not be null");
        }
//        if (m.getSimilarFilms() == null) {
//            throw new IllegalArgumentException("Movie similarFilms can not be null");
//        }
        if (m.getGenre() == null) {
            throw new IllegalArgumentException("Movie genre can not be null");
        }
        if (m.getStaff() == null) {
            throw new IllegalArgumentException("Movie staff can not be null");
        }
        if (m.getRatingList() == null) {
            throw new IllegalArgumentException("Movie ratingList can not be null");
        }
    }

    /**
     * Will download a poster for a given movie.
     * Already downloaded posters will not be overridden and downloading aborted.
     *
     * @param movie The movie to download the poster for.
     */
    public void downloadPoster(Movie movie) {
        File poster = new File(this.getClass().getClassLoader().getResource("poster").getPath().toString() + movie.getPosterPath());
        if (!poster.exists() && !movie.getPosterPath().equals("")) {
            try {
                URL website = new URL("http://image.tmdb.org/t/p/w500" + movie.getPosterPath());
                ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                FileOutputStream fos = null;
                fos = new FileOutputStream(this.getClass().getClassLoader().getResource("poster").getPath().toString().replaceAll("%20", " ") + movie.getPosterPath());
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            } catch (FileNotFoundException e) {
                logger.error(e);
            } catch (IOException e) {
                logger.error(e);
            }
        }
    }

    private Movie getMovieFromSet(ResultSet rs) throws SQLException {
        Movie m = new Movie();
        m.setMid(rs.getInt("mid"));
        m.setIdTMDB(rs.getInt("idtmdb"));
        m.setIdIMDB(rs.getInt("idimdb"));
        m.setPath(rs.getString("path"));
        m.setFavorite(rs.getBoolean("favorite"));
        m.setTitle(rs.getString("title"));
        m.setRunTime(rs.getInt("runTime"));
        m.setYear(rs.getInt("year"));
        m.setPosterPath(rs.getString("posterPath"));
        m.setLanguage(rs.getString("language"));
        m.setBudget(rs.getString("budget"));
        m.setRevenue(rs.getString("revenue"));
        m.setPlot(rs.getString("plot"));
        m.setTagLine(rs.getString("tagLine"));
        m.setSimilarFilms(rs.getString("similarFilms"));
        m.setGenre(rs.getString("genre"));
        return m;
    }
}
