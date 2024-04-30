package com.attus.testetecnico.controllers.address.converter;

import com.attus.testetecnico.controllers.address.dto.AddressRequestBody;
import com.attus.testetecnico.entities.Address;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AddressRequestBodyToEntityConverter implements Converter<AddressRequestBody, Address> {

    @Override
    public Address convert(AddressRequestBody source) {
        var address = new Address();
        address.setStreet(source.street());
        address.setZipCode(source.zipCode());
        address.setNumber(source.number());
        address.setCity(source.city());
        address.setState(source.state());
        address.setMain(source.main());
        return address ;
    }
}
