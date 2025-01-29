package com.andreyprodromov.java.medical.data.repo;

import com.andreyprodromov.java.medical.data.entity.Doctor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class DoctorRepositoryTest {

    @Autowired
    private DoctorRepository doctorRepository;

    @BeforeEach
    public void setUp() {
        Doctor doctor = new Doctor();
        doctor.setUniqueDoctorCode("DOC123");
        doctor.setName("Dr. Jane Smith");
        doctorRepository.save(doctor);

        doctor = new Doctor();
        doctor.setUniqueDoctorCode("DOC122");
        doctor.setName("Dr. Jack Smith");
        doctorRepository.save(doctor);

        doctor = new Doctor();
        doctor.setUniqueDoctorCode("DOC124");
        doctor.setName("Dr. Joule Smith");
        doctorRepository.save(doctor);
    }

    @Test
    void testFindByUniqueDoctorCode() {
        Doctor foundDoctor = doctorRepository.findByUniqueDoctorCode("DOC123");

        assertNotNull(foundDoctor);
        assertEquals("Dr. Jane Smith", foundDoctor.getName());
        assertEquals("DOC123", foundDoctor.getUniqueDoctorCode());
    }

    @Test
    void testFindByUniqueDoctorCodeNotFound() {
        Doctor foundDoctor = doctorRepository.findByUniqueDoctorCode("NotDoc");
        assertNull(foundDoctor);
    }
}
