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
import com.andreyprodromov.java.medical.service.exceptions.NonExistentDoctorCodeException;
import com.andreyprodromov.java.medical.service.exceptions.UsernameAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SpecialityRepository specialityRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private DoctorService doctorService;

    @Mock
    private PatientService patientService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private Doctor doctor;
    private Patient patient;
    private Speciality speciality;
    private CreateUserDTO userDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        user.setPassword("testpassword");

        doctor = new Doctor();
        doctor.setUsername("doctor1");
        doctor.setPassword("doctorpassword");
        doctor.setUniqueDoctorCode("1234");

        patient = new Patient();
        patient.setUsername("patient1");
        patient.setPassword("patientpassword");

        speciality = new Speciality();
        speciality.setName("UNG");

        userDTO = new CreateUserDTO();
        userDTO.setEmail("testuser@example.com");
        userDTO.setPassword("password");
        userDTO.setRole("admin");
    }

    @Test
    void testCreateUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.create(user);

        assertNotNull(savedUser);
        assertEquals("testuser", savedUser.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testLoadUserByUsername() {
        when(userRepository.findByUsername("testuser")).thenReturn(user);

        UserDetails userDetails = userService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
    }

    @Test
    void testCreateAdminUser() {
        when(userRepository.findByUsername(userDTO.getEmail())).thenReturn(null);
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("password");

        userService.create(userDTO);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateDoctorUser() {
        userDTO.setRole("doctor");
        userDTO.setDoctorCode("1234");
        userDTO.setSpecialties("UNG");

        when(userRepository.findByUsername(userDTO.getEmail())).thenReturn(null);
        when(specialityRepository.findByName("UNG")).thenReturn(speciality);
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("password");

        userService.create(userDTO);

        verify(doctorService, times(1)).createDoctor(any(Doctor.class));
    }

    @Test
    void testCreatePatientUser() {
        userDTO.setRole("patient");
        userDTO.setDoctorCode("1234");
        userDTO.setPatientName("John Doe");
        userDTO.setHasPaidInsurance(true);
        userDTO.setSsn("123456789");

        when(userRepository.findByUsername(userDTO.getEmail())).thenReturn(null);
        when(doctorRepository.findByUniqueDoctorCode(userDTO.getDoctorCode())).thenReturn(doctor);
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("password");

        userService.create(userDTO);

        verify(patientService, times(1)).createPatient(any(Patient.class));
    }

    @Test
    void testCreatePatientWithNonExistentDoctorCode() {
        userDTO.setRole("patient");
        userDTO.setDoctorCode("nonexistent");

        when(userRepository.findByUsername(userDTO.getEmail())).thenReturn(null);
        when(doctorRepository.findByUniqueDoctorCode(userDTO.getDoctorCode())).thenReturn(null);

        assertThrows(NonExistentDoctorCodeException.class, () -> userService.create(userDTO));
    }

    @Test
    void testCreateUserWithExistingUsername() {
        when(userRepository.findByUsername(userDTO.getEmail())).thenReturn(user);

        assertThrows(UsernameAlreadyExistsException.class, () -> userService.create(userDTO));
    }

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> users = userService.getAll();

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("testuser", users.get(0).getUsername());
    }

    @Test
    void testDeleteUser() {
        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }
}
