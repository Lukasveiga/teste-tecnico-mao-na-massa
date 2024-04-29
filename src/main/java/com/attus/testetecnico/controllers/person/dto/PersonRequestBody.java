package com.attus.testetecnico.controllers.person.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PersonRequestBody(
        @NotBlank(message = "Cannot be null or empty")
        String fullName,
        @JsonFormat(pattern = "dd/MM/yyyy")
                @NotNull(message = "Cannot be null or empty")
        LocalDate dateOfBirth
) {
}
