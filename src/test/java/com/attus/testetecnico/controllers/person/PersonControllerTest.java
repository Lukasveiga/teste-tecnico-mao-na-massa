package com.attus.testetecnico.controllers.person;

import com.attus.testetecnico.ControllerTestConfiguration;
import com.attus.testetecnico.controllers.person.dto.PersonRequestBody;
import com.attus.testetecnico.entities.Address;
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

import static com.attus.testetecnico.utils.GenerateTestEntities.generatePerson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    List<Address> addressesTest;

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
    void testCreateNewPersonErrorBadRequestEmptyFullName() throws Exception {
        // Given
        var request = new PersonRequestBody("",
                personTest.getDateOfBirth());

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
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testCreateNewPersonErrorBadRequestEmptyDateOfBirth() throws Exception {
        // Given
        var request = new PersonRequestBody(personTest.getFullName(),
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
    void testUpdatePersonErrorBadRequestEmptyFullName() throws Exception {
        // Given
        var request = new PersonRequestBody("",
                personTest.getDateOfBirth());

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
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testUpdatePersonErrorBadRequestEmptyDateOfBirth() throws Exception {
        // Given
        var request = new PersonRequestBody(personTest.getFullName(),
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

}