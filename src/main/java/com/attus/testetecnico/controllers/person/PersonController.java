package com.attus.testetecnico.controllers.person;

import com.attus.testetecnico.controllers.person.converter.PersonEntityToResponseBodyConverter;
import com.attus.testetecnico.controllers.person.converter.PersonRequestBodyToEntityConverter;
import com.attus.testetecnico.controllers.person.dto.PersonRequestBody;
import com.attus.testetecnico.services.PersonService;
import com.attus.testetecnico.system.HttpResponseResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Objects;

@RestController
@RequestMapping(value = "${api.endpoint.base-url}/person")
public class PersonController {

    private final PersonService personService;

    private final PersonRequestBodyToEntityConverter personRequestBodyToEntityConverter;

    private final PersonEntityToResponseBodyConverter personEntityToResponseBodyConverter;

    public PersonController(PersonService personService, PersonRequestBodyToEntityConverter personRequestBodyToEntityConverter,
                            PersonEntityToResponseBodyConverter personEntityToResponseBodyConverter) {
        this.personService = personService;
        this.personRequestBodyToEntityConverter = personRequestBodyToEntityConverter;
        this.personEntityToResponseBodyConverter = personEntityToResponseBodyConverter;
    }

    @PostMapping
    public ResponseEntity<HttpResponseResult> createNewPerson(@RequestBody @Validated PersonRequestBody requestBody) {
        var newPerson = this.personService.create(this.personRequestBodyToEntityConverter.convert(requestBody));
        var responsePerson = this.personEntityToResponseBodyConverter.convert(this.personService.create(newPerson));
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new HttpResponseResult(
                        true,
                        "Created person success",
                        LocalDateTime.now(),
                        responsePerson
                )
        );
    }

    @PutMapping("/{personId}")
    public ResponseEntity<HttpResponseResult> updatePerson(@PathVariable("personId") Long personId, @RequestBody @Validated PersonRequestBody requestBody) {
        var person = this.personRequestBodyToEntityConverter.convert(requestBody);
        var updatedPerson = this.personService.update(personId, Objects.requireNonNull(person));

        var responsePerson = this.personEntityToResponseBodyConverter.convert(updatedPerson);
        return ResponseEntity.status(HttpStatus.OK).body(
                new HttpResponseResult(
                        true,
                        "Updated person success",
                        LocalDateTime.now(),
                        responsePerson
                )
        );
    }

    @GetMapping
    public ResponseEntity<HttpResponseResult> findAllPersons(@RequestParam(name = "page", required = false) Integer page) {
        var personList = this.personService.findAll();

        if(page != null) {
            personList = this.personService.findAllPageable(page);
        }

        var responsePerson = personList.stream().map(this.personEntityToResponseBodyConverter::convert).toList();
        return ResponseEntity.status(HttpStatus.OK).body(
                new HttpResponseResult(
                true,
                "Find all persons success",
                LocalDateTime.now(),
                responsePerson
        ));
    }

    @GetMapping("/{personId}")
    public ResponseEntity<HttpResponseResult> findOnePerson(@PathVariable("personId") Long personId) {
        var person = this.personService.findOne(personId);
        var responsePerson = this.personEntityToResponseBodyConverter.convert(person);
        return ResponseEntity.status(HttpStatus.OK).body(
                new HttpResponseResult(
                        true,
                        "Find person success",
                        LocalDateTime.now(),
                        responsePerson
                )
        );
    }
}
