package com.andreyprodromov.java.medical.service;

import com.andreyprodromov.java.medical.data.entity.Doctor;
import com.andreyprodromov.java.medical.dto.EditDoctorDTO;

import java.util.List;

public interface DoctorService {
    List<Doctor> getDoctors();

    Doctor getDoctor(long id);

    Doctor createDoctor(Doctor doctor);

    Doctor updateDoctor(Doctor doctor, long id);

    void deleteDoctor(long id);

    Doctor updateDoctor(EditDoctorDTO editDoctorDTO);
}
