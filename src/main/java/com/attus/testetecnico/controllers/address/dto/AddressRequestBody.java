package com.attus.testetecnico.controllers.address.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AddressRequestBody(
        @NotBlank(message = "Cannot be null or empty")
        String street,

        @NotBlank(message = "Cannot be null or empty")
        String zipCode,

        @Min(value = 1, message = "Cannot be less than 1")
        int number,

        @NotBlank(message = "Cannot be null or empty")
        String city,

        @NotBlank(message = "Cannot be null or empty")
        String state,

        boolean main
) {
}
