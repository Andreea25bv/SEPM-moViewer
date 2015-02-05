package service;

import dto.Movie;

import java.nio.file.Path;
import java.util.ArrayList;

public interface ICrawlerService {

    /**
     * Will scan the previously set path for movies that are support (only extensions-checks are performed).
     */
    public void scan();

    /**
     * Will clear the set path and the scanned files.
     */
    public void clear();

    /**
     * Will set the path for later scanning, has no function after the files have been scanned.
     *
     * @param path The path to be set to look for files.
     */
    public void setPath(Path path);

    /**
     * Gets the cleaned titles of the files that have been scanned.
     * Titles are assumed to be the filesnames which are cleaned as much as possible by automatic replacements.
     *
     * @return An ArrayList of Movie-objects that only have the title- and path-properties set.
     */
    public ArrayList<Movie> getMovies();

    /**
     * This function is used to scan in a single file.
     * For ease of adaption the single file is scanned-in like the others are when adding multiple files.
     * This parameter needs to be set to know and verify which file is to be scanned. (The others in the same folder are
     * ignored.
     *
     * @param file The file to be scanned in.
     */
    public void setFile(String file);

}
