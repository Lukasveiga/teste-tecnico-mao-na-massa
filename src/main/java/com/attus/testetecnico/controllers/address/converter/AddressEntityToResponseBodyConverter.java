package com.attus.testetecnico.controllers.address.converter;

import com.attus.testetecnico.controllers.address.dto.AddressResponseBody;
import com.attus.testetecnico.entities.Address;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AddressEntityToResponseBodyConverter implements Converter<Address, AddressResponseBody> {
    @Override
    public AddressResponseBody convert(Address source) {
        return new AddressResponseBody(
                source.getId(),
                source.getStreet(),
                source.getZipCode(),
                source.getNumber(),
                source.getCity(),
                source.getState(),
                source.isMain(),
                source.getPerson().getId()
        );
    }
}
