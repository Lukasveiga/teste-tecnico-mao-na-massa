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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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

    @Test
    void testFindOnePersonSuccess() {
        // Given
        when(this.personRepository.findById(anyLong()))
                .thenReturn(Optional.of(personTest));

        // When
        var result = this.personService.findOne(personTest.getId());

        // Then
        Assertions.assertThat(result).usingRecursiveAssertion().isEqualTo(personTest);
    }

    @Test
    void testFindOneErrorEntityNotFoundException() {
        // Given
        when(this.personRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        // When - Then
        Assertions.assertThatThrownBy(() -> this.personService.findOne(personTest.getId()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Person with id %d was not found".formatted(personTest.getId()));
    }

    @Test
    void testFindAllSuccess() {
        // Given
        when(this.personRepository.findAll())
                .thenReturn(List.of(personTest));

        // When
        var result = this.personService.findAll();

        // Then
        Assertions.assertThat(result).hasSize(1);
        Assertions.assertThat(result.get(0)).usingRecursiveAssertion().isEqualTo(personTest);
    }

    @Test
    void testFindAllPageableSuccess() {
        // Given
        var size = 5;
        var pageable = PageRequest.of(0, size);

        when(this.personRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(personTest)));

        // When
        var result = this.personService.findAllPageable(0);

        // Then
        Assertions.assertThat(result).hasSize(1);
        Assertions.assertThat(result.get(0)).usingRecursiveAssertion().isEqualTo(personTest);
    }
}