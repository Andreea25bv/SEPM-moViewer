package dto;

/**
 * Created by Toan on 26.11.2014.
 */
public class Subtitle {

    private Integer sid;
    private Integer mid;
    private String language;
    private String commonWords;
    private String languagePath;
    private Integer commonWordsCount;
    private String comment;
    private Integer numberOfWords;

    public Subtitle(Integer sid, Integer mid, String language, String commonWords, String languagePath, Integer commonWordsCount, String comment, Integer numberOfWords) {
        this.sid = sid;
        this.mid = mid;
        this.language = language;
        this.commonWords = commonWords;
        this.languagePath = languagePath;
        this.commonWordsCount = commonWordsCount;
        this.comment = comment;
        this.numberOfWords = numberOfWords;
    }

    public Subtitle() {}

    public Integer getSid() {
        return sid;
    }

    public void setSid(Integer sid) {
        this.sid = sid;
    }

    public Integer getMid() {
        return mid;
    }

    public void setMid(Integer mid) {
        this.mid = mid;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCommonWords() {
        return commonWords;
    }

    public void setCommonWords(String commonWords) {
        this.commonWords = commonWords;
    }

    public String getLanguagePath() {
        return languagePath;
    }

    public void setLanguagePath(String languagePath) {
        this.languagePath = languagePath;
    }

    public Integer getCommonWordsCount() {
        return commonWordsCount;
    }

    public void setCommonWordsCount(Integer commonWordsCount) {
        this.commonWordsCount = commonWordsCount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getNumberOfWords() {
        return numberOfWords;
    }

    public void setNumberOfWords(Integer numberOfWords) {
        this.numberOfWords = numberOfWords;
    }

    @Override
    public String toString() {
        return "Subtitle{" +
                "sid=" + sid +
                ", mid=" + mid +
                ", language='" + language + '\'' +
                ", commonWords='" + commonWords + '\'' +
                ", languagePath='" + languagePath + '\'' +
                ", commonWordsCount=" + commonWordsCount +
                ", comment='" + comment + '\'' +
                ", numberOfWords=" + numberOfWords +
                '}';
    }

}
