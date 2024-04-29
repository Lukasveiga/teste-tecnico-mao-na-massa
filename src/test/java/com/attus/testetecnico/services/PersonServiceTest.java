package com.attus.testetecnico.services;

import com.attus.testetecnico.ServiceTestConfiguration;
import com.attus.testetecnico.entities.Address;
import com.attus.testetecnico.entities.Person;
import com.attus.testetecnico.repositories.PersonRepository;
import com.attus.testetecnico.services.exceptions.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    @Test
    void testUpdatePersonSuccess() {
        // Given
        var updatePerson = generatePerson(1L, "Subject 89P13",
                LocalDate.of(1976, 7, 1));

        when(this.personRepository.findById(anyLong()))
                .thenReturn(Optional.of(personTest));

        when(this.personRepository.save(any(Person.class)))
                .thenReturn(personTest);

        // When
        var updatedPerson = this.personService.update(personTest.getId(), updatePerson);

        // Then
        Assertions.assertThat(updatedPerson).usingRecursiveAssertion().isEqualTo(personTest);
        verify(this.personRepository, times(1)).findById(anyLong());
        verify(this.personRepository, times(1)).save(any(Person.class));
    }

    @Test
    void testUpdatePersonErrorEntityNotFoundException() {
        // Given
        var updatePerson = generatePerson(1L, "Subject 89P13",
                LocalDate.of(1976, 7, 1));

        when(this.personRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        // When - Then
        Assertions.assertThatThrownBy(() -> this.personService.update(personTest.getId(), updatePerson))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Person with id %d was not found".formatted(personTest.getId()));
        verify(this.personRepository, times(0)).save(any(Person.class));
    }
}