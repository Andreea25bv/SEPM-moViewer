package dao;

import dto.Movie;
import dto.Person;
import dto.Role;
import exception.PersonPersistenceException;

import java.util.List;

/**
 * Created by Toan on 26.11.2014.
 */
public interface IPersonDao {

    /**
     * This Method is to insert new Person in the DB
     *
     * @param p
     * @return the inserted Person
     * @throws PersonPersistenceException if there is a Problem occurred
     */
    public Person createPerson (Person p) throws PersonPersistenceException;

    /**
     * This method is searching Person in the DB with the given id
     *
     * @param pid
     * @return returns Person
     * @throws PersonPersistenceException if a Problem occurred
     */
    public Person readPersonById (Integer pid) throws PersonPersistenceException;

    /**
     * This method is to updated the given Person Object throw but not the Person id.
     *
     * @param p
     * @return the updated person
     * @throws PersonPersistenceException if a Problem occurred
     */
    public void updatePerson (Person p) throws PersonPersistenceException;

    /**
     * This method is to delete the given Person
     *
     * @param p
     * @throws PersonPersistenceException if a Problem occurred
     */
    public void deletePerson (Person p) throws PersonPersistenceException;

    /**
     * this method giving all the saved Persons in the DB
     *
     * @return list of all Persons we have in the DB
     * @throws PersonPersistenceException if a Problem occurred
     */
    public List<Person> listAllPersons () throws PersonPersistenceException;

    /**
     * Connect a role(person) to a movie
     * @param movie The movie that is getting connected
     * @param role The Role that is getting connected
     */
    public void connect(Movie movie, Role role) throws PersonPersistenceException;

    /**
     * Essentially does a read. Gets a single person by it's name.
     * @param person the person who's name will be searched for
     * @throws PersonPersistenceException fires when the person is not found (no matches) or there is more then one match!
     */
    public Person getPersonByName(Person person) throws PersonPersistenceException;

    /**
     * Essentially does a read. Gets a single person by her/his TMDBID.
     * @param person the person who's name will be searched for
     * @throws PersonPersistenceException fires when the person is not found (no matches) or there is more then one match!
     */
    public Person getPersonByTmdbid(Person person) throws PersonPersistenceException;

}
