package service.impl;

import dto.Movie;
import exception.ServiceException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import service.ICrawlerService;
import service.IMovieService;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by markus on 27.11.14.
 */
public class CrawlerServiceImpl implements ICrawlerService {
    private static final Logger log = LogManager.getLogger();
    private Collection<Path> files = new ArrayList<Path>();
    private Map<String, String> titles = new HashMap<String, String>();
    private ArrayList<Movie> movies = new ArrayList<Movie>();
    private Path path;
    private List<Movie> oldMovies = new ArrayList<Movie>();
    private String replace;
    private String replace_groups;
    private String replace_mixed;

    @Autowired
    private IMovieService service;

    private String file;

    /**
     * Accepted filetypes. See: https://wiki.videolan.org/VLC_Features_Formats#Format.2FContainer.2FMuxers
     */
    private String[] acceptedFileTypes;

    /**
     * Empty constructor, if you prefer to set the path later.
     */
    public CrawlerServiceImpl() {
    }

    /**
     * Constructor which will allow you to set the path immediately.
     * The path can still be changed.
     *
     * @param path The path to be scanned later.
     */
    public CrawlerServiceImpl(String path) {
        this.path = Paths.get(path);
    }

    @Override
    public void scan() {
        if (this.path != null) {
            try {
                log.info(service);
                oldMovies.clear();
                oldMovies.addAll(service.readAllMovies());
                oldMovies.addAll(service.readAllMovies(true));
            } catch (ServiceException e) {
                log.error("ServiceException: " + e);
            }

            this.files = new ArrayList<Path>();
            Collection<File> files = new ArrayList<File>();
            files = FileUtils.listFiles(new File(path.toString()), acceptedFileTypes, true);
            ArrayList<File> f = new ArrayList<File>();
            f.addAll(files);
            Collections.sort(f);

            boolean bail;
            for (File file : f) {
                bail = false;
                if (!file.toString().toLowerCase().contains("sample")) {
                    log.info("Scanning file: " + file.toString());
                    if (this.file != null && !file.toString().contains(this.file)) {
                        bail = true;
                    } else {
                        for (Movie om : oldMovies) {
                            if (om.getPath().equals(file.toString())) {
                                bail = true;
                                break;
                            }
                        }
                    }
                    if (!bail) {
                        this.files.add(file.toPath());
                    }
                }
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < acceptedFileTypes.length; i++) {
                sb.append("(." + acceptedFileTypes[i] + ")");
                if (i != acceptedFileTypes.length - 1) {
                    sb.append("|");
                }
            }

            Movie movie;

            for (Path p : this.files) {
                String title = p.getFileName().toString();

                // File-Extension
                title = title.replaceAll(sb.toString(), "");

                // Make weird characters to Whitespace
                title = title.replaceAll("[:._-]|\\[|\\]", " ");

                // Release Groups (http://www.torrent-invites.com/showthread.php?t=199492)
                title = title.replaceAll(replace_groups, "");
                title = title.toLowerCase();
                // Quality (http://en.wikipedia.org/wiki/Pirated_movie_release_types)
                title = title.replaceAll(replace_mixed, "");

                // Languages
                title = title.replaceAll(replace, "");

                // Remove any unsavoury characters (this might screw up some stuff)
                title = title.replaceAll("[^A-Za-z0-9', ]", "");

                // Remove multi-spaces
                title = title.replaceAll(" +", " ");

                // Remove years
                title = title.replaceAll("([0-9]){4}", "");

                title = title.trim();
                title = WordUtils.capitalize(title);
                titles.put(p.toString(), title);
                movie = new Movie();
                movie.setTitle(title);
                movie.setPath(p.toString());
                movies.add(movie);
                log.info("Found: '" + title + "' (at " + p + ")");
            }
        }
    }

    @Override
    public void clear() {
        this.movies.clear();
        this.files.clear();
        this.path = null;
    }

    @Override
    public ArrayList<Movie> getMovies() {
        return movies;
    }

     /*
        Getter & Setter
     */

    @Override
    public void setPath(Path path) {
        this.path = path;
    }

    @Override
    public void setFile(String file) {
        this.file = file;
    }

     /*
        These setters are used by Spring to inject values.
     */

    public void setAcceptedFileTypes(String[] acceptedFileTypes) {
        this.acceptedFileTypes = acceptedFileTypes;
    }

    public void setReplace(String replace) {
        this.replace = replace;
    }

    public void setReplace_groups(String replace_groups) {
        this.replace_groups = replace_groups;
    }

    public void setReplace_mixed(String replace_mixed) {
        this.replace_mixed = replace_mixed;
    }
}
