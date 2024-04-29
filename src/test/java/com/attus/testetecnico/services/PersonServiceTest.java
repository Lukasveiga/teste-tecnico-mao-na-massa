package com.attus.testetecnico.services;

import com.attus.testetecnico.ServiceTestConfiguration;
import com.attus.testetecnico.entities.Address;
import com.attus.testetecnico.entities.Person;
import com.attus.testetecnico.repositories.PersonRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.List;

import static com.attus.testetecnico.utils.GenerateTestEntities.*;
import static org.mockito.Mockito.*;


class PersonServiceTest implements ServiceTestConfiguration {

    @Mock
    PersonRepository personRepository;

    @InjectMocks
    PersonService personService;

    Person personTest;

    List<Address> addressesTest;

    @BeforeEach
    void setUp() {
        personTest = generatePerson(1L, "Subject 89P13", LocalDate.of(1976, 7, 1));

        addressesTest = List.of(
                generateAddress(1L, "Street 1", "55555-444", "City 1", "State 1",true, personTest),
                generateAddress(2L, "Street 2", "55555-443", "City 2", "State 2",false, personTest),
                generateAddress(3L, "Street 3", "55555-442", "City 3", "State 3",false, personTest)
        );

        addressesTest.forEach(address -> personTest.addAddresses(address));
    }

    @Test
    void testCreateNewPersonSuccess() {
        // Given
        when(this.personRepository.save(personTest))
                .thenReturn(personTest);

        // When
        var savedPerson = this.personService.create(personTest);

        // Then
        Assertions.assertThat(savedPerson).usingRecursiveAssertion().isEqualTo(personTest);
        verify(this.personRepository, times(1)).save(any(Person.class));
    }
}