package com.attus.testetecnico.controllers.person.converter;

import com.attus.testetecnico.controllers.person.dto.PersonRequestBody;
import com.attus.testetecnico.entities.Person;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class RequestBodyToEntityConverter implements Converter<PersonRequestBody, Person> {
    @Override
    public Person convert(PersonRequestBody source) {
        var person = new Person();
        person.setFullName(source.fullName());
        person.setDateOfBirth(source.dateOfBirth());
        return person;
    }
}
