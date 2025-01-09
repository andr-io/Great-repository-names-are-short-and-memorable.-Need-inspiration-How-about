package com.andreyprodromov.java.medical.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewExamDTO {

    @NotNull(message = "Doctor's unique code is required")
    private String uniqueDoctorCode;

    @NotNull(message = "Patient is required")
    private String EGN;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past(message = "The date must be in the past")
    private LocalDate conductedDate;

    @NotNull(message = "Diagnosys is required")
    private Set<String> diagnosys;

    private LocalDate sickLeaveStartDate;

    @PositiveOrZero(message = "Sick leave duration must be a positive number")
    private int sickLeaveDuration;
}
