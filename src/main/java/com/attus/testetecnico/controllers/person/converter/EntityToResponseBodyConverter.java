package com.attus.testetecnico.controllers.person.converter;

import com.attus.testetecnico.controllers.person.dto.PersonResponseBody;
import com.attus.testetecnico.entities.Address;
import com.attus.testetecnico.entities.Person;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class EntityToResponseBodyConverter implements Converter<Person, PersonResponseBody> {
    @Override
    public PersonResponseBody convert(Person source) {
        return new PersonResponseBody(
                source.getId(),
                source.getFullName(),
                source.getDateOfBirth(),
                source.getMainAddress()
        );
    }
}
