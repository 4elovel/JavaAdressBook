package com.example.adressbook.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PersonInsertDTO {

    @NotEmpty(message = "Full name is required")
    private String fullName;

    @NotEmpty(message = "Phone number is required")
    @Size(min = 10, max = 15)
    private String phone;

    @Email(message = "Email should be valid")
    private String email;

    private String blogUrl;
    private String notes;
}
