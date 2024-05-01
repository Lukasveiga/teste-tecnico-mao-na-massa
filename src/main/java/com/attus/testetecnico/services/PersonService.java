package com.attus.testetecnico.services;

import com.attus.testetecnico.entities.Person;
import com.attus.testetecnico.repositories.PersonRepository;
import com.attus.testetecnico.services.exceptions.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Person> findAll() {
        return this.personRepository.findAll();
    }

    public List<Person> findAllPageable(int page) {
        var size = 5;
        var pageable = PageRequest.of(page, size);
        Page<Person> pagePerson = this.personRepository.findAll(pageable);
        return pagePerson.getContent();
    }

    @Transactional
    public Person update(Long id, Person newPerson) {
        var oldPerson = this.findOne(id);
        oldPerson.setFullName(newPerson.getFullName());
        oldPerson.setDateOfBirth(newPerson.getDateOfBirth());
        return this.personRepository.save(oldPerson);
    }
}
