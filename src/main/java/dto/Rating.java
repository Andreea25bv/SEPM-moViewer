package dto;

/**
 * Created by Toan on 26.11.2014.
 */
public class Rating {

    private Integer mid;        //foreign key
    private Double rating;
    private String source;

    public Rating(Integer mid, Double rating, String source) {
        this.mid = mid;
        this.rating = rating;
        this.source = source;
    }

    public Integer getMid() {
        return mid;
    }

    public void setMid(Integer mid) {
        this.mid = mid;
    }

    public Double getRating() { return rating;}

    public void setRating(Double rating) { this.rating = rating; }

    public String getSource() { return source;}

    public void setSource(String source) { this.source = source; }

    @Override
    public String toString() {
        return "Rating{" +
                "mid=" + mid +
                ", rating=" + rating +
                ", source=" + source +
                '}';
    }
}
