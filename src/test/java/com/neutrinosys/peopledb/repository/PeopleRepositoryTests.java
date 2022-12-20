package com.neutrinosys.peopledb.repository;

import com.neutrinosys.peopledb.model.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PeopleRepositoryTests {

    private Connection connection;
    private PeopleRepository repo;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:~/peopletest".replace("~", System.getProperty("user.home")));
        connection.setAutoCommit(false);
        repo = new PeopleRepository(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    public void canSaveOnePerson() throws SQLException {
        Person john  = new Person(10L, "John", "Smith", ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6")));
        Person savedPerson =  repo.save(john);
        assertThat(savedPerson.getId()).isGreaterThan(0);
    }
    @Test
    public void canSaveTwoPeople() throws SQLException {
        Person john  = new Person(10L, "John", "Smith", ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6")));
        Person bobby  = new Person(10L, "Bobby", "Smith", ZonedDateTime.of(1970, 9, 15, 10, 11, 0, 0, ZoneId.of("-6")));
        Person savedPerson1 =  repo.save(john);
        Person savedPerson2 =  repo.save(bobby);
        assertThat(savedPerson1.getId()).isNotEqualTo(savedPerson2.getId());
        //assertThat(savedPerson1.getId()).isGreaterThan(0);
    }

    @Test
    public void canFindPersonById() throws SQLException {
        Person savedPerson =  repo.save(new Person(10L, "test", "jackson", ZonedDateTime.now()));
        Person foundPerson = repo.findById(savedPerson.getId()).get();
        assertThat(foundPerson).isEqualTo(savedPerson);
    }

    @Test
    public void testPersonIdNotFound(){
        Optional<Person> foundPerson = repo.findById(-1L);
        assertThat(foundPerson).isEmpty();
    }

    @Test
    public void canGetCount() throws SQLException {
        long startCount =  repo.count();
        repo.save(new Person(10L, "John1", "Smith", ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6"))));
        repo.save(new Person(10L, "John1", "Smith", ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6"))));
        long endCount = repo.count();
        assertThat(endCount).isEqualTo(startCount + 2);
    }

    @Test
    public void canDelete() throws SQLException {
        Person savedPerson = repo.save(new Person(10L, "John1", "Smith", ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6"))));
        long startCount = repo.count();
        repo.delete(savedPerson);
        long endCount = repo.count();
        assertThat(endCount).isEqualTo(startCount - 1);
    }

    @Test
    public void canDeleteMultiplePeople() throws SQLException {
        Person p1 = repo.save(new Person(10L, "John1", "Smith", ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6"))));
        Person p2 = repo.save(new Person(10L, "John1", "Smith", ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6"))));
        repo.deleteMultiplePeople(p1, p2);
    }

    @Test
    public void experiment(){
        Person p1 = new Person(10L, null, null, null);
        Person p2 = new Person(20L, null, null, null);
        Person p3 = new Person(30L, null, null, null);
        Person p4 = new Person(40L, null, null, null);
        Person p5 = new Person(50L, null, null, null);

        //DELETE FROM PERSON WHERE ID IN (10, 20, 30, 40, 50);
        Person[] people = Arrays.asList(p1, p2, p3, p4, p5).toArray(new Person[]{});
        //convert stream of person objects to be long ids using stream api
        //convert a stream of long ids to be stream of text ids;
        String ids = Arrays.stream(people).map(p -> p.getId()).map(String::valueOf).collect(Collectors.joining(","));
        System.out.println(ids);

    }

}





