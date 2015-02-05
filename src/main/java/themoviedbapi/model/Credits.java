package themoviedbapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import themoviedbapi.Utils;
import themoviedbapi.model.core.IdElement;
import themoviedbapi.model.people.Person;
import themoviedbapi.model.people.PersonCast;
import themoviedbapi.model.people.PersonCrew;

import java.util.ArrayList;
import java.util.List;


public class Credits extends IdElement {

    @JsonProperty("crew")
    List<PersonCrew> crew;

    @JsonProperty("cast")
    List<PersonCast> cast;

    @JsonProperty("guest_stars")
    List<PersonCast> guestStars;


    public List<PersonCrew> getCrew() {
        return crew;
    }


    public List<PersonCast> getCast() {
        return cast;
    }


    public List<PersonCast> getGuestStars() {
        return guestStars;
    }


    /**
     * Convenience wrapper to get all people involved in the movie>
     */
    public List<Person> getAll() {
        List<Person> involved = new ArrayList<Person>();

        involved.addAll(Utils.nullAsEmpty(crew));
        involved.addAll(Utils.nullAsEmpty(cast));
        involved.addAll(Utils.nullAsEmpty(guestStars));

        return involved;
    }
}
