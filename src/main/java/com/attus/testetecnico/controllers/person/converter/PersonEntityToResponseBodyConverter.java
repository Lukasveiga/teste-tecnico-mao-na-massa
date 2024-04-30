package com.attus.testetecnico.controllers.person.converter;

import com.attus.testetecnico.controllers.person.dto.PersonResponseBody;
import com.attus.testetecnico.entities.Person;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class PersonEntityToResponseBodyConverter implements Converter<Person, PersonResponseBody> {
    @Override
    public PersonResponseBody convert(Person source) {
        var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return new PersonResponseBody(
                source.getId(),
                source.getFullName(),
                source.getDateOfBirth().format(formatter),
                source.getMainAddress().orElse(null)
        );
    }
}
