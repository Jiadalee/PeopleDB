package com.neutrinosys.peopledb.repository;

import com.neutrinosys.peopledb.model.Person;

import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class PeopleRepository {
    public static final String SAVE_PERSON_SQL = String.format("INSERT INTO PEOPLE (FIRST_NAME, LAST_NAME, DOB) VALUES(?, ?, ?)");
    public static final String FIND_BY_ID_SQL = "SELECT ID, FIRST_NAME, LAST_NAME, DOB FROM PEOPLE WHERE ID=?";
    private Connection connection;
    public PeopleRepository(Connection connection) {
        this.connection = connection;

    }

    public Person save(Person person) throws SQLException {
        try {
            PreparedStatement ps = connection.prepareStatement(SAVE_PERSON_SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, person.getFirstName());
            ps.setString(2, person.getLastName());
            ps.setTimestamp(3, Timestamp.valueOf(person.getDob().withZoneSameInstant(ZoneId.of("+0")).toLocalDateTime()));
            int recordsAffected =  ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            while(rs.next()){
                long id = rs.getLong(1);
                person.setId(id);
                System.out.println(person);
            }
            System.out.printf("Records affected: %d%n", recordsAffected);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return person;
    }

    public Optional<Person> findById(Long id) {
        Person person = null;

        try {
            PreparedStatement ps = connection.prepareStatement(FIND_BY_ID_SQL);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                long personId = rs.getLong("ID");
                String firstName = rs.getString("FIRST_NAME");
                String lastName = rs.getString("LAST_NAME");
                ZonedDateTime dob = ZonedDateTime.of(rs.getTimestamp("DOB").toLocalDateTime(), ZoneId.of("+0"));
                person = new Person(10L, firstName, lastName, dob);
                person.setId(personId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(person);
    }


    public long count() throws SQLException {
        long count = 0;
        PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM PEOPLE");
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            count = rs.getLong(1);
        }
        return count;
    }

    public void delete(Person person) {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM PEOPLE WHERE ID=?");
            ps.setLong(1, person.getId());
            int affectedRecordCount = ps.executeUpdate();
            System.out.println(affectedRecordCount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    //input is the Person array with people
//    public void delete(Person...people) {
//        for (Person person : people){
//            delete(person);
//        }
//    }

    public void deleteMultiplePeople(Person...people){
        try {
            Statement stmt = connection.createStatement();
            String ids = Arrays.stream(people).map(p -> p.getId()).map(String::valueOf).collect(Collectors.joining(","));
            int affectedRecordCount = stmt.executeUpdate("DELETE FROM PEOPLE WHERE ID IN (:ids)".replace(":ids", ids));
            System.out.println(affectedRecordCount);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
