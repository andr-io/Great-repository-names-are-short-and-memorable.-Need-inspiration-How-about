package com.andreyprodromov.java.medical.data.repo;

import com.andreyprodromov.java.medical.data.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        User user = new User();
        user.setUsername("admin@admin.com");
        user.setPassword("admin");
        user.setRole("ROLE_ADMIN");
        userRepository.save(user);

        user = new User();
        user.setUsername("doctor@doctor.com");
        user.setPassword("doctor");
        user.setRole("ROLE_DOCTOR");
        userRepository.save(user);

        user = new User();
        user.setUsername("patient@patient.com");
        user.setPassword("patient");
        user.setRole("ROLE_PATIENT");
        userRepository.save(user);
    }

    @Test
    void testFindByUsername() {
        User foundUser = (User) userRepository.findByUsername("doctor@doctor.com");
        assertNotNull(foundUser);
        assertEquals("doctor@doctor.com", foundUser.getUsername());
    }

    @Test
    void testFindByUsernameNotFound() {
        User foundUser = (User) userRepository.findByUsername("no@yes.com");
        assertNull(foundUser);
    }
}
