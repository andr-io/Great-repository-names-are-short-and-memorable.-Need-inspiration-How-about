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
public class EditPatientDTO {
    private long id;

    @NotBlank
    private String name;
    @NotBlank
    private String EGN;
    private boolean hasPaidHisInsuranceInTheLastSixMonths;
    private String uniqueDoctorCode;
}
