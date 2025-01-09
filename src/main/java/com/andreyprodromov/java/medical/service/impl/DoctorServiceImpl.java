package com.andreyprodromov.java.medical.service.impl;

import com.andreyprodromov.java.medical.data.entity.Doctor;
import com.andreyprodromov.java.medical.data.entity.Speciality;
import com.andreyprodromov.java.medical.data.repo.DoctorRepository;
import com.andreyprodromov.java.medical.data.repo.SpecialityRepository;
import com.andreyprodromov.java.medical.dto.EditDoctorDTO;
import com.andreyprodromov.java.medical.service.DoctorService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final SpecialityRepository specialityRepository;

    @Override
    public List<Doctor> getDoctors() {
        return this.doctorRepository.findAll();
    }

    @Override
    public Doctor getDoctor(long id) {
        return this.doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor with id=" + id + " not found!" ));
    }

    @Override
    public Doctor createDoctor(Doctor doctor) {
        return this.doctorRepository.save(doctor);
    }

    @Override
    public Doctor updateDoctor(Doctor doctor, long id) {
        return this.doctorRepository.findById(id)
                .map(doctor1 -> {
                    doctor1.setName(doctor.getName());
                    doctor1.setUniqueDoctorCode(doctor.getUniqueDoctorCode());
                    doctor1.setSpeciality(doctor.getSpeciality());
                    return this.doctorRepository.save(doctor1);
                }).orElseGet(()->
                        this.doctorRepository.save(doctor)
                );
    }

    @Override
    public Doctor updateDoctor(EditDoctorDTO editDoctorDTO) {
        Set<Speciality> toAdd = new HashSet<>();

        List<String> specialities =
            Arrays.stream(editDoctorDTO.getSpecialties().split(","))
                  .map(String::strip)
                  .toList();

        for (var speciality : specialities) {
            Speciality sp = specialityRepository.findByName(speciality);

            if (sp != null) {
                toAdd.add(sp);
            } else {
                var newSpeciality = new Speciality(speciality);
                specialityRepository.save(newSpeciality);
                toAdd.add(newSpeciality);
            }
        }

        Doctor doctor = doctorRepository.findById(editDoctorDTO.getId()).get();
        doctor.setUniqueDoctorCode(editDoctorDTO.getUniqueDoctorCode());
        doctor.setName(editDoctorDTO.getName());
        doctor.setSpeciality(toAdd);

        return updateDoctor(doctor, doctor.getId());
    }

    @Override
    public void deleteDoctor(long id) {
        this.doctorRepository.deleteById(id);
    }
}
