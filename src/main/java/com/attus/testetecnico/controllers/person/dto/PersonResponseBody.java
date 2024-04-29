package com.attus.testetecnico.controllers.person.dto;

import com.attus.testetecnico.entities.Address;

public record PersonResponseBody(Long id, String fullName, String dateOfBirth, Address mainAddress) {
}
