
package themoviedbapi.model.people;

import com.fasterxml.jackson.annotation.JsonProperty;
import themoviedbapi.model.core.AbstractJsonMapping;

import java.util.List;


public class PersonCredits extends AbstractJsonMapping {

    @JsonProperty("cast")
    private List<PersonCredit> cast;
    @JsonProperty("crew")
    private List<PersonCredit> crew;


    public List<PersonCredit> getCast() {
        return cast;
    }


    public List<PersonCredit> getCrew() {
        return crew;
    }
}
