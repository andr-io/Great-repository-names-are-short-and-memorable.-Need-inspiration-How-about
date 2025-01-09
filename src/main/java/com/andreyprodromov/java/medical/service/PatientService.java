package com.andreyprodromov.java.medical.service;

import com.andreyprodromov.java.medical.data.entity.Patient;
import com.andreyprodromov.java.medical.dto.EditPatientDTO;

import java.util.List;

public interface PatientService {
    List<Patient> getPatients();

    Patient getPatient(long id);

    Patient createPatient(Patient patient);

    Patient updatePatient(Patient patient, long id);

    void deletePatient(long id);

    Patient updatePatient(EditPatientDTO editPatientDTO);
}
