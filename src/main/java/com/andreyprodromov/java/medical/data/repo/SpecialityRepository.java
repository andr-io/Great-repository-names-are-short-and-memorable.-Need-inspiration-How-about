package com.andreyprodromov.java.medical.data.repo;

import com.andreyprodromov.java.medical.data.entity.Speciality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialityRepository extends JpaRepository<Speciality, Long> {
    Speciality findByName(String name);
}
