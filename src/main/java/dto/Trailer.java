package dto;

/**
 * Created by skYeYe on 09.01.2015.
 */
public class Trailer {

    private String linkText = null;
    private String webLink = null;

    public Trailer(String linkText, String webLink){
        this.linkText = linkText;
        this.webLink = webLink;
    }

    public String getLinkText() {
        return linkText;
    }

    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }

    public String getWebLink() {
        return webLink;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    @Override
    public String toString() {
        return "Trailer{" +
                "linkText='" + linkText + '\'' +
                ", webLink='" + webLink + '\'' +
                '}';
    }
}
