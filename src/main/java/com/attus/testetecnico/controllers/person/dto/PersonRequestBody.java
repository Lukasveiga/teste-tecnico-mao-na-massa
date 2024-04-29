package com.attus.testetecnico.controllers.person.dto;

import com.attus.testetecnico.entities.Address;

import java.time.LocalDate;
import java.util.List;

public record PersonRequestBody(
        String fullName,
        LocalDate dateOfBirth,
        List<Address> addresses
) {
}
