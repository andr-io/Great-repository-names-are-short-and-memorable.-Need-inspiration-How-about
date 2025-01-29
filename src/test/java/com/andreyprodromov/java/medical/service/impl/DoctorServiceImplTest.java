package com.andreyprodromov.java.medical.service.impl;

import com.andreyprodromov.java.medical.data.entity.Doctor;
import com.andreyprodromov.java.medical.data.entity.Speciality;
import com.andreyprodromov.java.medical.data.repo.DoctorRepository;
import com.andreyprodromov.java.medical.data.repo.SpecialityRepository;
import com.andreyprodromov.java.medical.dto.EditDoctorDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceImplTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private SpecialityRepository specialityRepository;

    @InjectMocks
    private DoctorServiceImpl doctorService;

    private Doctor doctor1;
    private Doctor doctor2;
    private Doctor doctor3;

    private Speciality speciality;

    @BeforeEach
    void setUp() {
        doctor1 = new Doctor();
        doctor1.setName("Dr. John Doe");
        doctor1.setUniqueDoctorCode("123");
        speciality = new Speciality("UNG");

        doctor2 = new Doctor();
        doctor2.setName("Dr. Jane Doe");
        doctor2.setUniqueDoctorCode("124");
        speciality = new Speciality("General");

        doctor3 = new Doctor();
        doctor3.setName("Dr. Jane Doe");
        doctor3.setUniqueDoctorCode("124");
        speciality = new Speciality("Xray");
    }

    @Test
    void testGetDoctors() {
        when(doctorRepository.findAll()).thenReturn(List.of(doctor1, doctor2, doctor3));

        var doctors = doctorService.getDoctors();

        assertNotNull(doctors);
        assertEquals(3, doctors.size());
    }

    @Test
    void testGetDoctor() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor1));

        Doctor foundDoctor = doctorService.getDoctor(1L);
        assertNotNull(foundDoctor);
        assertEquals("Dr. John Doe", foundDoctor.getName());
    }

    @Test
    void testGetDoctorNotFound() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> doctorService.getDoctor(1L));
    }

    @Test
    void testCreateDoctor() {
        when(doctorRepository.save(doctor1)).thenReturn(doctor1);

        Doctor createdDoctor = doctorService.createDoctor(doctor1);

        assertNotNull(createdDoctor);
        assertEquals("Dr. John Doe", createdDoctor.getName());
    }

    @Test
    void testUpdateDoctor() {
        Doctor updatedDoctor = new Doctor();

        updatedDoctor.setName("Dr. John Smith");
        updatedDoctor.setUniqueDoctorCode("999");
        updatedDoctor.setSpeciality(Set.of(speciality));

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor1));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(updatedDoctor);

        Doctor result = doctorService.updateDoctor(updatedDoctor, 1L);

        assertNotNull(result);
        assertEquals("Dr. John Smith", result.getName());
        assertEquals("999", result.getUniqueDoctorCode());
        assertTrue(result.getSpeciality().contains(speciality));
    }

    @Test
    void testUpdateDoctorWhenNotExistent() {
        Doctor updatedDoctor = new Doctor();
        updatedDoctor.setName("Dr. John Smith");
        updatedDoctor.setUniqueDoctorCode("999");
        updatedDoctor.setSpeciality(Set.of(speciality));

        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());
        when(doctorRepository.save(any(Doctor.class))).then(returnsFirstArg());

        Doctor result = doctorService.updateDoctor(updatedDoctor, 1L);

        assertNotNull(result);
        assertEquals("Dr. John Smith", result.getName());
        assertEquals("999", result.getUniqueDoctorCode());
        assertTrue(result.getSpeciality().contains(speciality));
    }

    @Test
    void testUpdateDoctorWithDTO() {
        EditDoctorDTO editDoctorDTO = new EditDoctorDTO();
        editDoctorDTO.setId(1L);
        editDoctorDTO.setName("Dr. John Smith");
        editDoctorDTO.setUniqueDoctorCode("123");
        editDoctorDTO.setSpecialties("spec1, spec2");

        Speciality spec1 = new Speciality("Neurology");
        Speciality spec2 = new Speciality("Neurology");

        when(specialityRepository.findByName("spec1")).thenReturn(spec1);
        when(specialityRepository.findByName("spec2")).thenReturn(spec2);
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor1));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor1);

        // When
        Doctor result = doctorService.updateDoctor(editDoctorDTO);

        assertNotNull(result);
        assertEquals("Dr. John Smith", result.getName());
        assertEquals("123", result.getUniqueDoctorCode());
        assertTrue(result.getSpeciality().contains(spec1));
        assertTrue(result.getSpeciality().contains(spec2));
    }

    @Test
    void testDeleteDoctor() {
        doctorService.deleteDoctor(1L);

        verify(doctorRepository, times(1)).deleteById(1L);
    }
}
