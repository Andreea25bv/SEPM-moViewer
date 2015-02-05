package dto;

/**
 * Created by Toan on 29.11.2014.
 */
public class Role {

    private String role;
    private Person person;

    public Role(String role, Person person) {
        this.role = role;
        this.person = person;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
