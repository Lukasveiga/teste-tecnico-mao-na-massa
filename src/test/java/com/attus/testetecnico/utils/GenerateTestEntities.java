package com.attus.testetecnico.utils;

import com.attus.testetecnico.entities.Address;
import com.attus.testetecnico.entities.Person;

import java.time.LocalDate;

public class GenerateTestEntities {

    public static Person generatePerson(Long id, String fullName, LocalDate dateOfBirth) {
        var personTest = new Person();
        personTest.setId(id);
        personTest.setFullName(fullName);
        personTest.setDateOfBirth(dateOfBirth);
        return personTest;
    }

    public static Address generateAddress(Long id, String street, String zipCode, String city, String state, boolean main, Person person) {
        var address = new Address();
        address.setId(id);
        address.setStreet(street);
        address.setZipCode(zipCode);
        address.setCity(city);
        address.setState(state);
        address.setMain(main);
        address.setPerson(person);
        return address;
    }
}
