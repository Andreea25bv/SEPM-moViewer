package dto;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Toan on 26.11.2014.
 */
public class Movie {

    private Integer mid;
    private Integer idTMDB = 0;
    private Integer idIMDB = 0;
    private String path = "unknownPath";
    private Boolean favorite = false;
    private String title = "unknownTitle";
    private Integer runTime = 0;
    private Integer year = 0;
    private String posterPath = "unknownPosterPath";
    private String language = "unknownLanguage";
    private String budget = "unknownBudget";
    private String revenue = "unknownRevenue";
    private String plot = "unknown";
    private String tagLine = "unknownTagLine";
    private String similarFilms = "unknownSimiliarFilms";    //ids (int) getrennt mit Beistrich ??!?
    private String genre = "unknownGenre";
    private List<Role> staff = new ArrayList<Role>();
    private List<Rating> ratingList = new ArrayList<Rating>();

    public Movie() {

    }

    public Movie(Integer mid, Integer idTMDB, Integer idIMDB, String path, Boolean favorite, String title,
                 Integer runTime, Integer year, String posterPath, String language, String budget, String revenue,
                 String plot, String tagLine, String similarFilms, String genre, List<Role> staff) {
        this.mid = mid;
        this.idTMDB = idTMDB;
        this.idIMDB = idIMDB;
        this.path = path;
        this.favorite = favorite;
        this.title = title;
        this.runTime = runTime;
        this.year = year;
        this.posterPath = posterPath;
        this.language = language;
        this.budget = budget;
        this.revenue = revenue;
        this.plot = plot;
        this.tagLine = tagLine;
        this.similarFilms = similarFilms;
        this.genre = genre;
        this.staff = staff;
    }

    public Integer getMid() {
        return mid;
    }

    public void setMid(Integer mid) {
        this.mid = mid;
    }

    public Integer getIdTMDB() {
        return idTMDB;
    }

    public void setIdTMDB(Integer idTMDB) {
        this.idTMDB = idTMDB;
    }

    public Integer getIdIMDB() {
        return idIMDB;
    }

    public void setIdIMDB(Integer idIMDB) {
        this.idIMDB = idIMDB;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getRunTime() {
        return runTime;
    }

    public void setRunTime(Integer runTime) {
        this.runTime = runTime;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public String getRevenue() {
        return revenue;
    }

    public void setRevenue(String revenue) {
        this.revenue = revenue;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getTagLine() {
        return tagLine;
    }

    public void setTagLine(String tagLine) {
        this.tagLine = tagLine;
    }

    public String getSimilarFilms() {
        return similarFilms;
    }

    public void setSimilarFilms(String similarFilms) {
        this.similarFilms = similarFilms;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public List<Role> getStaff() {
        return staff;
    }

    public void setStaff(List<Role> staff) {
        this.staff = staff;
    }

    public String getIsFavorite(){
        if(favorite == false){
            return "NO";
        }
        return "YES";
    }

    public List<Rating> getRatingList() {
        return ratingList;
    }

    public void setRatingList(List<Rating> ratingList) {
        this.ratingList = ratingList;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "mid=" + mid +
                ", idTMDB=" + idTMDB +
                ", idIMDB=" + idIMDB +
                ", path='" + path + '\'' +
                ", favorite=" + favorite +
                ", title='" + title + '\'' +
                ", runTime=" + runTime +
                ", year=" + year +
                ", posterPath='" + posterPath + '\'' +
                ", language='" + language + '\'' +
                ", budget='" + budget + '\'' +
                ", revenue='" + revenue + '\'' +
                ", plot='" + plot + '\'' +
                ", tagLine='" + tagLine + '\'' +
                ", similarFilms='" + similarFilms + '\'' +
                ", genre='" + genre + '\'' +
                '}';
    }
}
