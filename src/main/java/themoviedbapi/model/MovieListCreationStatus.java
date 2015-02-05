package themoviedbapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import themoviedbapi.model.core.StatusCode;


public class MovieListCreationStatus extends StatusCode {

    @JsonProperty("list_id")
    private String listId;


    public String getListId() {
        return listId;
    }
}
