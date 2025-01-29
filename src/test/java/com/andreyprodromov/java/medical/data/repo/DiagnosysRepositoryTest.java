package com.andreyprodromov.java.medical.data.repo;

import com.andreyprodromov.java.medical.data.entity.Diagnosys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class DiagnosysRepositoryTest {

    @Autowired
    private DiagnosysRepository diagnosysRepository;

    @BeforeEach
    public void setUp() {
        Diagnosys diagnosys = new Diagnosys();
        diagnosys.setName("Covid");
        diagnosysRepository.save(diagnosys);

        diagnosys = new Diagnosys();
        diagnosys.setName("Diabetes");
        diagnosysRepository.save(diagnosys);

        diagnosys = new Diagnosys();
        diagnosys.setName("Death");
        diagnosysRepository.save(diagnosys);
    }

    @Test
    void testFindByName() {
        Optional<Diagnosys> foundDiagnosys = diagnosysRepository.findByName("Diabetes");
        assertTrue(foundDiagnosys.isPresent());
        assertEquals("Diabetes", foundDiagnosys.get().getName());
    }

    @Test
    void testFindByNameNotFound() {
        Optional<Diagnosys> foundDiagnosys = diagnosysRepository.findByName("Life");
        assertFalse(foundDiagnosys.isPresent());
    }
}
