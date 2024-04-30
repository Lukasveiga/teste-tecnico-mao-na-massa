package com.attus.testetecnico.repositories;

import com.attus.testetecnico.entities.Person;
import com.attus.testetecnico.utils.GenerateTestEntities;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static com.attus.testetecnico.utils.GenerateTestEntities.generatePerson;

@ActiveProfiles("test")
@DataJpaTest
class AddressRepositoryTest {

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    PersonRepository personRepository;

    Person personTest;

    @BeforeEach
    void setUp() {
        personTest = generatePerson(null, "Subject 89P13", LocalDate.of(1976, 7, 1));

        this.personRepository.saveAndFlush(personTest);

        for (var i = 0; i < 6; i++) {
            this.addressRepository.saveAndFlush(GenerateTestEntities.generateAddress(null, "Street Test", "555-556", 5,"City Test",
                    "State Test", true, personTest));
        }
    }

    @Test
    void testFindAllByPersonId(){
        //When
        var result = this.addressRepository.findAllByPersonId(personTest.getId());

        // Then
        Assertions.assertThat(result).hasSize(6);
    }

    @Test
    void testFindAllByPersonIdPageable(){
        // Given
        var pageable = PageRequest.of(0, 5);

        //When
        var result = this.addressRepository.findAllByPersonId(personTest.getId(), pageable);

        // Then
        Assertions.assertThat(result).hasSize(5);
    }
}