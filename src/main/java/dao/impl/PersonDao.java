package dao.impl;

import dao.AbstactDaoManager;
import dao.IMovieDao;
import dao.IPersonDao;
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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andreea on 29/11/2014.
 */
//@Component
public class PersonDao extends AbstactDaoManager implements IPersonDao {

    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private IMovieDao movieDao;
    @Autowired
    private DriverManagerDataSource dataSource;

    @Override
    public Person createPerson(Person p) throws PersonPersistenceException {
        checkPersonValidation(p);
        logger.debug("Entering createPersonMethod with parameters:" + p.getName());
        sql = "INSERT INTO Person VALUES(NULL,?,?,?,?) ";

        try {
            ps = dataSource.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, p.getName());
            ps.setInt(2, p.getTmdbid());
            ps.setString(3, p.getBiography());
            ps.setString(4, p.getPhotoPath());

            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            rs.next();
            p.setPid(rs.getInt(1));

        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new PersonPersistenceException(e);
        }
        if (p.getPid() == null) throw new IllegalArgumentException("Person Id can not be null");

        this.downloadPersonPicture(p);

        return p;
    }

    @Override
    public Person readPersonById(Integer pid) throws PersonPersistenceException {
        logger.debug("Entering readPersonByIdMethod with parameters:" + pid);
        if (pid == null) throw new IllegalArgumentException("Illegal Argument: pid can not be null");
        Person p = null;

        sql = "SELECT * FROM Person WHERE pid = ?";

        try {
            ps = dataSource.getConnection().prepareStatement(sql);
            ps.setInt(1, pid);
            rs = ps.executeQuery();
            if (rs.next()) {
                p = new Person(rs.getInt("pid"), rs.getString("name"), rs.getString("biography"), rs.getString("photoPath"));
                p.setTmdbid(rs.getInt("tmdbid"));
            } else {
                throw new PersonPersistenceException("Not found");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new PersonPersistenceException(e);
        }
        checkPersonValidation(p);
        return p;
    }

    @Override
    public Person getPersonByTmdbid(Person person) throws PersonPersistenceException {
        logger.debug("Entering readPersonByTmdbidMethod with parameters:" + person.getTmdbid());
        if (person == null) throw new IllegalArgumentException("Illegal Argument: pid can not be null");
        Person p = null;

        sql = "SELECT * FROM Person WHERE tmdbid = ?";

        try {
            ps = dataSource.getConnection().prepareStatement(sql);
            ps.setInt(1, person.getTmdbid());
            rs = ps.executeQuery();
            if (rs.next()) {
                p = new Person(rs.getInt("pid"), rs.getString("name"), rs.getInt("tmdbid"), rs.getString("biography"), rs.getString("photoPath"));
            } else {
                throw new PersonPersistenceException("Not found");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new PersonPersistenceException(e);
        }
        checkPersonValidation(p);
        return p;
    }

    @Override
    public void updatePerson(Person p) throws PersonPersistenceException {
        logger.debug("Entering updatePersonMethod with parameters:" + p);
        checkPersonValidation(p);
        if (p.getPid() == null) throw new IllegalArgumentException("Person Id can not be null");
        sql = "UPDATE Person SET name=?, tmdbid=?, biography=?, photoPath=? WHERE pid = ?";

        try {
            ps = dataSource.getConnection().prepareStatement(sql);
            ps.setString(1, p.getName());
            ps.setInt(2, p.getTmdbid());
            ps.setString(3, p.getBiography());
            ps.setString(4, p.getPhotoPath());
            ps.setInt(5, p.getPid());
            ps.executeUpdate();

        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new PersonPersistenceException(e);
        }
    }

    @Override
    public void deletePerson(Person p) throws PersonPersistenceException {
        logger.debug("Entering deletePersonMethod with parameters:" + p);
        checkPersonValidation(p);
        if (p.getPid() == null) throw new IllegalArgumentException("Person Id can not be null");
        try {
            sql = "DELETE FROM MOVIE_HAS_PERSON WHERE PID=?";
            ps = dataSource.getConnection().prepareStatement(sql);
            ps.setInt(1, p.getPid());
            ps.executeUpdate();

            sql = "DELETE FROM Person WHERE pid=?";
            ps = dataSource.getConnection().prepareStatement(sql);
            ps.setInt(1, p.getPid());
            if (ps.executeUpdate() < 1){
                throw new PersonPersistenceException("Person not found.");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new PersonPersistenceException(e);
        }

    }

    @Override
    public List<Person> listAllPersons() throws PersonPersistenceException {
        logger.debug("Entering listAllPersonsMethod");
        List<Person> list = new ArrayList<Person>();
        sql = "SELECT * FROM Person";
        try {
            ps = dataSource.getConnection().prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Person ps = new Person(rs.getInt("pid"), rs.getString("name"), rs.getString("biography"), rs.getString("photoPath"));
                ps.setTmdbid(rs.getInt("tmdbid"));
                list.add(ps);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new PersonPersistenceException(e);
        }
        return list;
    }

    /**
     * This method checks if one of the parameters of the person object is null
     *
     * @param p
     */
    private void checkPersonValidation(Person p) {
        if (p == null) {
            throw new IllegalArgumentException("Person object can not be null");
        }
        if (p.getName() == null) {
            throw new IllegalArgumentException("Person name can not be null");
        }
        if (p.getTmdbid() == null) {
            throw new IllegalArgumentException("Person TMDBID can not be null");
        }
        if (p.getBiography() == null) {
            throw new IllegalArgumentException("Person biography can not be null");
        }
        if (p.getPhotoPath() == null) {
            throw new IllegalArgumentException("Person photoPath can not be null");
        }
    }

    @Override
    public void connect(Movie movie, Role role) throws PersonPersistenceException {
        logger.debug("Entering connect");


        if (movie == null || role == null || role.getPerson() == null) {
            throw new PersonPersistenceException("connect: Nullpointer given.");
        }

        try {
            Person person = this.readPersonById(role.getPerson().getPid());
            movie = movieDao.readMovieById(false, movie.getMid());
        } catch (PersonPersistenceException e) {
            throw new PersonPersistenceException("connect: Movie does not exists");
        } catch (MoviePersistenceException e) {
            throw new PersonPersistenceException("connect: Movie does not exists");
        }

        sql = "INSERT INTO MOVIE_HAS_PERSON VALUES(?,?,?) ";

        try {
            ps = dataSource.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, movie.getMid());
            ps.setInt(2, role.getPerson().getPid());
            ps.setString(3, role.getRole());

            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            rs.next();

        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new PersonPersistenceException(e);
        }
    }

    @Override
    public Person getPersonByName(Person person) throws PersonPersistenceException {
        logger.debug("Entering readPersonByIdMethod with parameters:" + person.getName());
        if (person == null || person.getName().equals("")) {
            throw new IllegalArgumentException("Illegal Argument: name can not be null");
        }
        Person p = null;

        sql = "SELECT * FROM Person WHERE NAME = ?";

        try {
            ps = dataSource.getConnection().prepareStatement(sql);
            ps.setString(1, person.getName());
            rs = ps.executeQuery();

            if (rs.next()) {
                p = new Person(rs.getInt("pid"), rs.getString("name"), rs.getString("biography"), rs.getString("photoPath"));
            } else {
                throw new PersonPersistenceException("More than one person (or none) with this name exist.");
            }
            if (rs.next()) {
                throw new PersonPersistenceException("More than one person (or none) with this name exist.");
            }

        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new PersonPersistenceException(e);
        }
        checkPersonValidation(p);
        return p;
    }

    public void downloadPersonPicture(Person person) {
        File poster = new File(this.getClass().getClassLoader().getResource("portraits").getPath().toString() + person.getPhotoPath());
        if (!poster.exists() && !person.getPhotoPath().equals("")) {
            try {
                URL website = new URL("http://image.tmdb.org/t/p/w500" + person.getPhotoPath());
                ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                FileOutputStream fos = null;
                fos = new FileOutputStream(this.getClass().getClassLoader().getResource("portraits").getPath().toString().replaceAll("%20", " ") + person.getPhotoPath());
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            } catch (FileNotFoundException e) {
                logger.error(e);
            } catch (IOException e) {
                logger.error(e);
            }
        }
    }
}
