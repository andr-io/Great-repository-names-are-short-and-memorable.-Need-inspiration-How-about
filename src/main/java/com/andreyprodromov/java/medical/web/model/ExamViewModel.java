package com.andreyprodromov.java.medical.web.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExamViewModel {
    private Long id;
    private String uniqueDoctorCode;
    private String patientSSN;
    private LocalDate conductedDate;
    private Set<String> diagnosys;
    private LocalDate sickLeaveStartDate;
    private int sickLeaveDuration;

    public Set<String> getDiagnosys() {
        return new HashSet<>(diagnosys) {
            @Override
            public String toString() {
                return String.join(",", this);
            }
        };
    }
}
