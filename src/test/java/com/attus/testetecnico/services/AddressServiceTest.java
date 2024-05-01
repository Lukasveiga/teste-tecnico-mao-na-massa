package com.attus.testetecnico.services;

import com.attus.testetecnico.ServiceTestConfiguration;
import com.attus.testetecnico.entities.Address;
import com.attus.testetecnico.entities.Person;
import com.attus.testetecnico.repositories.AddressRepository;
import com.attus.testetecnico.services.exceptions.EntityNotFoundException;
import com.attus.testetecnico.services.exceptions.MainAddressException;
import com.attus.testetecnico.utils.GenerateTestEntities;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;

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

    @Test
    void testCreateNewAddressErrorMainAddressAlreadyExists() {
        // Given
        when(this.personService.findOne(anyLong()))
                .thenReturn(personTest);

        when(this.addressRepository.findAllByPersonId(anyLong()))
                .thenReturn(List.of(addressTest));

        // When - Then
        Assertions.assertThatThrownBy(() -> this.addressService.create(personTest.getId(), addressTest))
                .isInstanceOf(MainAddressException.class)
                .hasMessage("Person with id %d already have a main address".formatted(personTest.getId()));
        verify(this.addressRepository, times(0)).save(any(Address.class));
    }

    @Test
    void testUpdateAddressSuccess() {
        // Given
        personTest.addAddresses(addressTest);

        when(this.personService.findOne(anyLong()))
                .thenReturn(personTest);

        when(this.addressRepository.save(any(Address.class)))
                .thenReturn(addressTest);

        // When
        var result = this.addressService.update(personTest.getId(), addressTest.getId(), addressTest);

        // Then
        Assertions.assertThat(result).usingRecursiveAssertion().isEqualTo(addressTest);
        verify(this.addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    void testUpdateAddressErrorEntityPersonNotFoundException() {
        // Given
        when(this.personService.findOne(anyLong()))
                .thenThrow(new EntityNotFoundException("Person with id %d was not found".formatted(personTest.getId())));

        // When - Then
        Assertions.assertThatThrownBy(() -> this.addressService.update(personTest.getId(), addressTest.getId(), addressTest))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Person with id %d was not found".formatted(personTest.getId()));
        verify(this.addressRepository, times(0)).save(any(Address.class));
    }

    @Test
    void testUpdateAddressErrorEntityAddressNotFoundException() {
        // Given
        when(this.personService.findOne(anyLong()))
                .thenReturn(personTest);

        // When - Then
        Assertions.assertThatThrownBy(() -> this.addressService.update(personTest.getId(), addressTest.getId(), addressTest))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Address with id %d was not found".formatted(addressTest.getId()));
        verify(this.addressRepository, times(0)).save(any(Address.class));
    }

    @Test
    void testUpdateAddressErrorMainAddressAlreadyExists() {
        // Given
        var addressTestTwo = GenerateTestEntities.generateAddress(2L, "Street Test", "555-556", 5,"City Test",
                "State Test", true, personTest);

        personTest.addAddresses(addressTest);
        personTest.addAddresses(addressTestTwo);

        when(this.personService.findOne(anyLong()))
                .thenReturn(personTest);

        when(this.addressRepository.findAllByPersonId(anyLong()))
                .thenReturn(List.of(addressTest, addressTestTwo));

        // When - Then
        Assertions.assertThatThrownBy(() -> this.addressService.update(personTest.getId(), addressTestTwo.getId(), addressTest))
                .isInstanceOf(MainAddressException.class)
                .hasMessage("Person with id %d already have a main address".formatted(personTest.getId()));
        verify(this.addressRepository, times(0)).save(any(Address.class));
    }

    @Test
    void testFindAllSuccess() {
        // Given
        when(this.addressRepository.findAllByPersonId(anyLong()))
                .thenReturn(List.of(addressTest));

        // When
        var result = this.addressService.findAll(personTest.getId());

        // Then
        Assertions.assertThat(result).hasSize(1);
        Assertions.assertThat(result.get(0)).usingRecursiveAssertion().isEqualTo(addressTest);
    }

    @Test
    void testFindAllPageableSuccess() {
        // Given
        when(this.addressRepository.findAllByPersonId(any(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(addressTest)));

        // When
        var result = this.addressService.findAllPageable(personTest.getId(), 0);

        // Then
        Assertions.assertThat(result).hasSize(1);
        Assertions.assertThat(result.get(0)).usingRecursiveAssertion().isEqualTo(addressTest);
    }
}