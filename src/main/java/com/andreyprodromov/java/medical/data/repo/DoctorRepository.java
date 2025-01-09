package com.andreyprodromov.java.medical.data.repo;

import com.andreyprodromov.java.medical.data.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Doctor findByUniqueDoctorCode(String code);
}
