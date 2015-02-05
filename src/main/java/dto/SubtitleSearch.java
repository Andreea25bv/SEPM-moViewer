package dto;

/**
 * Created by powerowle on 19.01.2015.
 */
public class SubtitleSearch {

    private String languageName;
    private String subFormat;
    private String subHearingImpaired;
    private String subDownloadLink;
    private String idSubtitleFile;

    public SubtitleSearch(String languageName, String subFormat, String subHearingImpaired, String subDownloadLink, String idSubtitleFile) {
        this.languageName = languageName;
        this.subFormat = subFormat;
        this.subHearingImpaired = subHearingImpaired;
        this.subDownloadLink = subDownloadLink;
        this.idSubtitleFile = idSubtitleFile;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getSubFormat() {
        return subFormat;
    }

    public void setSubFormat(String subFormat) {
        this.subFormat = subFormat;
    }

    public String getSubHearingImpaired() {
        return subHearingImpaired;
    }

    public void setSubHearingImpaired(String subHearingImpaired) {
        this.subHearingImpaired = subHearingImpaired;
    }

    public String getSubDownloadLink() {
        return subDownloadLink;
    }

    public void setSubDownloadLink(String subDownloadLink) {
        this.subDownloadLink = subDownloadLink;
    }

    public String getIdSubtitleFile() {
        return idSubtitleFile;
    }

    public void setIdSubtitleFile(String idSubtitleFile) {
        this.idSubtitleFile = idSubtitleFile;
    }
}
