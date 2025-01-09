package com.andreyprodromov.java.medical.data.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
public class Doctor extends User {
    private String name;

    {
        role = "ROLE_DOCTOR";
    }

    @Column(unique = true)
    @NotNull
    private String uniqueDoctorCode;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Speciality> speciality;
}
