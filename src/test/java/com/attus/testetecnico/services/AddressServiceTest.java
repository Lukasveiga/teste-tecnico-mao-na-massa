package com.attus.testetecnico.services;


import com.attus.testetecnico.ServiceTestConfiguration;
import com.attus.testetecnico.entities.Address;
import com.attus.testetecnico.entities.Person;
import com.attus.testetecnico.repositories.AddressRepository;
import com.attus.testetecnico.services.exceptions.EntityNotFoundException;
import com.attus.testetecnico.utils.GenerateTestEntities;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;

import static com.attus.testetecnico.utils.GenerateTestEntities.generatePerson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class AddressServiceTest implements ServiceTestConfiguration {

    @Mock
    AddressRepository addressRepository;

    @Mock
    PersonService personService;

    @InjectMocks
    AddressService addressService;

    Address addressTest;

    Person personTest;

    @BeforeEach
    void setUp() {
        personTest = generatePerson(1L, "Subject 89P13", LocalDate.of(1976, 7, 1));
        addressTest = GenerateTestEntities.generateAddress(1L, "Street Test", "555-556", 5,"City Test",
                "State Test", true, personTest);
    }

    @Test
    void testCreateNewAddressSuccess() {
        // Given
        when(this.personService.findOne(anyLong()))
                .thenReturn(personTest);

        when(this.addressRepository.save(any(Address.class)))
                .thenReturn(addressTest);

        // When
        var result = this.addressService.create(personTest.getId(), addressTest);

        // Then
        Assertions.assertThat(result).usingRecursiveAssertion().isEqualTo(addressTest);
        verify(this.addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    void testCreateNewAddressErrorEntityPersonNotFoundException() {
        // Given
        when(this.personService.findOne(anyLong()))
                .thenThrow(new EntityNotFoundException("Person with id %d was not found".formatted(personTest.getId())));

        // When - Then
        Assertions.assertThatThrownBy(() -> this.addressService.create(personTest.getId(), addressTest))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Person with id %d was not found".formatted(personTest.getId()));
        verify(this.addressRepository, times(0)).save(any(Address.class));
    }
}