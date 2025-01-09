package com.andreyprodromov.java.medical.service.impl;

import com.andreyprodromov.java.medical.data.entity.Doctor;
import com.andreyprodromov.java.medical.data.entity.Patient;
import com.andreyprodromov.java.medical.data.repo.DoctorRepository;
import com.andreyprodromov.java.medical.data.repo.PatientRepository;
import com.andreyprodromov.java.medical.dto.EditPatientDTO;
import com.andreyprodromov.java.medical.service.PatientService;
import com.andreyprodromov.java.medical.service.exceptions.NonExistentDoctorCodeException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @Override
    public List<Patient> getPatients() {
        return this.patientRepository.findAll();
    }

    @Override
    public Patient getPatient(long id) {
        return this.patientRepository.findById(id)
                                    .orElseThrow(() -> new RuntimeException("Patient with id=" + id + " not found!" ));
    }

    @Override
    public Patient createPatient(Patient patient) {
        return this.patientRepository.save(patient);
    }

    @Override
    public Patient updatePatient(Patient patient, long id) {
        return this.patientRepository.findById(id)
                                    .map(patient1 -> {
                                        patient1.setName(patient.getName());
                                        patient1.setEGN(patient.getEGN());
                                        patient1.setPersonalDoctor(patient.getPersonalDoctor());
                                        patient1.setExams(patient.getExams());
                                        patient1.setPersonalDoctor(patient.getPersonalDoctor());
                                        patient1.setHasPaidHisInsuranceInTheLastSixMonths(patient.isHasPaidHisInsuranceInTheLastSixMonths());
                                        return this.patientRepository.save(patient1);
                                    }).orElseGet(()->
                this.patientRepository.save(patient)
            );
    }

    @Override
    public void deletePatient(long id) {
        this.patientRepository.deleteById(id);
    }

    @Override
    public Patient updatePatient(EditPatientDTO editPatientDTO) {
        Patient patient = patientRepository.getReferenceById(editPatientDTO.getId());
        Doctor personalDoctor = doctorRepository.findByUniqueDoctorCode(editPatientDTO.getUniqueDoctorCode());

        if (personalDoctor == null) {
            throw new NonExistentDoctorCodeException("Doctor with the given code not found");
        }

        patient.setName(editPatientDTO.getName());
        patient.setPersonalDoctor(personalDoctor);
        patient.setEGN(editPatientDTO.getEGN());
        patient.setHasPaidHisInsuranceInTheLastSixMonths(editPatientDTO.isHasPaidHisInsuranceInTheLastSixMonths());

        return updatePatient(patient, editPatientDTO.getId());
    }
}
