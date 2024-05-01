package com.attus.testetecnico.integration;

import com.attus.testetecnico.IntegrationTestContainerConfiguration;
import com.attus.testetecnico.controllers.person.dto.PersonRequestBody;
import com.attus.testetecnico.entities.Address;
import com.attus.testetecnico.entities.Person;
import com.attus.testetecnico.repositories.PersonRepository;
import com.attus.testetecnico.utils.GenerateTestEntities;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PersonControllerIntegrationTest extends IntegrationTestContainerConfiguration {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PersonRepository personRepository;

    List<Person> personTestList;

    Address addressTest;

    DateTimeFormatter formatter;

    @Value(value = "${api.endpoint.base-url}/person")
    String baseUrl;

    @BeforeEach
    void setUp() {

        personTestList = List.of(
                GenerateTestEntities.generatePerson(1L, "Test 1", LocalDate.of(1998, 5, 15)),
                GenerateTestEntities.generatePerson(2L, "Test 1", LocalDate.of(1998, 5, 15)),
                GenerateTestEntities.generatePerson(3L, "Test 1", LocalDate.of(1998, 5, 15)),
                GenerateTestEntities.generatePerson(4L, "Test 1", LocalDate.of(1998, 5, 15)),
                GenerateTestEntities.generatePerson(5L, "Test 1", LocalDate.of(1998, 5, 15)),
                GenerateTestEntities.generatePerson(6L, "Test 1", LocalDate.of(1998, 5, 15))
        );

        addressTest = GenerateTestEntities.generateAddress(1L, "Street Test", "555-556", 5,
                "City Test", "State Test",true, personTestList.get(0));

        personTestList.get(0).addAddresses(addressTest);

        personRepository.saveAllAndFlush(personTestList);

        formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }

    @Test
    void testCreateNewPersonSuccess() throws Exception {
        // Given
        var request = new PersonRequestBody("Test Create", LocalDate.of(1998, 5, 15));

        var requestJson = this.objectMapper.writeValueAsString(request);

        // When - Then
        this.mockMvc.perform(post(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Created person success"))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.fullName").value(request.fullName()))
                .andExpect(jsonPath("$.data.dateOfBirth").value(request.dateOfBirth().format(formatter)))
                .andExpect(jsonPath("$.data.mainAddress").isEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testCreateNewPersonErrorBadRequest() throws Exception {
        // Given
        var request = new PersonRequestBody(null, null);

        var requestJson = this.objectMapper.writeValueAsString(request);

        // When - Then
        this.mockMvc.perform(post(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("Provided arguments are invalid, see data for details"))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data.fullName").value("Cannot be null or empty"))
                .andExpect(jsonPath("$.data.dateOfBirth").value("Cannot be null or empty"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testCreateNewPersonErrorBadRequestInvalidDateOfBirthFormat() throws Exception {
        // Given
        JSONObject json = new JSONObject();
        json.put("fullName", "Test Full Name");
        json.put("dateOfBirth", "29-07-1985");
        var requestJson = json.toString();

        // When - Then
        this.mockMvc.perform(post(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("Provided arguments are invalid, see data for details"))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data").value("Invalid date format. Follow the following pattern: dd/MM/yyyy"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testUpdatePersonSuccess() throws Exception {
        // Given
        var request = new PersonRequestBody("Test Create", LocalDate.of(1998, 5, 15));

        var requestJson = this.objectMapper.writeValueAsString(request);

        // When - Then
        this.mockMvc.perform(put(baseUrl + "/" + personTestList.get(1).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Updated person success"))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data.id").value(personTestList.get(1).getId()))
                .andExpect(jsonPath("$.data.fullName").value(request.fullName()))
                .andExpect(jsonPath("$.data.dateOfBirth").value(request.dateOfBirth().format(formatter)))
                .andExpect(jsonPath("$.data.mainAddress").isEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testUpdatePersonErrorPersonNotFoundException() throws Exception {
        // Given
        var request = new PersonRequestBody("Test Create", LocalDate.of(1998, 5, 15));

        var requestJson = this.objectMapper.writeValueAsString(request);

        var invalidId = 999L;

        // When - Then
        this.mockMvc.perform(put(baseUrl + "/" + invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("Person with id %d was not found".formatted(invalidId)))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testUpdatePersonErrorBadRequest() throws Exception {
        // Given
        var request = new PersonRequestBody(null, null);

        var requestJson = this.objectMapper.writeValueAsString(request);

        // When - Then
        this.mockMvc.perform(put(baseUrl + "/" + personTestList.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("Provided arguments are invalid, see data for details"))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data.fullName").value("Cannot be null or empty"))
                .andExpect(jsonPath("$.data.dateOfBirth").value("Cannot be null or empty"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testUpdatePersonErrorBadRequestInvalidDateOfBirthFormat() throws Exception {
        // Given
        JSONObject json = new JSONObject();
        json.put("fullName", "Test Full Name");
        json.put("dateOfBirth", "29-07-1985");
        var requestJson = json.toString();

        // When - Then
        this.mockMvc.perform(put(baseUrl + "/" + personTestList.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("Provided arguments are invalid, see data for details"))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data").value("Invalid date format. Follow the following pattern: dd/MM/yyyy"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testFindOnePersonSuccess() throws Exception {
        // When - Then
        this.mockMvc.perform(get(baseUrl + "/" + personTestList.get(1).getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Find person success"))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data.id").value(personTestList.get(1).getId()))
                .andExpect(jsonPath("$.data.fullName").value(personTestList.get(1).getFullName()))
                .andExpect(jsonPath("$.data.dateOfBirth").value(personTestList.get(1).getDateOfBirth().format(formatter)))
                .andExpect(jsonPath("$.data.mainAddress").isEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testFindOnePersonShowingMainAddressIfExistsSuccess() throws Exception {
        // When - Then
        this.mockMvc.perform(get(baseUrl + "/" + personTestList.get(0).getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Find person success"))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data.id").value(personTestList.get(0).getId()))
                .andExpect(jsonPath("$.data.fullName").value(personTestList.get(0).getFullName()))
                .andExpect(jsonPath("$.data.dateOfBirth").value(personTestList.get(0).getDateOfBirth().format(formatter)))
                .andExpect(jsonPath("$.data.mainAddress.id").value(addressTest.getId()))
                .andExpect(jsonPath("$.data.mainAddress.street").value(addressTest.getStreet()))
                .andExpect(jsonPath("$.data.mainAddress.zipCode").value(addressTest.getZipCode()))
                .andExpect(jsonPath("$.data.mainAddress.city").value(addressTest.getCity()))
                .andExpect(jsonPath("$.data.mainAddress.state").value(addressTest.getState()))
                .andExpect(jsonPath("$.data.mainAddress.main").value(addressTest.isMain()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testFindOnePersonErrorPersonNotFoundException() throws Exception {
        // Given
        var invalidId = 999L;

        // When - Then
        this.mockMvc.perform(get(baseUrl + "/" + invalidId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("Person with id %d was not found".formatted(invalidId)))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testFindAllPersonsSuccess() throws Exception {
        this.mockMvc.perform(get(baseUrl).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Find all persons success"))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data.length()").value(7))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testFindAllPersonsPageableSuccess() throws Exception {
        // Given
        var page = 0;

        // When - Then
        this.mockMvc.perform(get(baseUrl + "?page=" + page).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Find all persons success"))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data.length()").value(5))
                .andDo(MockMvcResultHandlers.print());
    }

}
