package com.attus.testetecnico.controllers.address;

import com.attus.testetecnico.ControllerTestConfiguration;
import com.attus.testetecnico.controllers.address.dto.AddressRequestBody;
import com.attus.testetecnico.entities.Address;
import com.attus.testetecnico.entities.Person;
import com.attus.testetecnico.services.AddressService;
import com.attus.testetecnico.services.PersonService;
import com.attus.testetecnico.services.exceptions.EntityNotFoundException;
import com.attus.testetecnico.services.exceptions.MainAddressException;
import com.attus.testetecnico.utils.GenerateTestEntities;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.time.LocalDate;
import java.util.List;

import static com.attus.testetecnico.utils.GenerateTestEntities.generatePerson;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AddressControllerTest extends ControllerTestConfiguration {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AddressService addressService;

    @MockBean
    PersonService personService;

    @Autowired
    ObjectMapper objectMapper;

    @Value(value = "${api.endpoint.base-url}/address")
    String baseUrl;

    Address addressTest;

    Person personTest;

    @BeforeEach
    void setUp() {
        personTest = generatePerson(1L, "Subject 89P13", LocalDate.of(1976, 7, 1));
        addressTest = GenerateTestEntities.generateAddress(1L, "Street Test", "555-556", 5,
                "City Test", "State Test",true, personTest);
    }

    @Test
    void testCreateNewAddressSuccess() throws Exception {
        // Given
        var request = new AddressRequestBody(addressTest.getStreet(), addressTest.getZipCode(), addressTest.getNumber(),
                addressTest.getCity(), addressTest.getState(), addressTest.isMain());

        var requestJson = this.objectMapper.writeValueAsString(request);

        when(this.addressService.create(anyLong(), any(Address.class)))
                .thenReturn(addressTest);

        // When - Then
        this.mockMvc.perform(post(baseUrl + "/person/" + personTest.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Created address success"))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data.id").value(addressTest.getId()))
                .andExpect(jsonPath("$.data.street").value(addressTest.getStreet()))
                .andExpect(jsonPath("$.data.zipCode").value(addressTest.getZipCode()))
                .andExpect(jsonPath("$.data.number").value(addressTest.getNumber()))
                .andExpect(jsonPath("$.data.city").value(addressTest.getCity()))
                .andExpect(jsonPath("$.data.state").value(addressTest.getState()))
                .andExpect(jsonPath("$.data.main").value(addressTest.isMain()))
                .andExpect(jsonPath("$.data.personId").value(addressTest.getPerson().getId()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testCreateNewAddressErrorPersonNotFoundException() throws Exception {
        // Given
        var request = new AddressRequestBody(addressTest.getStreet(), addressTest.getZipCode(), addressTest.getNumber(),
                addressTest.getCity(), addressTest.getState(), addressTest.isMain());

        var requestJson = this.objectMapper.writeValueAsString(request);

        when(this.addressService.create(anyLong(), any(Address.class)))
                .thenThrow(new EntityNotFoundException("Person with id %d was not found".formatted(personTest.getId())));

        // When - Then
        this.mockMvc.perform(post(baseUrl + "/person/" + personTest.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("Person with id %d was not found".formatted(personTest.getId())))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testCreateNewAddressErrorErrorBadRequestInvalidFields() throws Exception {
        // Given
        var errorCase = new AddressRequestBody("", "", -1, "", "", true);

        var requestJson = this.objectMapper.writeValueAsString(errorCase);

        when(this.addressService.create(anyLong(), any(Address.class)))
                        .thenReturn(addressTest);

        // When - Then
        this.mockMvc.perform(post(baseUrl + "/person/" + personTest.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("Provided arguments are invalid, see data for details"))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data.street").value("Cannot be null or empty"))
                .andExpect(jsonPath("$.data.zipCode").value("Cannot be null or empty"))
                .andExpect(jsonPath("$.data.number").value("Cannot be less than 1"))
                .andExpect(jsonPath("$.data.city").value("Cannot be null or empty"))
                .andExpect(jsonPath("$.data.state").value("Cannot be null or empty"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testCreateNewAddressErrorMainAddressException() throws Exception {
        // Given
        var request = new AddressRequestBody(addressTest.getStreet(), addressTest.getZipCode(), addressTest.getNumber(),
                addressTest.getCity(), addressTest.getState(), addressTest.isMain());

        var requestJson = this.objectMapper.writeValueAsString(request);

        when(this.addressService.create(anyLong(), any(Address.class)))
                .thenThrow(new MainAddressException("Person with id %d already have a main address".formatted(personTest.getId())));

        // When - Then
        this.mockMvc.perform(post(baseUrl + "/person/" + personTest.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("Person with id %d already have a main address".formatted(personTest.getId())))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testUpdateAddressSuccess() throws Exception {
        // Given
        var request = new AddressRequestBody(addressTest.getStreet(), addressTest.getZipCode(), addressTest.getNumber(),
                addressTest.getCity(), addressTest.getState(), addressTest.isMain());

        var requestJson = this.objectMapper.writeValueAsString(request);

        when(this.addressService.update(anyLong(), anyLong(), any(Address.class)))
                .thenReturn(addressTest);

        // When - Then
        this.mockMvc.perform(put(baseUrl + "/" + addressTest.getId() + "/person/" + personTest.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Updated address success"))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data.id").value(addressTest.getId()))
                .andExpect(jsonPath("$.data.street").value(addressTest.getStreet()))
                .andExpect(jsonPath("$.data.zipCode").value(addressTest.getZipCode()))
                .andExpect(jsonPath("$.data.number").value(addressTest.getNumber()))
                .andExpect(jsonPath("$.data.city").value(addressTest.getCity()))
                .andExpect(jsonPath("$.data.state").value(addressTest.getState()))
                .andExpect(jsonPath("$.data.main").value(addressTest.isMain()))
                .andExpect(jsonPath("$.data.personId").value(addressTest.getPerson().getId()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testUpdateAddressErrorPersonNotFoundException() throws Exception {
        // Given
        var request = new AddressRequestBody(addressTest.getStreet(), addressTest.getZipCode(), addressTest.getNumber(),
                addressTest.getCity(), addressTest.getState(), addressTest.isMain());

        var requestJson = this.objectMapper.writeValueAsString(request);

        when(this.addressService.update(anyLong(), anyLong(), any(Address.class)))
                .thenThrow(new EntityNotFoundException("Person with id %d was not found".formatted(personTest.getId())));

        // When - Then
        this.mockMvc.perform(put(baseUrl + "/" + addressTest.getId() + "/person/" + personTest.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("Person with id %d was not found".formatted(personTest.getId())))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testUpdateAddressErrorAddressNotFoundException() throws Exception {
        // Given
        var request = new AddressRequestBody(addressTest.getStreet(), addressTest.getZipCode(), addressTest.getNumber(),
                addressTest.getCity(), addressTest.getState(), addressTest.isMain());

        var requestJson = this.objectMapper.writeValueAsString(request);

        when(this.addressService.update(anyLong(), anyLong(), any(Address.class)))
                .thenThrow(new EntityNotFoundException("Address with id %d was not found".formatted(addressTest.getId())));

        // When - Then
        this.mockMvc.perform(put(baseUrl + "/" + addressTest.getId() + "/person/" + personTest.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("Address with id %d was not found".formatted(addressTest.getId())))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testUpdateAddressErrorErrorBadRequestInvalidFields() throws Exception {
        // Given
        var errorCase = new AddressRequestBody("", "", -1, "", "", true);

        var requestJson = this.objectMapper.writeValueAsString(errorCase);

        when(this.addressService.update(anyLong(), anyLong(), any(Address.class)))
                .thenReturn(addressTest);

        // When - Then
        this.mockMvc.perform(put(baseUrl + "/" + addressTest.getId() + "/person/" + personTest.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("Provided arguments are invalid, see data for details"))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data.street").value("Cannot be null or empty"))
                .andExpect(jsonPath("$.data.zipCode").value("Cannot be null or empty"))
                .andExpect(jsonPath("$.data.number").value("Cannot be less than 1"))
                .andExpect(jsonPath("$.data.city").value("Cannot be null or empty"))
                .andExpect(jsonPath("$.data.state").value("Cannot be null or empty"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testUpdateAddressErrorMainAddressException() throws Exception {
        // Given
        var request = new AddressRequestBody(addressTest.getStreet(), addressTest.getZipCode(), addressTest.getNumber(),
                addressTest.getCity(), addressTest.getState(), addressTest.isMain());

        var requestJson = this.objectMapper.writeValueAsString(request);

        when(this.addressService.update(anyLong(), anyLong(), any(Address.class)))
                .thenThrow(new MainAddressException("Person with id %d already have a main address".formatted(personTest.getId())));

        // When - Then
        this.mockMvc.perform(put(baseUrl + "/" + addressTest.getId() + "/person/" + personTest.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("Person with id %d already have a main address".formatted(personTest.getId())))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testFindAllAddressesSuccess() throws Exception {
        // Given
        when(this.addressService.findAll(anyLong()))
                .thenReturn(List.of(addressTest));

        // When - Then
        this.mockMvc.perform(get(baseUrl + "/person/" + personTest.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Find all addresses success"))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(addressTest.getId()))
                .andExpect(jsonPath("$.data[0].street").value(addressTest.getStreet()))
                .andExpect(jsonPath("$.data[0].zipCode").value(addressTest.getZipCode()))
                .andExpect(jsonPath("$.data[0].number").value(addressTest.getNumber()))
                .andExpect(jsonPath("$.data[0].city").value(addressTest.getCity()))
                .andExpect(jsonPath("$.data[0].state").value(addressTest.getState()))
                .andExpect(jsonPath("$.data[0].main").value(addressTest.isMain()))
                .andExpect(jsonPath("$.data[0].personId").value(addressTest.getPerson().getId()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testFindAllAddressesPageableSuccess() throws Exception {
        // Given
        when(this.addressService.findAllPageable(anyLong(), anyInt()))
                .thenReturn(List.of(addressTest));

        // When - Then
        this.mockMvc.perform(get(baseUrl + "/person/" + personTest.getId() + "?page=" + 0)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Find all addresses success"))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(addressTest.getId()))
                .andExpect(jsonPath("$.data[0].street").value(addressTest.getStreet()))
                .andExpect(jsonPath("$.data[0].zipCode").value(addressTest.getZipCode()))
                .andExpect(jsonPath("$.data[0].number").value(addressTest.getNumber()))
                .andExpect(jsonPath("$.data[0].city").value(addressTest.getCity()))
                .andExpect(jsonPath("$.data[0].state").value(addressTest.getState()))
                .andExpect(jsonPath("$.data[0].main").value(addressTest.isMain()))
                .andExpect(jsonPath("$.data[0].personId").value(addressTest.getPerson().getId()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testFindOneAddressesSuccess() throws Exception {
        // Given
        personTest.addAddresses(addressTest);

        when(this.personService.findOne(anyLong()))
                .thenReturn(personTest);

        when(this.addressService.findOne(anyLong(), anyLong()))
                .thenReturn(addressTest);

        // When - Then
        this.mockMvc.perform(get(baseUrl + "/"  + addressTest.getId() + "/person/" + personTest.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Find one address success"))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data.id").value(addressTest.getId()))
                .andExpect(jsonPath("$.data.street").value(addressTest.getStreet()))
                .andExpect(jsonPath("$.data.zipCode").value(addressTest.getZipCode()))
                .andExpect(jsonPath("$.data.number").value(addressTest.getNumber()))
                .andExpect(jsonPath("$.data.city").value(addressTest.getCity()))
                .andExpect(jsonPath("$.data.state").value(addressTest.getState()))
                .andExpect(jsonPath("$.data.main").value(addressTest.isMain()))
                .andExpect(jsonPath("$.data.personId").value(addressTest.getPerson().getId()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testFindOneAddressesErrorPersonNotFoundException() throws Exception {
        // Given
        when(this.addressService.findOne(anyLong(), anyLong()))
                .thenThrow(new EntityNotFoundException("Person with id %d was not found".formatted(personTest.getId())));

        // When - Then
        this.mockMvc.perform(get(baseUrl + "/"  + addressTest.getId() + "/person/" + personTest.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("Person with id %d was not found".formatted(personTest.getId())))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testFindOneAddressesErrorAddressNotFoundException() throws Exception {
        // Given
        when(this.addressService.findOne(anyLong(), anyLong()))
                .thenThrow(new EntityNotFoundException("Address with id %d was not found".formatted(addressTest.getId())));

        // When - Then
        this.mockMvc.perform(get(baseUrl + "/"  + addressTest.getId() + "/person/" + personTest.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("Address with id %d was not found".formatted(addressTest.getId())))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testInternalServerError() throws Exception {
        // Given
        when(this.addressService.findAllPageable(anyLong(), anyInt()))
                .thenThrow(new RuntimeException("Internal Server Error"));

        // When - Then
        this.mockMvc.perform(get(baseUrl + "/person/" + personTest.getId() + "?page=" + 0)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("Internal Server Error"))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print());
    }
}