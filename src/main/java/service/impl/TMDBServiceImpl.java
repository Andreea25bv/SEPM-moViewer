package service.impl;

import dto.Movie;
import dto.Person;
import dto.Role;
import exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.ITMDBService;
import themoviedbapi.TmdbApi;
import themoviedbapi.model.Credits;
import themoviedbapi.model.Genre;
import themoviedbapi.model.MovieDb;
import themoviedbapi.model.core.MovieResults;
import themoviedbapi.model.people.PersonCast;
import themoviedbapi.model.people.PersonCrew;
import themoviedbapi.model.people.PersonPeople;
import themoviedbapi.tools.MovieDbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by markus on 27.11.14.
 * API in use: https://github.com/Omertron/
 */
public class TMDBServiceImpl implements ITMDBService {
    private final Logger log = LogManager.getLogger(TMDBServiceImpl.class.getName());
    private final String key = "6126aa0057b7fc7cc6273d59a5447e63";
    private TmdbApi api;

    public TMDBServiceImpl() throws ServiceException {
        api = new TmdbApi(this.key);
    }

    @Override
    public Movie getMovieByTMDBID(int id, String lang) throws ServiceException {
        try {
            if (lang.equals("")) {
                lang = "en";
            }
            Movie movie = new Movie();
            MovieDb movie_api = api.getMovies().getMovie(id, lang, null);
            movie = tmdbMovieToMovie(movie_api);
            movie.setLanguage(lang);
            return movie;
        } catch (MovieDbException e) {
            throw new ServiceException("Movie not found.");
        }
    }

    @Override
    public Person getPersonByTMDBID(int id) throws ServiceException {
        try {
            PersonPeople person_api = this.api.getPeople().getPersonInfo(id, null);
            Person person = this.tmdbPersonToPerson(person_api);
            return person;
        } catch (MovieDbException e) {
            throw new ServiceException("Person not found.");
        }
    }

    @Override
    public ArrayList<Movie> search(String title, int year, String lang, boolean nsfw, int page) throws ServiceException {
        ArrayList<Movie> movies = new ArrayList<Movie>();
        // Searching
        MovieResults movies_api = api.getSearch().searchMovie(title, year, lang, nsfw, page);
        for (MovieDb movie : movies_api.getResults()) {
            movies.add(tmdbMovieToMovie(movie, true));
        }
        return movies;
    }

    @Override
    public ArrayList<Movie> searchAppend(String title, int year, String lang, boolean nsfw, int page) throws ServiceException {
        log.debug("AppendSearch started for " + title);

        Boolean match = false;
        Movie best_match = null;
        int best_match_score = 10000;
        ArrayList<String> parts = new ArrayList<String>();
        for (String part : title.split(" ")) {
            parts.add(part);
        }

        while (parts.size() > 0) {
            // Searching
            MovieResults movies_api = api.getSearch().searchMovie(parts.toString(), year, lang, nsfw, page);
            List<MovieDb> movies = movies_api.getResults();
            if (movies.size() <= 5 && movies.size() >= 1) {
                log.debug("First choice taken (<5 hits): " + movies.get(0).getTitle());
                best_match = tmdbMovieToMovie(movies.get(0), true);
                break;
            } else {
                for (MovieDb movie : movies_api.getResults()) {
                    log.debug("Checking " + movie.getTitle() + " against " + title);
                    Movie lmovie = tmdbMovieToMovie(movie, true);
                    int dist = distance(title, lmovie.getTitle());
                    log.debug("Distance is: " + dist);
                    if (dist < best_match_score && dist < (title.length() / 2)) {
                        best_match = lmovie;
                        best_match_score = dist;
                        if (best_match_score <= 5) {
                            match = true;
                            break;
                        }
                    }

                }
            }
            if (match) {
                break;
            }
            parts.remove(parts.size() - 1);
        }
        ArrayList<Movie> ret = new ArrayList<Movie>();
        ret.add(best_match);
        if (best_match != null) {
            log.debug("Best Match: " + best_match.getTitle());
        } else {
            log.debug("No match, sorry!");
        }
        return ret;
    }

    /*
        Helper
     */

    private Movie tmdbMovieToMovie(MovieDb mdb, boolean skipCast) {
        Movie movie = new Movie();

        // internal set in H2
        movie.setIdTMDB(mdb.getId());
        if (mdb.getImdbID() != null && !mdb.getImdbID().equals("")) {
            movie.setIdIMDB(Integer.parseInt(mdb.getImdbID().substring(2)));
        }
        // Path set in CrawlerServiceImpl
        // Favorite set default false
        movie.setTitle(mdb.getTitle());
        movie.setRunTime(mdb.getRuntime());
        if (!mdb.getReleaseDate().equals("")) {
            movie.setYear(Integer.parseInt(mdb.getReleaseDate().substring(0, 4)));
        }
        movie.setPosterPath(mdb.getPosterPath());
        movie.setBudget(mdb.getBudget() + "");
        movie.setRevenue(mdb.getRevenue() + "");
        movie.setPlot(mdb.getOverview()); // plot
        movie.setTagLine(mdb.getTagline());

        List<Genre> genres = mdb.getGenres();

        if (genres != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < genres.size(); i++) {
                sb.append(genres.get(i).getName());
                if (i != genres.size() - 1) {
                    sb.append(",");
                }
            }
            movie.setGenre(sb.toString());
        }

        if (!skipCast) {
            log.debug("Parsing cast and crew, might take a while!");
            Credits c = api.getMovies().getCredits(movie.getIdTMDB());
            List<Role> cast = new ArrayList<Role>();
            for (PersonCast p : c.getCast()) {
                PersonPeople per = api.getPeople().getPersonInfo(p.getId());
//                log.debug("Cast-Member: " + p.getName() + " playing " + p.getCharacter());
                cast.add(new Role("As: " + p.getCharacter(), new Person(p.getId(), p.getName(), p.getId(), per.getBiography(), p.getProfilePath())));
            }
            for (PersonCrew p : c.getCrew()) {
                PersonPeople per = api.getPeople().getPersonInfo(p.getId());
//                log.debug("Cast-Member: " + p.getName() + " " + p.getJob() + " in " + p.getDepartment());
                cast.add(new Role(p.getJob(), new Person(p.getId(), p.getName(), p.getId(), per.getBiography(), p.getProfilePath())));
            }
            movie.setStaff(cast);
        } else {
            log.debug("Not checking on cast & crew (no parsing)!");
//            Credits c = api.getMovies().getCredits(movie.getIdTMDB());
//            List<Role> cast = new ArrayList<Role>();
//            for (PersonCast p : c.getCast()) {
//                cast.add(new Role("As: " + p.getCharacter(), new Person(p.getId(), p.getName(), "", p.getProfilePath())));
//            }
//            for (PersonCrew p : c.getCrew()) {
//                cast.add(new Role(p.getJob(), new Person(p.getId(), p.getName(), "", p.getProfilePath())));
//            }
//            movie.setStaff(cast);
        }

        movie.setFavorite(false);
        movie.setSimilarFilms("");

        return movie;
    }

    private Movie tmdbMovieToMovie(MovieDb mdb) {
        return tmdbMovieToMovie(mdb, false);
    }

    private Person tmdbPersonToPerson(PersonPeople people) {
        Person person = new Person();
        person.setName(people.getName());
        person.setTmdbid(people.getId());
        person.setBiography(people.getBiography());
        person.setPhotoPath(people.getProfilePath());

        return person;
    }

    private int distance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        // i == 0
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }
}
