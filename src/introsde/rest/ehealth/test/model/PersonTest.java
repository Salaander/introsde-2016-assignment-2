package introsde.rest.ehealth.test.model;
import static org.junit.Assert.*;
import introsde.rest.ehealth.model.Person;
import java.util.Calendar;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;   
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class PersonTest {

    @Test
    public void readPersonListTest() {
        System.out.println("--> TEST: readPersonList");
        List<Person> list = em.createNamedQuery("Person.findAll", Person.class)
                .getResultList();
        for (Person person : list) {
            System.out.println("--> Person = "+person.toString());
        }
        assertTrue(list.size()>0);
    }

    @Test
    public void addPersonWithDaoTest() {
        System.out.println("--> TEST: addPersonWithDao");

        List<Person> list = Person.getAll();
        int personOriginalCount = list.size();

        Person p = new Person();
        p.setFirstname("Pinco");
        p.setLastname("Pallino");
        p.setBirthdate("1945-01-01 00:00:00");

        System.out.println("--> TEST: addPersonWithDao ==> persisting person");
        Person.savePerson(p);
        assertNotNull("Id should not be null", p.getIdPerson());

        System.out.println("--> TEST: addPersonWithDao ==> getting the list");
        list = Person.getAll();
        assertEquals("Table has two entities", personOriginalCount+1, list.size());

        Person newPerson = Person.getPersonById(p.getIdPerson());

        System.out.println("--> TEST: addPersonWithDao ==> removing new person");
        Person.removePerson(newPerson);
        list = Person.getAll();
        assertEquals("Table has two entities", personOriginalCount, list.size());
    }

    @BeforeClass
    public static void beforeClass() {
        System.out.println("Testing JPA on lifecoach database using 'introsde-jpa' persistence unit");
        emf = Persistence.createEntityManagerFactory("introsde-jpa");
        em = emf.createEntityManager();
    }

    @AfterClass
    public static void afterClass() {
        em.close();
        emf.close();
    }

    @Before
    public void before() {
        tx = em.getTransaction();
    }

    private static EntityManagerFactory emf;
    private static EntityManager em;
    private EntityTransaction tx;

}