package com.attus.testetecnico.services;

import com.attus.testetecnico.entities.Person;
import com.attus.testetecnico.repositories.PersonRepository;
import com.attus.testetecnico.services.exceptions.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


@Service
public class PersonService {

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Transactional
    public Person create(Person person) {
        return this.personRepository.save(person);
    }

    public Person findOne(Long id) {
        return this.personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Person with id %d was not found".formatted(id)));
    }

    @Transactional
    public Person update(Long id, Person newPerson) {
        var oldPerson = this.findOne(id);
        oldPerson.setFullName(newPerson.getFullName());
        oldPerson.setDateOfBirth(newPerson.getDateOfBirth());
        return this.personRepository.save(oldPerson);
    }
}
