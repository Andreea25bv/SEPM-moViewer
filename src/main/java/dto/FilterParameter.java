package dto;

/**
 * Created by Ali on 09/12/14.
 */
public class FilterParameter {

    private String title = null;
    private String language = null;
    private String genre = null;
    private Boolean favorite = null;
    private Integer year = null;
    private Integer runTime = null;
    private String  sourceRating = null;
    private Double  ratingFrom = null;
    private Double  ratingTill = null;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getRunTime() {
        return runTime;
    }

    public void setRunTime(Integer runTime) {
        this.runTime = runTime;
    }

    public String getSourceRating() {
        return sourceRating;
    }

    public void setSourceRating(String sourceRating) {
        this.sourceRating = sourceRating;
    }

    public Double getRatingFrom() {
        return ratingFrom;
    }

    public void setRatingFrom(Double ratingFrom) {
        this.ratingFrom = ratingFrom;
    }

    public Double getRatingTill() {
        return ratingTill;
    }

    public void setRatingTill(Double ratingTill) {
        this.ratingTill = ratingTill;
    }

    @Override
    public String toString() {
        return "FilterParameter{" +
                "title='" + title + '\'' +
                ", language='" + language + '\'' +
                ", genre='" + genre + '\'' +
                ", favorite=" + favorite +
                ", year=" + year +
                ", runTime=" + runTime +
                ", sourceRating='" + sourceRating + '\'' +
                ", ratingFrom=" + ratingFrom +
                ", ratingTill=" + ratingTill +
                '}';
    }
}
