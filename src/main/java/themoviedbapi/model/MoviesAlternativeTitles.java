
package themoviedbapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import themoviedbapi.model.core.AbstractJsonMapping;

import java.util.List;


public class MoviesAlternativeTitles extends AbstractJsonMapping {

    @JsonProperty("titles")
    private List<AlternativeTitle> titles;


    public List<AlternativeTitle> getTitles() {
        return titles;
    }


    public void setTitles(List<AlternativeTitle> titles) {
        this.titles = titles;
    }
}
