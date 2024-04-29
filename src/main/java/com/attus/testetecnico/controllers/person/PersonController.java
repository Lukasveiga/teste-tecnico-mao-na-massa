package com.attus.testetecnico.controllers.person;

import com.attus.testetecnico.controllers.person.converter.EntityToResponseBodyConverter;
import com.attus.testetecnico.controllers.person.converter.RequestBodyToEntityConverter;
import com.attus.testetecnico.controllers.person.dto.PersonRequestBody;
import com.attus.testetecnico.services.PersonService;
import com.attus.testetecnico.system.HttpResponseResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "${api.endpoint.base-url}/person")
public class PersonController {

    private final PersonService personService;

    private final RequestBodyToEntityConverter requestBodyToEntityConverter;

    private final EntityToResponseBodyConverter entityToResponseBodyConverter;

    public PersonController(PersonService personService, RequestBodyToEntityConverter requestBodyToEntityConverter, EntityToResponseBodyConverter entityToResponseBodyConverter) {
        this.personService = personService;
        this.requestBodyToEntityConverter = requestBodyToEntityConverter;
        this.entityToResponseBodyConverter = entityToResponseBodyConverter;
    }

    @PostMapping
    public ResponseEntity<HttpResponseResult> createNewPerson(@RequestBody PersonRequestBody requestBody) {
        var newPerson = this.personService.create(this.requestBodyToEntityConverter.convert(requestBody));
        var responsePerson = this.entityToResponseBodyConverter.convert(this.personService.create(newPerson));
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new HttpResponseResult(
                        true,
                        "Created person success",
                        LocalDateTime.now(),
                        responsePerson
                )
        );
    }
}
