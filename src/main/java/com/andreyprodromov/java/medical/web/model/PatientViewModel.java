package com.andreyprodromov.java.medical.web.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientViewModel {
    private long id;
    private String name;
    private String username;
    private String EGN;
    private boolean hasPaidHisInsuranceInTheLastSixMonths;
    private String uniqueDoctorCode;
}
