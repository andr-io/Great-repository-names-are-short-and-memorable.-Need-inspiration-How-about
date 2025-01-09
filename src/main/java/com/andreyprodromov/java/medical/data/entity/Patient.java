package com.andreyprodromov.java.medical.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
public class Patient extends User {
    private String name;
    private String EGN;
    private boolean hasPaidHisInsuranceInTheLastSixMonths;

    {
        role = "ROLE_PATIENT";
    }

    @ManyToOne
    private Doctor personalDoctor;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Exam> exams;
}
