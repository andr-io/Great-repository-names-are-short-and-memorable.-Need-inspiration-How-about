package com.andreyprodromov.java.medical.web.model;

import com.andreyprodromov.java.medical.data.entity.Speciality;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DoctorViewModel {
    private long id;
    private String name;
    private String username;
    private String uniqueDoctorCode;
    private Set<Speciality> speciality;
    private String specialityConcatenated;
}
