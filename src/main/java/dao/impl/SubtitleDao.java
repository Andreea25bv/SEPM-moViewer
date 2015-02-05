package dao.impl;

import dao.AbstactDaoManager;
import dao.ISubtitleDao;
import dto.Subtitle;
import exception.SubtitlePersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Ali Edan on ${25/11/2014}.
 */
//@Component
public class SubtitleDao extends AbstactDaoManager implements ISubtitleDao{

    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private DriverManagerDataSource dataSource;

    @Override
    public Subtitle createSubtitle(Subtitle subtitle) throws SubtitlePersistenceException {
        logger.info("Entering createSubtitleMethod with parameters:" + subtitle);
        checkSubtitleValidation(subtitle);
        sql = "INSERT INTO Subtitle VALUES (NULL,?,?,?,?,?,?,? )";
        try {
            ps = dataSource.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, subtitle.getMid());
            ps.setString(2, subtitle.getLanguage());
            ps.setString(3, subtitle.getCommonWords());
            ps.setString(4, subtitle.getLanguagePath());
            ps.setInt(5, subtitle.getCommonWordsCount());
            ps.setString(6, subtitle.getComment());
            ps.setInt(7, subtitle.getNumberOfWords());
            ps.executeUpdate();

            int id = 0;
            rs = ps.getGeneratedKeys();
            rs.next();
            id = rs.getInt(1);

            subtitle.setSid(id);
            logger.debug(subtitle);

        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new SubtitlePersistenceException(e.getMessage());
        }
        if(subtitle.getSid() == null )throw new IllegalArgumentException("Subtitle Id can not be null");
        return subtitle;
    }

    @Override
    public List<Subtitle> readSubtitle(Integer mid) throws SubtitlePersistenceException{
        logger.info("Entering readSubtitleByIdMethod with parameters:" + mid);
        if (mid == null) throw new IllegalArgumentException("Illegal Argument: mid can not be null");
        List<Subtitle> listOfSubtitles = new ArrayList<Subtitle>();

        sql = "SELECT * FROM Subtitle WHERE mid = ?";

        try {
            ps = dataSource.getConnection().prepareStatement(sql);
            ps.setInt(1, mid);
            rs = ps.executeQuery();
            while(rs.next()){
                Subtitle sub = new Subtitle (rs.getInt(1),rs.getInt(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getInt(6),rs.getString(7),rs.getInt(8));
                checkSubtitleValidation(sub);
                listOfSubtitles.add(sub);
            }
        }catch(SQLException s){
            logger.error(s.getMessage(),s);
            throw new SubtitlePersistenceException(s.getMessage(),s);
        }
        return listOfSubtitles;
    }

    @Override
    public void updateSubtitle(Subtitle subtitle) throws SubtitlePersistenceException{
        logger.info("Entering updateSubtitleMethod with parameters:"+ subtitle);
        checkSubtitleValidation(subtitle);
        if(subtitle.getSid() == null) throw new IllegalArgumentException("Subtitle Id can not be null");
        sql = "UPDATE Subtitle SET mid = ?, language = ? , commonWords = ? ,languagePath = ? " +
                ", commonWordsCount = ?, comment =? , numberOfWords =? WHERE sid = ? ";

        try{
            ps = dataSource.getConnection().prepareStatement(sql);
            ps.setInt(1, subtitle.getMid());
            ps.setString(2, subtitle.getLanguage());
            ps.setString(3,subtitle.getCommonWords());
            ps.setString(4,subtitle.getLanguagePath());
            ps.setInt(5, subtitle.getCommonWordsCount());
            ps.setString(6, subtitle.getComment());
            ps.setInt(7, subtitle.getNumberOfWords());
            ps.setInt(8,subtitle.getSid());

            ps.executeUpdate();

        }catch(SQLException s){
            logger.error(s.getMessage());
            throw new SubtitlePersistenceException(s.getMessage(),s);
        }
    }

    @Override
    public void deleteSubtitle(Subtitle subtitle) throws SubtitlePersistenceException{
        logger.info("Entering deleteSubtitleMethod with parameters:"+ subtitle);
        checkSubtitleValidation(subtitle);
        if(subtitle.getSid() == null) throw new IllegalArgumentException("Subtitle Id can not be null");
        sql = "DELETE FROM Subtitle WHERE sid = ?";
        try {
            ps = dataSource.getConnection().prepareStatement(sql);
            ps.setInt(1, subtitle.getSid());
            ps.executeUpdate();
        }catch(SQLException s){
            logger.error(s.getMessage());
            throw new SubtitlePersistenceException(s.getMessage(),s);
        }
    }

    /**
     * This method checks if one of the parameters of the subtitle object is null
     *
     * @param subtitle
     */
    private void checkSubtitleValidation(Subtitle subtitle){
        if(subtitle == null){
            throw new IllegalArgumentException("Subtitle object can not be null");
        }
        if(subtitle.getLanguage() == null){
            throw new IllegalArgumentException("Subtitle language can not be null");
        }
        if(subtitle.getComment() == null){
            throw new IllegalArgumentException("Subtitle comment can not be null");
        }
        if(subtitle.getCommonWords() == null){
            throw new IllegalArgumentException("Subtitle commonWords can not be null");
        }
        if(subtitle.getLanguagePath() == null ){
            throw new IllegalArgumentException("Subtitle languagePath can not be null");
        }
        if(subtitle.getMid() == null){
            throw new IllegalArgumentException("Subtitle mid can not be null");
        }
    }
}
