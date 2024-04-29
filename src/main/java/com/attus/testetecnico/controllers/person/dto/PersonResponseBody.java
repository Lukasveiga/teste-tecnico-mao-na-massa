package com.attus.testetecnico.controllers.person.dto;

import com.attus.testetecnico.entities.Address;

import java.time.LocalDate;

public record PersonResponseBody(Long id, String fullName, LocalDate dateOfBirth, Address mainAddress) {
}
