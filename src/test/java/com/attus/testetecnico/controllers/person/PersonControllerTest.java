package com.attus.testetecnico.controllers.person;

import com.attus.testetecnico.ControllerTestConfiguration;
import com.attus.testetecnico.controllers.person.dto.PersonRequestBody;
import com.attus.testetecnico.entities.Person;
import com.attus.testetecnico.services.PersonService;
import com.attus.testetecnico.services.exceptions.EntityNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.attus.testetecnico.utils.GenerateTestEntities.generateAddress;
import static com.attus.testetecnico.utils.GenerateTestEntities.generatePerson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class PersonControllerTest extends ControllerTestConfiguration {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PersonService personService;

    @Autowired
    ObjectMapper objectMapper;

    @Value(value = "${api.endpoint.base-url}/person")
    String baseUrl;

    Person personTest;

    DateTimeFormatter formatter;

    @BeforeEach
    void setUp() {
        personTest = generatePerson(1L, "Subject 89P13", LocalDate.of(1976, 7, 1));
        formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }

    @Test
    void testCreateNewPersonSuccess() throws Exception {
        // Given
        var request = new PersonRequestBody(personTest.getFullName(),
                personTest.getDateOfBirth());

        var requestJson = this.objectMapper.writeValueAsString(request);

        when(this.personService.create(any(Person.class)))
                .thenReturn(personTest);

        // When - Then
        this.mockMvc.perform(post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Created person success"))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data.id").value(personTest.getId()))
                .andExpect(jsonPath("$.data.fullName").value(personTest.getFullName()))
                .andExpect(jsonPath("$.data.dateOfBirth").value(personTest.getDateOfBirth().format(formatter)))
                .andExpect(jsonPath("$.data.mainAddress").isEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testCreateNewPersonErrorBadRequest() throws Exception {
        // Given
        var request = new PersonRequestBody(null,
                null);

        var requestJson = this.objectMapper.writeValueAsString(request);

        when(this.personService.create(any(Person.class)))
                .thenReturn(personTest);

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
        json.put("fullName", personTest.getFullName());
        json.put("dateOfBirth", "29-07-1985");
        var requestJson = json.toString();

        when(this.personService.create(any(Person.class)))
                .thenReturn(personTest);

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
        var request = new PersonRequestBody(personTest.getFullName(),
                personTest.getDateOfBirth());

        var requestJson = this.objectMapper.writeValueAsString(request);

        when(this.personService.update(anyLong(), any(Person.class)))
                .thenReturn(personTest);

        // When - Then
        this.mockMvc.perform(put(baseUrl + "/" + personTest.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Updated person success"))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data.id").value(personTest.getId()))
                .andExpect(jsonPath("$.data.fullName").value(personTest.getFullName()))
                .andExpect(jsonPath("$.data.dateOfBirth").value(personTest.getDateOfBirth().format(formatter)))
                .andExpect(jsonPath("$.data.mainAddress").isEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testUpdatePersonErrorPersonNotFoundException() throws Exception {
        // Given
        var request = new PersonRequestBody(personTest.getFullName(),
                personTest.getDateOfBirth());

        var requestJson = this.objectMapper.writeValueAsString(request);

        when(this.personService.update(anyLong(), any(Person.class)))
                .thenThrow(new EntityNotFoundException("Person with id %d was not found".formatted(personTest.getId())));

        // When - Then
        this.mockMvc.perform(put(baseUrl + "/" + personTest.getId())
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
    void testUpdatePersonErrorBadRequest() throws Exception {
        // Given
        var request = new PersonRequestBody(null,
                null);

        var requestJson = this.objectMapper.writeValueAsString(request);

        when(this.personService.update(anyLong(), any(Person.class)))
                .thenReturn(personTest);

        // When - Then
        this.mockMvc.perform(put(baseUrl + "/" + personTest.getId())
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
        json.put("fullName", personTest.getFullName());
        json.put("dateOfBirth", "29-07-1985");
        var requestJson = json.toString();

        when(this.personService.update(anyLong(), any(Person.class)))
                .thenReturn(personTest);

        // When - Then
        this.mockMvc.perform(put(baseUrl + "/" + personTest.getId())
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
        // Given
        when(this.personService.findOne(anyLong()))
                .thenReturn(personTest);

        // When - Then
        this.mockMvc.perform(get(baseUrl + "/" + personTest.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Find person success"))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data.id").value(personTest.getId()))
                .andExpect(jsonPath("$.data.fullName").value(personTest.getFullName()))
                .andExpect(jsonPath("$.data.dateOfBirth").value(personTest.getDateOfBirth().format(formatter)))
                .andExpect(jsonPath("$.data.mainAddress").isEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testFindOnePersonShowingMainAddressIfExistsSuccess() throws Exception {
        // Given
        var address = generateAddress(1L, "Street 1", "5588-966",
                5, "City 1", "State 1", true, personTest);

        personTest.addAddresses(address);

        when(this.personService.findOne(anyLong()))
                .thenReturn(personTest);

        // When - Then
        this.mockMvc.perform(get(baseUrl + "/" + personTest.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Find person success"))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data.id").value(personTest.getId()))
                .andExpect(jsonPath("$.data.fullName").value(personTest.getFullName()))
                .andExpect(jsonPath("$.data.dateOfBirth").value(personTest.getDateOfBirth().format(formatter)))
                .andExpect(jsonPath("$.data.mainAddress.id").value(address.getId()))
                .andExpect(jsonPath("$.data.mainAddress.street").value(address.getStreet()))
                .andExpect(jsonPath("$.data.mainAddress.zipCode").value(address.getZipCode()))
                .andExpect(jsonPath("$.data.mainAddress.city").value(address.getCity()))
                .andExpect(jsonPath("$.data.mainAddress.state").value(address.getState()))
                .andExpect(jsonPath("$.data.mainAddress.main").value(address.isMain()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testFindOnePersonErrorPersonNotFoundException() throws Exception {
        // Given
        when(this.personService.findOne(anyLong()))
                .thenThrow(new EntityNotFoundException("Person with id %d was not found".formatted(personTest.getId())));

        // When - Then
        this.mockMvc.perform(get(baseUrl + "/" + personTest.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("Person with id %d was not found".formatted(personTest.getId())))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testFindAllPersonsSuccess() throws Exception {
        // Given
        when(this.personService.findAll())
                .thenReturn(List.of(personTest));

        // When - Then
        this.mockMvc.perform(get(baseUrl).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Find all persons success"))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(personTest.getId()))
                .andExpect(jsonPath("$.data[0].fullName").value(personTest.getFullName()))
                .andExpect(jsonPath("$.data[0].dateOfBirth").value(personTest.getDateOfBirth().format(formatter)))
                .andExpect(jsonPath("$.data[0].mainAddress").isEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testFindAllPersonsPageableSuccess() throws Exception {
        // Given
        var page = 0;

        when(this.personService.findAllPageable(page))
                .thenReturn(List.of(personTest));

        // When - Then
        this.mockMvc.perform(get(baseUrl + "?page=" + page).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Find all persons success"))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(personTest.getId()))
                .andExpect(jsonPath("$.data[0].fullName").value(personTest.getFullName()))
                .andExpect(jsonPath("$.data[0].dateOfBirth").value(personTest.getDateOfBirth().format(formatter)))
                .andExpect(jsonPath("$.data[0].mainAddress").isEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testInternalServerError() throws Exception {
        // Given
        when(this.personService.findOne(anyLong()))
                .thenThrow(new RuntimeException("Internal Server Error"));

        // When - Then
        this.mockMvc.perform(get(baseUrl + "/" + personTest.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("Internal Server Error"))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testNotFoundEndpointException() throws Exception {
        // Given
        var invalidUrlEndpoint = "/invalid/endpoint";

        // When - Then
        this.mockMvc.perform(get(invalidUrlEndpoint).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("API endpoint not found"))
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

}