package com.andreyprodromov.java.medical.data.repo;

import com.andreyprodromov.java.medical.data.entity.Speciality;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class SpecialityRepositoryTest {

    @Autowired
    private SpecialityRepository specialityRepository;

    @BeforeEach
    public void setUp() {
        Speciality speciality = new Speciality();
        speciality.setName("UNG");
        specialityRepository.save(speciality);

        speciality = new Speciality();
        speciality.setName("General");
        specialityRepository.save(speciality);

        speciality = new Speciality();
        speciality.setName("X-ray");
        specialityRepository.save(speciality);
    }

    @Test
    void testFindByName() {

        Speciality foundSpeciality = specialityRepository.findByName("UNG");
        assertNotNull(foundSpeciality);
        assertEquals("UNG", foundSpeciality.getName());
    }

    @Test
    void testFindByNameNotFound() {

        Speciality foundSpeciality = specialityRepository.findByName("Math");
        assertNull(foundSpeciality);
    }
}
