package service.impl;

import dao.IMovieDao;
import dao.IPersonDao;
import dao.IRatingDao;
import dao.ISubtitleDao;
import dto.FilterParameter;
import dto.Movie;
import dto.Rating;
import dto.Role;
import exception.MoviePersistenceException;
import exception.MovieServiceException;
import exception.RatingPersistenceException;
import exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import service.ICrawlerService;
import service.IMovieService;
import service.ITMDBService;
import service.vlcj.VLCUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.Map.Entry;

/**
 * Created by Toan on 02.12.2014.
 * NOTE: what will happen if movie is null?
 */
public class MovieServiceImpl implements IMovieService {

    private static final Logger logger = LogManager.getLogger(MovieServiceImpl.class);

    @Autowired
    private IMovieDao movieDao;
    @Autowired
    private IRatingDao ratingDao;
    @Autowired
    private ISubtitleDao subtitleDao;
    @Autowired
    private IPersonDao personDao;
    @Autowired
    private ITMDBService api;
    @Autowired
    private ICrawlerService crawler;

    private int yearNow = Calendar.getInstance().get(Calendar.YEAR);

    private static boolean isWindowsSystem() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.indexOf("windows") >= 0;
    }

    private static boolean isLinuxSystem() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.indexOf("linux") >= 0;
    }

    private static boolean isMacSystem() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.indexOf("mac") >= 0;
    }

    @Override
    public Movie createMovie(Movie m) throws ServiceException, MovieServiceException {

        validateMovie(m);

        try {
            m = movieDao.createMovie(m);
        } catch (MoviePersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage(), e);
        }

        return m;
    }

    @Override
    public Movie readMovie(Integer mid) throws ServiceException {
        logger.info("Reading movie: " + mid);
        if (!movieExists(mid)) {
            throw new ServiceException("Movie with this mid does not exist");
        }
        Movie m = null;
        if (mid < 0) throw new ServiceException("mid can't be smaller than zero");
        try {
            m = movieDao.readMovieById(false, mid);
        } catch (MoviePersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage(), e);
        }

        logger.info("Reading over!");
        return m;
    }

    @Override
    public void updateMovie(Movie m) throws ServiceException, MovieServiceException {
        if (!movieExists(m.getMid())) {
            throw new ServiceException("Movie with this mid does not exist");
        }

        validateMovie(m);
        validateMovieUpdate(m);

        try {
            movieDao.updateMovie(m);
        } catch (MoviePersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void deleteMovie(Movie m) throws ServiceException {
        if (!movieExists(m.getMid())) {
            throw new ServiceException("Movie with this mid does not exist");
        }
        // validating an object to delete it makes no sense

        try {
            movieDao.deleteMovie(m);
        } catch (MoviePersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage(), e);
        }

    }

    @Override
    public void setMovieIgnored(Movie m) throws ServiceException {
        try {
            movieDao.setMovieIgnored(m);
        } catch (MoviePersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public List<Movie> readAllMovies(Boolean ignored) throws ServiceException {

        List<Movie> result = null;

        try {
            result = movieDao.readAllMovies(ignored); // read all "not ignored" entries
        } catch (MoviePersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage(), e);
        }

        return result;
    }

    @Override
    public List<Movie> readAllMovies() throws ServiceException {
        return this.readAllMovies(false);
    }

    @Override
    public void playMovie(Movie m) throws ServiceException {
        VLCUtil.runVLC(m.getPath());

    }

    @Override
    public void playMovieWithSubs(Movie m, String filename) throws ServiceException {
        String sub_path = this.getClass().getClassLoader().getResource("subtitles/").getPath().replaceAll("%20", " ").toString() + filename;
        if (isWindowsSystem() && sub_path.indexOf("/") == 0) {
            sub_path = sub_path.substring(1).replace("/", "\\");
        }
        logger.debug("Subtitle-Path: " + sub_path);
        VLCUtil.runVLCWithsubs(m.getPath(), sub_path);
    }

    @Override
    public void showMovieExplorer(Movie m) throws ServiceException {
        int directoryEnd = m.getPath().lastIndexOf("\\");
        if (directoryEnd < 0) {
            directoryEnd = 0;
        }
        String directory = m.getPath().substring(0, directoryEnd);

        if (isWindowsSystem()) {
            logger.info("Windows detected");

            try {
                Runtime.getRuntime().exec("explorer.exe " + directory);
            } catch (IOException e) {
                logger.error("File not founded");
            }
        } else if (isLinuxSystem()) {
            logger.info("Linux detected");
            directoryEnd = m.getPath().lastIndexOf("/");
            directory = m.getPath().substring(0, directoryEnd);
            logger.info(directory);

            try {
                ProcessBuilder pb = new ProcessBuilder("thunar", directory);
                Process p = pb.start();
            } catch (IOException e) {
                logger.error("File not founded");
            }
        } else if (isMacSystem()) {
            logger.info("MacOS detected");

            try {
                Runtime.getRuntime().exec(directory);
            } catch (IOException e) {
                logger.error("File not founded");
            }
        } else {
            logger.error("This function is not possible with our System.");
        }
    }

    @Override
    public Movie showNextMovie(Movie m) throws ServiceException {
        int actualmid = m.getMid();

        if (actualmid == listSizeAll()) {
            throw new ServiceException("No Next Movie possible");

        } else {

            actualmid++;

            while (!(movieExists(actualmid))) {
                if (actualmid == listSizeAll()) {
                    throw new ServiceException("No Next Movie possible");
                }

                actualmid++;

            }
        }

        return readMovie(actualmid);
    }

    @Override
    public Movie showPreviousMovie(Movie m) throws ServiceException {
        int actualmid = m.getMid();

        if (actualmid == 1) {
            throw new ServiceException("No Previous Movie possible");

        } else {
            actualmid--;

            while (!movieExists(actualmid)) {
                actualmid--;

                if (actualmid == 1) {
                    throw new ServiceException("No Previous Movie possible");
                }
            }
        }

        return readMovie(actualmid);
    }

    @Override
    public List<Movie> find(FilterParameter params) throws ServiceException {
        List<Movie> result = null;

        try {
            if (params.getRatingFrom() != null && params.getRatingFrom() != null) {
                if (params.getRatingFrom() > params.getRatingTill()) {
                    throw new ServiceException("Rating from is bigger than to");
                }
            } else if (params.getRatingFrom() == null && params.getRatingFrom() != null) {
                params.setRatingFrom(params.getRatingFrom());
            } else {
                params.setRatingFrom(params.getRatingFrom());
            }
            logger.info(yearNow);
            if (params.getYear() != null) {
                if (params.getYear() < 1800 || params.getYear() > yearNow) {
                    throw new ServiceException("check given year " + "\n"
                            + " Year must be bigger than 1800");
                }
            }
            if (params.getGenre() != null) {
                params.setGenre(params.getGenre().toLowerCase());
            }
            if (params.getTitle() != null) {
                params.setTitle(params.getTitle().toLowerCase());
            }
            if (params.getLanguage() != null) {
                params.setLanguage(params.getLanguage().toLowerCase());
            }

            if (params.getSourceRating() != null) {
                params.setSourceRating(params.getSourceRating().toLowerCase());
            }

            logger.info(params);
            result = movieDao.find(params);
        } catch (MoviePersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }

        return result;
    }

    @Override
    public void addToFavorite(Movie m) throws ServiceException {

        m.setFavorite(true);

        try {
            movieDao.updateMovie(m);
            logger.debug("Movie: " + m.getMid() + " was added to Favorites ");
        } catch (MoviePersistenceException e) {
            throw new ServiceException(e.getMessage());
        }


    }

    @Override
    public void removeFromFavorite(Movie m) throws ServiceException {

        m.setFavorite(false);

        try {
            movieDao.updateMovie(m);
            logger.debug("Movie: " + m.getMid() + " was removed from Favorites ");
        } catch (MoviePersistenceException e) {
            throw new ServiceException(e.getMessage());
        }


    }

    @Override
    public boolean isFavourite(Movie m) throws ServiceException {

        try {
            return movieDao.readMovieById(false, m.getMid()).getFavorite();
        } catch (MoviePersistenceException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public List<String> readAllSources() throws ServiceException {
        List<String> sources = new ArrayList<String>();
        logger.info("getting all the sources we have in the DB");
        try {
            sources = ratingDao.readAllSources();
            logger.debug("number of found sources = " + sources.size());
        } catch (RatingPersistenceException e) {
            throw new ServiceException(e.getMessage());
        }
        return sources;
    }

    private void validateMovie(Movie m) throws MovieServiceException {
        if (m.getIdTMDB() < 0) {
            throw new MovieServiceException("Movie idTMDB cannot be negative");
        }
        if (m.getIdIMDB() < 0) {
            throw new MovieServiceException("Movie idIMDB cannot be negative");
        }
//        if (m.getPath().isEmpty()) {
//            throw new MovieServiceException("Movie path cannot be empty");
//        }
        if (m.getTitle().isEmpty()) {
            throw new MovieServiceException("Movie title cannot be empty");
        }
        if (m.getRunTime() < 0) {
            throw new MovieServiceException("Movie runTime cannot be negative");
        }
        if (m.getYear() < 0) {
            throw new MovieServiceException("Movie year cannot be empty");
        }
//        if (m.getPosterPath().isEmpty()) {
//            throw new MovieServiceException("Movie posterPath can not be empty");
//        }
        if (m.getLanguage().isEmpty()) {
            throw new MovieServiceException("Movie language can not be empty");
        }
        if (m.getBudget().isEmpty()) {
            throw new MovieServiceException("Movie budget can not be empty");
        }
        if (m.getRevenue().isEmpty()) {
            throw new MovieServiceException("Movie revenue can not be empty");
        }
        if (m.getPlot().isEmpty()) {
            throw new MovieServiceException("Movie plot can not be empty");
        }
//        if (m.getTagLine().isEmpty()) {
//            throw new MovieServiceException("Movie tagLine can not be empty");
//        }
//        if (m.getSimilarFilms().isEmpty()) {
//            throw new MovieServiceException("Movie posterPath can not be empty");
//        }
        if (m.getGenre().isEmpty()) {
            throw new MovieServiceException("Movie genre can not be empty");
        }
    }

    private void validateMovieUpdate(Movie m) throws ServiceException {

        if (!m.getTitle().isEmpty()) {
            if (m.getTitle().length() < 2) {
                throw new ServiceException("Movie Title cannot be smaller than 2");
            }
        }

        if (m.getRunTime() != null) {
            if (m.getRunTime() <= 0 || m.getRunTime() > 999) {
                throw new ServiceException("Movie RunTime cannot be 0 or higher than 999");
            }
        }

        if (m.getYear() != null) {
            if (m.getYear() <= 1800 || m.getYear() > yearNow) {
                throw new ServiceException("Movie Year cannot be smaller than 1800 or higher than the actual year");
            }
        }

        if (!m.getLanguage().isEmpty()) {
            if (m.getLanguage().length() < 2) {
                throw new ServiceException("Movie Language cannot be smaller than 2");
            }
        }


    }

    private boolean movieExists(Integer id) {
        try {
            movieDao.readMovieById(false, id);
            return true;
        } catch (MoviePersistenceException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    private int listSizeAll() throws ServiceException {
        int anzahl = 0;
        List<Movie> truemovie = null;
        List<Movie> falsemovie = null;

        try {
            truemovie = movieDao.readAllMovies(true);
            falsemovie = movieDao.readAllMovies(false);
        } catch (MoviePersistenceException e) {
            throw new ServiceException(e.getMessage());
        }

        return anzahl + truemovie.size() + falsemovie.size();

    }

    @Override
    public List<Movie> heuristic(String title1, String name, String genre1,
                                 Integer year1) throws ServiceException, IllegalArgumentException {


        logger.info("Heuristic method is activated!");

        int evaluation = 0;
        String personName = null;
        String title = null;
        String genre = null;
        Integer year = null;

        List<Movie> allMovies = new ArrayList<Movie>();
        List<Movie> selectedMovies = new ArrayList<Movie>();
        List<Integer> keys = new ArrayList<Integer>();
        Map<Integer, Movie> heuristicList = new HashMap<Integer, Movie>();


        try {

            allMovies = movieDao.readAllMovies(false);

        } catch (MoviePersistenceException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage(), e);
        }

        try {
            for (Movie m : allMovies) {
                if (title1 == null && name == null && genre1 == null && year1 == null) {
                    throw new ServiceException("One parameter is required to start the search!");
                }

                if (title1 != null) {

                    title = title1.toLowerCase();

                    //Spliting the words in the String in Array of Strings
                    String[] splitedTitle = title.split(" ");
                    // Title Evaluation
                    evaluation += evaluateTitle(m, splitedTitle);
                }
                if (name != null) {

                    personName = name.toLowerCase();

                    //Spliting name of person in Array of Strings
                    String[] splitedName = personName.split(" ");
                    // Person Evaluation
                    evaluation += evaluatePerson(m, splitedName);
                }

                if (genre1 != null) {
                    genre = genre1.toLowerCase();
                    //Genre Evaluation
                    evaluation += evaluateGenre(m, genre);
                }

                if (m.getFavorite()) {
                    evaluation += 20;
                }
                if (year1 != null) {
                    year = year1;
                    if (year1 < 1800) {
                        throw new ServiceException("Year must be positive and bigger than 1800");
                    }

                    // Year Match
                    if (m.getYear() == year) {
                        evaluation += 150;
                    } else if ((year-5) <= m.getYear()
                            && (year+5) >= m.getYear()) {
                        evaluation += 80;
                    } else if ((year - 10) <= m.getYear()
                            && (year + 10) >= m.getYear()) {
                        evaluation += 60;
                    } else if ((year - 20) <= m.getYear()
                            && (year + 20) >= m.getYear()) {
                        evaluation += 40;
                    }
                    // Recent Movie
                    if ((m.getYear() - 3) <= year
                            && year >= (m.getYear() + 3)) {
                        evaluation += 30;
                    } else if ((yearNow - 5) <= m.getYear()
                            && m.getYear() >= yearNow) {
                        evaluation += 10;
                    }

                }
                List<Rating> ratingList = new ArrayList<>();
                try {
                    ratingList = ratingDao.readRatingById(m.getMid());
                } catch (RatingPersistenceException e) {
                    if (!e.getMessage().equals("Liste is empty")) {
                        throw new ServiceException(e.getMessage());
                    }
                }

                // rating must be fixed the source name should be clear for all

                if (ratingList.size() > 0) {
                    for (Rating r : ratingList) {
                        if (3.0 < r.getRating() && r.getRating() > 5.0) {
                            evaluation += 20;
                        } else if (r.getRating() > 8.0) {
                            evaluation += 40;
                        }
                    }
                }

                while (heuristicList.containsKey((evaluation))) {
                    evaluation++;
                }
                if (evaluation >= 50) {

                    heuristicList.put(evaluation, m);

                    keys.add(evaluation);
                }
                evaluation = 0;

            }
            Collections.sort(keys);
            Collections.reverse(keys);
            logger.info(keys.size());
            if (keys.size() < 15) {
                for (int i = 0; i < keys.size(); i++) {
                    selectedMovies.add(heuristicList.get(keys.get(i)));
                }
            } else {
                for (int i = 0; i < 15; i++) {
                    selectedMovies.add(heuristicList.get(keys.get(i)));
                    logger.info(keys.get(i) + "  " + heuristicList.get(keys.get(i)));
                }
            }

        } catch (ServiceException e) {
            //logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
        return selectedMovies;
    }

    /**
     * this method is for evaluation the Title of the movie
     * given by the user with all titles we have in the DB
     * the method evaluate after found words in the title and
     * if the are on the same position they should be.
     *
     * @param m
     * @param splitedString Array of Strings what the user search for
     * @return
     */
    private int evaluateTitle(Movie m, String[] splitedString) {
        int evaluation = 0;
        Movie movie = m;
        //getting the Title of movie
        String movieTitle = movie.getTitle().toLowerCase();

        //Spliting the movie title in String Array
        List<String> mTitle = Arrays.asList(movieTitle.split(" "));
        Map<Integer, Integer> pairList = new HashMap<Integer, Integer>();

        //String array of the given Parameter
        String[] paraTitle = splitedString;

        // counter to see the number of match words
        int count = 0;
        int position = 0;

        //Number of words in the given Parameter
        int numberOfWords = paraTitle.length;

        // checking if the given Title match the given Movie
        for (int i = 0; i < paraTitle.length; i++) {
            String s = paraTitle[i];
            if (mTitle.contains(s)) {
                //logger.debug("word found and counter = " + count);
                count++;
                if (mTitle.indexOf(s) != -1) {
                    //checking the position of the words
                    Integer pos1 = i;
                    Integer pos2 = mTitle.indexOf(s);
                    pairList.put(pos1, pos2);

                }

            }
        }
       /* logger.debug(count + "counter + " + "number" +
                " of words + " + numberOfWords);*/
        if (count != 0) {
            if (count == numberOfWords) {
                logger.info("Full match ");
                evaluation = 100;
            } else {
                double check = (double) count / (double) numberOfWords;
                evaluation = (int) Math.round((check * 100));
                // logger.debug("checking when it is not full" +
                //        " match =" + evaluation);
            }
        }

        //position of the words in given string and in saved string
        Set<Entry<Integer, Integer>> set = pairList.entrySet();
        Iterator<Entry<Integer, Integer>> i = set.iterator();

        int count2 = 0;
        int numberOfMatchWords = set.size();
        while (i.hasNext()) {
            Map.Entry<Integer, Integer> positionList =
                    i.next();

            logger.debug(positionList.getKey() + "  =" +
                    "= " + positionList.getValue());
            if (positionList.getKey() == positionList.getValue()) {
                count2++;
            }

        }

        //logger.info(count2 + "  number of matched words  " + numberOfMatchWords);
        if (count2 == numberOfMatchWords && count2 != 0) {
            evaluation += 50;
        } else {
            double check = (double) count2 / (double) numberOfMatchWords;
            evaluation += (int) Math.round((check * 50));
            //logger.debug("checking when it is not full match =" + evaluation);
        }

        //logger.info("title evaluation is = " + evaluation);
        return evaluation;
    }

    /**
     * this method is to evaluate the name of person given by user
     * with name of persons  saved in the DB and related to certain movie
     *
     * @param m             (Movie)
     * @param splitedString (array of strings)
     * @return integer value of the evaluation
     * @throws ServiceException incase of having a null object movie
     */
    private int evaluatePerson(Movie m, String[] splitedString)
            throws ServiceException {
        int evaluation = 0;

        //List of all staff belong to given movie
        List<Role> staff = new ArrayList<Role>();
        String[] paraName = splitedString;
        //Map for compair position of given name with the saved name
        Map<Integer, Integer> pairList = new HashMap<Integer, Integer>();


        try {
            if (m == null) {
                throw new ServiceException("there is no movie to evaluate");
            }
            staff = m.getStaff();
            for (Role role : staff) {
                int sum = 0;

                //Spliting the Name from DB in String Array
                List<String> mName = Arrays.asList(role.getPerson()
                        .getName().toLowerCase().split(" "));
               /* if (mName.size() > 1) {
                    logger.info("name is " + mName.get(0) + " " + mName.get(1));
                } else {
                    logger.info("name is " + mName.get(0));

                }*/
                // counter to see the number of match words
                int count = 0;
                int position = 0;

                //Number of words in the given Parameter
                int numberOfWords = paraName.length;

                for (int i = 0; i < paraName.length; i++) {
                    String s = paraName[i];
                    if (mName.contains(s)) {
                        //logger.debug("name found and counter for name  = " + count);
                        count++;
                        if (mName.indexOf(s) != -1) {
                            //checking the position of the words
                            Integer pos1 = i;
                            Integer pos2 = mName.indexOf(s);
                            pairList.put(pos1, pos2);

                        }

                    }
                }
                //logger.debug("counter for name   = " + count + " And " +
                //  "number of words + " + numberOfWords);

                if (count != 0) {
                    if (count == numberOfWords) {
                        sum = 150;
                        //logger.info("Full match " + sum);
                    } else {
                        double check = (double) count / (double) numberOfWords;
                        sum = (int) Math.round((check * 150));
                        // logger.debug("checking when it is not full match =" + sum);
                    }
                }

                Set<Entry<Integer, Integer>> set = pairList.entrySet();
                Iterator<Entry<Integer, Integer>> i = set.iterator();

                int count2 = 0;
                int numberOfMatchWords = set.size();
                Map.Entry<Integer, Integer> positionList;
                while (i.hasNext()) {
                    positionList = i.next();

                   /* logger.debug(positionList.getKey() + "  == " +
                            positionList.getValue() +
                            "  position of words in person name ");*/
                    if (positionList.getKey() == positionList.getValue()) {
                        count2++;
                    }

                }
                if (count2 == numberOfMatchWords && numberOfMatchWords != 0) {
                    sum += 50;

                } else {
                    double check = (double) count2 / (double) numberOfMatchWords;
                    sum += (int) Math.round((check * 50));
                    logger.debug("checking when it " +
                            "is not full match =" + evaluation);
                }

                //logger.info("name evaluation is = " + sum);
                pairList.clear();
                evaluation += sum;


            }


        } catch (ServiceException e) {
            logger.error(e.getMessage());
        } catch (NullPointerException n) {
            logger.error(n.getMessage());
        } catch (IllegalArgumentException i) {
            logger.error(i.getMessage());
        }
        return evaluation;

    }

    private int evaluateGenre(Movie m, String genre) throws ServiceException {

        int evaluation = 0;

        String[] splitedGenre = genre.split(" ");
        System.out.println(splitedGenre.length);
        List<String> mGenre = Arrays.asList(m.getGenre().toLowerCase().split(","));
        for (int i = 0; i < splitedGenre.length; i++) {
            String paramGenre = splitedGenre[i];
            if (mGenre.indexOf(paramGenre) != -1) {
                //checking the position of the words
                evaluation = 50;
            }
        }

        //logger.debug(evaluation);
        return evaluation;
    }

    @Override
    public Movie random(String title, String name, String genre, Integer year)
            throws ServiceException {

        List<Movie> list = new ArrayList<>();
        list = heuristic(title, name, genre, year);
        int listSize = list.size();
        if (list.size() == 0) {
            throw new ServiceException(" No sutible movie found. ");
        }
        Random randomMovie = new Random();
        int randomNum = (int) Math.round(Math.random() * (list.size() - 1));
        logger.debug("Random movie is = " + list.get(randomNum));

        return list.get(randomNum);
    }

    @Override
    public void changePoster(Movie m, File in, File out) throws ServiceException {
        logger.info("Change Poster of this movie:" + m.getMid());

        FileChannel inChannel = null;
        FileChannel outChannel = null;

        try {
            inChannel = new FileInputStream(in).getChannel();
            outChannel = new FileOutputStream(out).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);

        } catch (IOException e) {
            logger.error("File not found.");
            throw new ServiceException(e.getMessage());

        } finally {
            try {
                if (inChannel != null) {
                    inChannel.close();
                }

                if (outChannel != null) {
                    outChannel.close();
                }

            } catch (IOException e) {
                logger.error("Change Poster Process failed");
                throw new ServiceException(e.getMessage(), e);
            }
        }
    }
}

