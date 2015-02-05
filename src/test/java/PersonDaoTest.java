import dto.Movie;
import dto.Person;
import dto.Role;
import exception.MoviePersistenceException;
import exception.PersonPersistenceException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by Toan on 25.11.2014.
 */
public class PersonDaoTest extends AbstractDaoTest {

    @Before
    public void setUpPerson(){
        person = new Person(0, "jon doe", "biography", "/xxPMucou2wRDxLrud8i2D4dsywh.jpg");
    }

    @After
    public void tearDownPerson(){
        person = null;
    }

    /**
     * This Method test the createPerson method by inserting new one and checking
     * if it is saved in the DB
     *
     * @throws PersonPersistenceException
     */
    @Test
    public void createValidPerson() throws PersonPersistenceException {
        person = personDao.createPerson(person);
        assertThat(personDao.readPersonById(person.getPid()).getPid(), equalTo(person.getPid()));
    }

    /**
     * this method test the createPerson method by inserting new null Person
     * the method will throw exception.
     *
     * @throws IllegalArgumentException by inserting null object of person throws exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void createInvalidPerson() throws PersonPersistenceException {
        person.setBiography(null);
        personDao.createPerson(person);
    }

    /**
     * This Method test the readPersonById method by inserting new person and then
     * search for the inserted person by it is Id and check if the found Person
     * is same like the found one by checking the id of them.
     *
     * @throws PersonPersistenceException if a Problem occurred
     */
    @Test
    public void readValidPersonById() throws PersonPersistenceException {
        person = personDao.createPerson(person);
        Person personCopy = personDao.readPersonById(person.getPid());
        assertThat(personCopy.getPid(), equalTo(person.getPid()));
    }

    /**
     * This Method to test the readPerson method by searching for null id
     *
     * @throws IllegalArgumentException if the pid = null
     */
    @Test(expected = IllegalArgumentException.class)
    public void testReadWithNullPidThrowsException() throws PersonPersistenceException {
        personDao.readPersonById(null);
    }

    /**
     * This Method test the deletePerson Method by inserting new Person and then deleted
     * after deleting and searching for the same person id the method will throw IllegalArgument
     * exception coz the searched person not found and the return person will be null.
     *
     * @throws PersonPersistenceException         if a Problem occurred
     * @throws IllegalArgumentException after deleting the person and searching for it again the return person
     *                                            is null and the method throws IllegalArgumentException
     */
    @Test(expected = PersonPersistenceException.class)
    public void testDeletePersonThrowsException() throws PersonPersistenceException {
        person = personDao.createPerson(person);
        Person personCopy = personDao.readPersonById(person.getPid());
        assertThat(personCopy.getPid(), equalTo(person.getPid()));
        personDao.deletePerson(person);
        personDao.readPersonById(person.getPid());
    }

    /**
     * This Method test the deletePerson with null pid.
     *
     * @throws IllegalArgumentException
     */

    @Test(expected = PersonPersistenceException.class)
    public void testDeletePersonThrowsExceptionPidNull() throws PersonPersistenceException {
        personDao.deletePerson(person);
    }

    /**
     * This Method test the updatePerson method
     *
     * @throws PersonPersistenceException
     */

    @Test
    public void testUpdatePersonThrowsNoException() throws PersonPersistenceException{
        person = personDao.createPerson(person);
        assertThat(personDao.readPersonById(person.getPid()).getBiography(), equalTo(person.getBiography()));
        person.setBiography("testBiography");
        personDao.updatePerson(person);
        assertThat(personDao.readPersonById(person.getPid()).getBiography(), equalTo(person.getBiography()));
    }

    /**
     * This Method test the updatePerson method
     *
     * @throws PersonPersistenceException if a Problem occurred
     * @throws IllegalArgumentException when person null
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUpdatePersonThrowsException() throws PersonPersistenceException{
        personDao.updatePerson(null);
    }

    /**
     * This Method test the listAllPerson method and deletes them
     *
     * @throws PersonPersistenceException if a Problem occurred
     * @throws IllegalArgumentException when person null
     */
    @Test
    public void testListAllPersons() throws PersonPersistenceException {
        List<Person> persons = personDao.listAllPersons();
        try {
            for (Person person: persons) {
                personDao.deletePerson(person);
            }
        }catch(Exception e){
            fail("Test should not fail here");
        }
        Iterator<Person> personIterator = persons.iterator();
        while(personIterator.hasNext()){
            Person p = personIterator.next();
            personIterator.remove();
        }
        assertThat(personDao.listAllPersons().size(), equalTo(persons.size()));
    }

    /**
     * This Method test the connect Role to Movie
     *
     * @throws PersonPersistenceException
     * @throws MoviePersistenceException
     */
    @Test
    public void testConnectPersonToMovie() throws PersonPersistenceException, MoviePersistenceException {
        person = personDao.createPerson(person);
        role = new Role("actor", person);
        staff = new ArrayList<Role>();
        staff.add(role);
        movie = new Movie(null, 5, 5, "testPath", false, "testTitle", 50, 1999, "/680X9apSqmAcebLg8evnnUeQNeI.jpg", "german", "500000", "700000",
                "plotTest", "testTagLine", "listOfSimilarFilms", "testGenre", staff);
        movie = movieDao.createMovie(movie);
        Role director = new Role("director", person);
        personDao.connect(movie, director);
        List<Role> roleList = movieDao.readMovieById(false, movie.getMid()).getStaff();
        for(int i = 0; i < roleList.size(); i++) {
            if(roleList.get(i).getRole().equals(director.getRole())) {
                assertThat(roleList.get(i).getRole(), equalTo(director.getRole()));
            }
        }
    }

    /**
     * This Method test the getPersonByName
     *
     * @throws PersonPersistenceException
     */
    @Test
    public void testGetPersonByName() throws PersonPersistenceException {
        person = personDao.createPerson(person);
        assertThat(personDao.getPersonByName(person).getName(), equalTo(person.getName()));
    }
}