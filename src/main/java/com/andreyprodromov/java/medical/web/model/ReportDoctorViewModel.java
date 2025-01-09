package com.andreyprodromov.java.medical.web.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportDoctorViewModel {
    String doctorCode;
    long patientCount;
}
