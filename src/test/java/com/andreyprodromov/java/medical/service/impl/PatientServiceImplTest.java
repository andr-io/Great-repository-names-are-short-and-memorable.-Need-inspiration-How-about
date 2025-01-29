package com.andreyprodromov.java.medical.service.impl;

import com.andreyprodromov.java.medical.data.entity.Doctor;
import com.andreyprodromov.java.medical.data.entity.Patient;
import com.andreyprodromov.java.medical.data.repo.DoctorRepository;
import com.andreyprodromov.java.medical.data.repo.PatientRepository;
import com.andreyprodromov.java.medical.dto.EditPatientDTO;
import com.andreyprodromov.java.medical.service.exceptions.NonExistentDoctorCodeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PatientServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private PatientServiceImpl patientService;

    private Patient patient;
    private Doctor doctor;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setName("John Doe");
        patient.setEGN("1234567890");

        doctor = new Doctor();
        doctor.setName("Dr. Jane Smith");
        doctor.setUniqueDoctorCode("123");

        patient.setPersonalDoctor(doctor);
    }

    @Test
    void testGetPatients() {
        when(patientRepository.findAll()).thenReturn(List.of(patient));

        List<Patient> patients = patientService.getPatients();

        assertNotNull(patients);
        assertEquals(1, patients.size());
        assertEquals("John Doe", patients.get(0).getName());
    }

    @Test
    void testGetPatient() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        Patient foundPatient = patientService.getPatient(1L);

        assertNotNull(foundPatient);
        assertEquals("John Doe", foundPatient.getName());
    }

    @Test
    void testGetPatientNotFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> patientService.getPatient(1L));
    }

    @Test
    void testCreatePatient() {
        when(patientRepository.save(patient)).thenReturn(patient);

        Patient createdPatient = patientService.createPatient(patient);

        assertNotNull(createdPatient);
        assertEquals("John Doe", createdPatient.getName());
    }

    @Test
    void testUpdatePatient() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(patient)).thenReturn(patient);

        patient.setName("Jane Doe");
        Patient updatedPatient = patientService.updatePatient(patient, 1L);

        assertNotNull(updatedPatient);
        assertEquals("Jane Doe", updatedPatient.getName());
    }

    @Test
    void testDeletePatient() {
        patientService.deletePatient(1L);

        verify(patientRepository, times(1)).deleteById(1L);
    }

    @Test
    void testUpdatePatientWithDTO() {
        EditPatientDTO editPatientDTO = new EditPatientDTO();
        editPatientDTO.setId(1L);
        editPatientDTO.setName("Jane Doe");
        editPatientDTO.setEGN("9876543210");
        editPatientDTO.setUniqueDoctorCode("123");
        editPatientDTO.setHasPaidHisInsuranceInTheLastSixMonths(true);

        when(doctorRepository.findByUniqueDoctorCode(editPatientDTO.getUniqueDoctorCode())).thenReturn(doctor);
        when(patientRepository.getReferenceById(editPatientDTO.getId())).thenReturn(patient);
        when(patientRepository.save(patient)).thenReturn(patient);

        Patient updatedPatient = patientService.updatePatient(editPatientDTO);

        assertNotNull(updatedPatient);
        assertEquals("Jane Doe", updatedPatient.getName());
        assertEquals("9876543210", updatedPatient.getEGN());
        assertEquals("Dr. Jane Smith", updatedPatient.getPersonalDoctor().getName());
    }

    @Test
    void testUpdatePatientWithInvalidDoctorCode() {
        EditPatientDTO editPatientDTO = new EditPatientDTO();
        editPatientDTO.setId(1L);
        editPatientDTO.setName("Jane Doe");
        editPatientDTO.setEGN("9876543210");
        editPatientDTO.setUniqueDoctorCode("INVALID_CODE");
        editPatientDTO.setHasPaidHisInsuranceInTheLastSixMonths(true);

        when(doctorRepository.findByUniqueDoctorCode(editPatientDTO.getUniqueDoctorCode())).thenReturn(null);

        assertThrows(NonExistentDoctorCodeException.class, () -> patientService.updatePatient(editPatientDTO));
    }
}
