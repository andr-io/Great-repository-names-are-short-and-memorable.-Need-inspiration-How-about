package com.andreyprodromov.java.medical.service.impl;

import com.andreyprodromov.java.medical.data.entity.Doctor;
import com.andreyprodromov.java.medical.data.entity.Patient;
import com.andreyprodromov.java.medical.data.entity.Speciality;
import com.andreyprodromov.java.medical.data.entity.User;
import com.andreyprodromov.java.medical.data.repo.DoctorRepository;
import com.andreyprodromov.java.medical.data.repo.SpecialityRepository;
import com.andreyprodromov.java.medical.data.repo.UserRepository;
import com.andreyprodromov.java.medical.dto.CreateUserDTO;
import com.andreyprodromov.java.medical.service.DoctorService;
import com.andreyprodromov.java.medical.service.PatientService;
import com.andreyprodromov.java.medical.service.UserService;
import com.andreyprodromov.java.medical.service.exceptions.NonExistentDoctorCodeException;
import com.andreyprodromov.java.medical.service.exceptions.UsernameAlreadyExistsException;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SpecialityRepository specialityRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByUsername(username);
    }

    @Override
    public void create(CreateUserDTO userDTO) {
        if (userRepository.findByUsername(userDTO.getEmail()) != null) {
            throw new UsernameAlreadyExistsException("Username is already taken");
        }

        switch (userDTO.getRole()) {
            case "admin" -> {
                User user = new User();
                user.setUsername(userDTO.getEmail());
                user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
                this.create(user);
            }
            case "doctor" -> {
                Set<Speciality> toAdd = new HashSet<>();

                List<String> specialities =
                    Arrays.stream(userDTO.getSpecialties().split(","))
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

                Doctor doctor = new Doctor();
                doctor.setUsername(userDTO.getEmail());
                doctor.setPassword(passwordEncoder.encode(userDTO.getPassword()));
                doctor.setUniqueDoctorCode(userDTO.getDoctorCode());
                doctor.setName(userDTO.getDoctorName());
                doctor.setSpeciality(toAdd);

                doctorService.createDoctor(doctor);
            }
            case "patient" -> {
                Doctor personalDoctor = doctorRepository.findByUniqueDoctorCode(userDTO.getDoctorCode());

                if (personalDoctor == null) {
                    throw new NonExistentDoctorCodeException("Doctor with the given code not found");
                }

                Patient patient = new Patient();
                patient.setUsername(userDTO.getEmail());
                patient.setName(userDTO.getPatientName());
                patient.setPassword(passwordEncoder.encode(userDTO.getPassword()));
                patient.setPersonalDoctor(personalDoctor);
                patient.setEGN(userDTO.getSsn());
                patient.setHasPaidHisInsuranceInTheLastSixMonths(userDTO.getHasPaidInsurance());

                patientService.createPatient(patient);
            }
        }
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
