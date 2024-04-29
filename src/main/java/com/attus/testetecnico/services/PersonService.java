package com.attus.testetecnico.services;

import com.attus.testetecnico.entities.Person;
import com.attus.testetecnico.repositories.PersonRepository;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Person create(Person person) {
        return this.personRepository.save(person);
    }
}
