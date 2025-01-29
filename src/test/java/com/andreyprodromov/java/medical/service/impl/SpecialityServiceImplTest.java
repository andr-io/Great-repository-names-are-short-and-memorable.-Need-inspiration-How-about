package com.andreyprodromov.java.medical.service.impl;

import com.andreyprodromov.java.medical.data.entity.Speciality;
import com.andreyprodromov.java.medical.data.repo.SpecialityRepository;
import com.andreyprodromov.java.medical.service.SpecialityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecialityServiceImplTest {

    @Mock
    private SpecialityRepository specialityRepository;

    @InjectMocks
    private SpecialityServiceImpl specialityService;

    private Speciality speciality1;
    private Speciality speciality2;

    @BeforeEach
    void setUp() {
        speciality1 = new Speciality();
        speciality1.setName("General");

        speciality2 = new Speciality();
        speciality2.setName("Dentist");
    }

    @Test
    void testGetAll() {
        when(specialityRepository.findAll()).thenReturn(List.of(speciality1, speciality2));

        List<Speciality> specialties = specialityService.getAll();

        assertNotNull(specialties);
        assertEquals(2, specialties.size());
        assertEquals("General", specialties.get(0).getName());
        assertEquals("Dentist", specialties.get(1).getName());
    }

    @Test
    void testGetAllNoSpecialities() {
        when(specialityRepository.findAll()).thenReturn(List.of());

        List<Speciality> specialties = specialityService.getAll();

        assertNotNull(specialties);
        assertTrue(specialties.isEmpty());
    }
}
