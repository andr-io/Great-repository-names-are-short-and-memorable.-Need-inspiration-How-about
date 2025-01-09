package com.andreyprodromov.java.medical.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditDoctorDTO {
    private long id;

    @NotBlank
    private String name;

    @NotBlank
    private String uniqueDoctorCode;

    @NotBlank
    private String specialties;
}
