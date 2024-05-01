package com.attus.testetecnico.integration;

import com.attus.testetecnico.IntegrationTestContainerConfiguration;
import com.attus.testetecnico.controllers.address.dto.AddressRequestBody;
import com.attus.testetecnico.entities.Address;
import com.attus.testetecnico.entities.Person;
import com.attus.testetecnico.repositories.AddressRepository;
import com.attus.testetecnico.repositories.PersonRepository;
import com.attus.testetecnico.utils.GenerateTestEntities;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AddressControllerIntegrationTest extends IntegrationTestContainerConfiguration {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    PersonRepository personRepository;

    List<Address> addressTestList;

    Person personTest;

    @Value(value = "${api.endpoint.base-url}/address")
    String baseUrl;

    @BeforeEach
    void setUp() {
        personTest = GenerateTestEntities
                .generatePerson(1L, "Test 1", LocalDate.of(1998, 5, 15));

        addressTestList = List.of(
                GenerateTestEntities.generateAddress(1L, "Street Test", "555-556", 5,
                        "City Test", "State Test",true, personTest),
                GenerateTestEntities.generateAddress(2L, "Street Test", "555-556", 5,
                        "City Test", "State Test",false, personTest),
                GenerateTestEntities.generateAddress(3L, "Street Test", "555-556", 5,
                        "City Test", "State Test",false, personTest),
                GenerateTestEntities.generateAddress(4L, "Street Test", "555-556", 5,
                        "City Test", "State Test",false, personTest),
                GenerateTestEntities.generateAddress(5L, "Street Test", "555-556", 5,
                        "City Test", "State Test",false, personTest),
                GenerateTestEntities.generateAddress(6L, "Street Test", "555-556", 5,
                        "City Test", "State Test",false, personTest)
        );

        this.personRepository.saveAndFlush(personTest);
        this.addressRepository.saveAllAndFlush(addressTestList);
    }

    @Test
    void testCreateNewAddressSuccess() throws Exception {
        // Given
        var request = new AddressRequestBody("Street Test", "555-555", 5,
                "City Test", "State Test", false);

        var requestJson = this.objectMapper.writeValueAsString(request);

        // When - Then
        this.mockMvc.perform(post(baseUrl + "/person/" + personTest.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Created address success"))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.street").value(request.street()))
                .andExpect(jsonPath("$.data.zipCode").value(request.zipCode()))
                .andExpect(jsonPath("$.data.number").value(request.number()))
                .andExpect(jsonPath("$.data.city").value(request.city()))
                .andExpect(jsonPath("$.data.state").value(request.state()))
                .andExpect(jsonPath("$.data.main").value(request.main()))
                .andExpect(jsonPath("$.data.personId").value(personTest.getId()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testCreateNewAddressErrorPersonNotFoundException() throws Exception {
        // Given
        var request = new AddressRequestBody("Street Test", "555-555", 5,
                "City Test", "State Test", false);

        var requestJson = this.objectMapper.writeValueAsString(request);

        var invalidPersonId = 999L;

        // When - Then
        this.mockMvc.perform(post(baseUrl + "/person/" + invalidPersonId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("Person with id %d was not found".formatted(invalidPersonId)))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testCreateNewAddressErrorErrorBadRequestInvalidFields() throws Exception {
        // Given
        var errorCase = new AddressRequestBody(null, null, -1, null, null, true);

        var requestJson = this.objectMapper.writeValueAsString(errorCase);

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
        var request = new AddressRequestBody("Street Test", "555-555", 5,
                "City Test", "State Test", true);

        var requestJson = this.objectMapper.writeValueAsString(request);

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
        var request = new AddressRequestBody("Street Test", "555-555", 5,
                "City Test", "State Test", true);

        var requestJson = this.objectMapper.writeValueAsString(request);

        // When - Then
        this.mockMvc.perform(put(baseUrl + "/" + addressTestList.get(0).getId() + "/person/" + personTest.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Updated address success"))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data.id").value(addressTestList.get(0).getId()))
                .andExpect(jsonPath("$.data.street").value(request.street()))
                .andExpect(jsonPath("$.data.zipCode").value(request.zipCode()))
                .andExpect(jsonPath("$.data.number").value(request.number()))
                .andExpect(jsonPath("$.data.city").value(request.city()))
                .andExpect(jsonPath("$.data.state").value(request.state()))
                .andExpect(jsonPath("$.data.main").value(request.main()))
                .andExpect(jsonPath("$.data.personId").value(personTest.getId()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testUpdateAddressErrorPersonNotFoundException() throws Exception {
        // Given
        var request = new AddressRequestBody("Street Test", "555-555", 5,
                "City Test", "State Test", true);

        var requestJson = this.objectMapper.writeValueAsString(request);

        var invalidPersonId = 999L;

        // When - Then
        this.mockMvc.perform(put(baseUrl + "/" + addressTestList.get(0).getId() + "/person/" + invalidPersonId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("Person with id %d was not found".formatted(invalidPersonId)))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testUpdateAddressErrorAddressNotFoundException() throws Exception {
        // Given
        var request = new AddressRequestBody("Street Test", "555-555", 5,
                "City Test", "State Test", true);

        var requestJson = this.objectMapper.writeValueAsString(request);

        var invalidAddressId = 999L;

        // When - Then
        this.mockMvc.perform(put(baseUrl + "/" + invalidAddressId + "/person/" + personTest.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("Address with id %d was not found".formatted(invalidAddressId)))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testUpdateAddressErrorErrorBadRequestInvalidFields() throws Exception {
        // Given
        var errorCase = new AddressRequestBody(null, null, -1, null, null, true);

        var requestJson = this.objectMapper.writeValueAsString(errorCase);

        // When - Then
        this.mockMvc.perform(put(baseUrl + "/" + addressTestList.get(0).getId() + "/person/" + personTest.getId())
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
        var request = new AddressRequestBody("Street Test", "555-555", 5,
                "City Test", "State Test", true);

        var requestJson = this.objectMapper.writeValueAsString(request);

        // When - Then
        this.mockMvc.perform(put(baseUrl + "/" + addressTestList.get(1).getId() + "/person/" + personTest.getId())
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
        // When - Then
        this.mockMvc.perform(get(baseUrl + "/person/" + personTest.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Find all addresses success"))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data.length()").value(7))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testFindAllAddressesPageableSuccess() throws Exception {
        // When - Then
        this.mockMvc.perform(get(baseUrl + "/person/" + personTest.getId() + "?page=" + 0)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Find all addresses success"))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data.length()").value(5))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testFindOneAddressesSuccess() throws Exception {
        // When - Then
        this.mockMvc.perform(get(baseUrl + "/"  + addressTestList.get(0).getId() + "/person/" + personTest.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Find one address success"))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data.id").value(addressTestList.get(0).getId()))
                .andExpect(jsonPath("$.data.street").value(addressTestList.get(0).getStreet()))
                .andExpect(jsonPath("$.data.zipCode").value(addressTestList.get(0).getZipCode()))
                .andExpect(jsonPath("$.data.number").value(addressTestList.get(0).getNumber()))
                .andExpect(jsonPath("$.data.city").value(addressTestList.get(0).getCity()))
                .andExpect(jsonPath("$.data.state").value(addressTestList.get(0).getState()))
                .andExpect(jsonPath("$.data.main").value(addressTestList.get(0).isMain()))
                .andExpect(jsonPath("$.data.personId").value(personTest.getId()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testFindOneAddressesErrorPersonNotFoundException() throws Exception {
        // Given
        var invalidPersonId = 999L;

        // When - Then
        this.mockMvc.perform(get(baseUrl + "/"  + addressTestList.get(0).getId() + "/person/" + invalidPersonId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("Person with id %d was not found".formatted(invalidPersonId)))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testFindOneAddressesErrorAddressNotFoundException() throws Exception {
        // Given
        var invalidAddressId = 999L;

        // When - Then
        this.mockMvc.perform(get(baseUrl + "/"  + invalidAddressId + "/person/" + personTest.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("Address with id %d was not found".formatted(invalidAddressId)))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print());
    }


}
