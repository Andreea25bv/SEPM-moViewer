package dao;

import dto.Subtitle;
import exception.SubtitlePersistenceException;

import java.util.List;

/**
 * Created by Toan on 26.11.2014.
 */
public interface ISubtitleDao {

    /**
     * This method is to insert new Subtitle in the DB
     *
     * @param subtitle
     * @return subtitle
     * @throws SubtitlePersistenceException if a Problem occurred
     */
    public Subtitle createSubtitle(Subtitle subtitle) throws SubtitlePersistenceException;

    /**
     * This method search in the DB all subtitles which is related to the given movie id.
     *
     * @param mId
     * @return list of all the found subtitles entries belong to the given movie id
     * @throws SubtitlePersistenceException if a Problem occurred
     */
    public List<Subtitle> readSubtitle(Integer mId) throws SubtitlePersistenceException;

    /**
     * This method is to update the given Subtitle
     *
     * @param subtitle
     * @throws SubtitlePersistenceException if a Problem occurred
     */
    public void updateSubtitle(Subtitle subtitle) throws SubtitlePersistenceException;

    /**
     * this method is to delete the given Subtitle object
     *
     * @param subtitle
     * @throws SubtitlePersistenceException is a Problem occurred
     */
    public void deleteSubtitle(Subtitle subtitle) throws SubtitlePersistenceException;

}
