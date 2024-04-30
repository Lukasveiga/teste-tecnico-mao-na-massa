package com.attus.testetecnico.controllers.address.dto;

public record AddressResponseBody(
        Long id,
        String street,
        String zipCode,
        int number,
        String city,
        String state,
        boolean main,
        Long personId
) {
}
