package com.attus.testetecnico.controllers.person;

import com.attus.testetecnico.ControllerTestConfiguration;
import com.attus.testetecnico.controllers.person.dto.PersonRequestBody;
import com.attus.testetecnico.entities.Address;
import com.attus.testetecnico.entities.Person;
import com.attus.testetecnico.services.PersonService;
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

import static com.attus.testetecnico.utils.GenerateTestEntities.generateAddress;
import static com.attus.testetecnico.utils.GenerateTestEntities.generatePerson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    void testCreateNewPersonSuccess() throws Exception {
        // Given
        var request = new PersonRequestBody(personTest.getFullName(),
                personTest.getDateOfBirth(), personTest.getAddresses());

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
                .andExpect(jsonPath("$.data.dateOfBirth").value(personTest.getDateOfBirth().toString()))
                .andExpect(jsonPath("$.data.mainAddress.id").value(personTest.getMainAddress().getId()))
                .andExpect(jsonPath("$.data.mainAddress.street").value(personTest.getMainAddress().getStreet()))
                .andExpect(jsonPath("$.data.mainAddress.zipCode").value(personTest.getMainAddress().getZipCode()))
                .andExpect(jsonPath("$.data.mainAddress.number").value(personTest.getMainAddress().getNumber()))
                .andExpect(jsonPath("$.data.mainAddress.city").value(personTest.getMainAddress().getCity()))
                .andExpect(jsonPath("$.data.mainAddress.state").value(personTest.getMainAddress().getState()))
                .andExpect(jsonPath("$.data.mainAddress.main").value(personTest.getMainAddress().isMain()))
                .andDo(MockMvcResultHandlers.print());
    }

}