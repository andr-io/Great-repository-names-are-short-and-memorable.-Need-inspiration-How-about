package com.andreyprodromov.java.medical.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDTO {

    @Email(message = "Please enter a valid email address.")
    private String email;

    @NotBlank(message = "Password cannot be empty.")
    private String password;

    private String role;

    // Fields for Doctor
    private String doctorName;
    private String doctorCode;
    private String specialties;

    // Fields for Patient
    private String patientName;
    private String ssn;
    private Boolean hasPaidInsurance;

}
