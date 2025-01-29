package com.andreyprodromov.java.medical.data.repo;

import com.andreyprodromov.java.medical.data.entity.Patient;
import com.andreyprodromov.java.medical.data.entity.Doctor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class PatientRepositoryTest {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @BeforeEach
    public void setUp() {
        Doctor doctor = new Doctor();
        doctor.setName("Dr. Smith");
        doctorRepository.save(doctor);

        Patient patient = new Patient();
        patient.setName("John Doe");
        patient.setEGN("1234567890");
        patient.setPersonalDoctor(doctor);
        patient.setHasPaidHisInsuranceInTheLastSixMonths(true);

        patientRepository.save(patient);
    }

    @Test
    void testFindByEGN() {
        Optional<Patient> foundPatient = patientRepository.findByEGN("1234567890");

        assertTrue(foundPatient.isPresent());
        assertEquals("John Doe", foundPatient.get().getName());
        assertEquals("1234567890", foundPatient.get().getEGN());
    }

    @Test
    void testFindByEGNNotFound() {
        Optional<Patient> foundPatient = patientRepository.findByEGN("1232131231");
        assertFalse(foundPatient.isPresent());
    }
}
