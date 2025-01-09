package com.andreyprodromov.java.medical.data.repo;

import com.andreyprodromov.java.medical.data.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByEGN(String EGN);
}

