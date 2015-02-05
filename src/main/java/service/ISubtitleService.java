package service;

import dto.Movie;
import dto.Subtitle;
import dto.SubtitleSearch;
import exception.ServiceException;
import service.subtitle.WordCloud;

import java.io.File;
import java.util.List;

/**
 * Created by powerowle on 14.01.2015.
 */
public interface ISubtitleService {
    /**
     * This methods validate the MinimumLength of the Language-Definition and pass it to the persistence layer
     * if the validation is successful. If there would be an problem in the persistence layer, this caused exception
     * would be catch and throw a new ServiceException. Furthermore, this methods counts the different words
     * and get the most 30 common words by using the private methods of "getUnsortedListWords", "getCommonWordString".
     * If the methods occurs without exceptions, it return a String with the languageName and the Number of the subtitles
     * of this movie in the same language.
     * @param path
     * @param m
     * @param language
     * @param comment
     * @return
     * @throws ServiceException
     */
    public Subtitle create(String path, Movie m, String language, String comment) throws ServiceException;

    /**
     * This method reads all subtitles of the selectedFile-Movie from the database and returns
     * a list with all subtitles of this movie.
     * @param selectedFile
     * @return
     * @throws ServiceException
     */
    public List<Subtitle> readSubtitlesForMovie(Movie selectedFile) throws ServiceException;

    /**
     * This method removes the Subtitle s from the database.
     * @param s
     * @throws ServiceException
     */
    public void delete(Subtitle s) throws ServiceException;

    /**
     * This method return the 30 commonSortedList by using the private method "getCommonWordList"
     * @param s
     * @return
     * @throws ServiceException
     */
    public List<WordCloud> commonSortedList(Subtitle s) throws ServiceException;

    /**
     * This method updates a subtitle object in the database.
     * @param m
     * @param s
     * @throws ServiceException
     */
    public void update(Movie m, Subtitle s) throws ServiceException;

    /**
     * This method searchs the subtitles on opensubtitles for Movie m and returns a list
     * of all available subtitles with their LanguageName, SubFormat, SubDownloadLink,
     * Information about HearingImpairedSubtitle and the name of the IDSubtitleFile.
     * If the connection to the opensubtitles-API via XmlRpc is failed, there will be
     * throw a new ServiceException.
     * @param m
     * @return
     * @throws ServiceException
     */
    public List<SubtitleSearch> searchSubtitles(Movie m) throws ServiceException;

    /**
     * This method close the connection to the opensubtitles-API with XmlRpc.
     * @throws ServiceException
     */
    public void logout() throws ServiceException;

    /**
     * This method open the connection to the opensubtitles-API with XmlRpc
     * @throws ServiceException
     */
    public void login() throws ServiceException;

    /**
     * This methods adds a Subtitle to the database from the opensubtitle-API with XmlRpc-connection
     * and creates a new Subtitle-file in the target-folder. If the SubtitleSearch s object contains
     * subtitle for hearing impaired people, this method defines a comment with "Subtitle for Hearing
     * Impaired" and uses the method create(..) for creating a new Subtitle in the database.
     * @param s
     * @param m
     * @throws ServiceException
     */
    public void addSubtitleExtern(SubtitleSearch s, Movie m) throws ServiceException;

    /**
     * This methods copy the already existing SubtitleFile in the target-Folder and return a language
     * if the SubtitleFile contains some hints in its name.
     * @param m
     * @param in
     * @return
     * @throws ServiceException
     */
    public String addSubtitleLocal(Movie m, File in) throws ServiceException;
}