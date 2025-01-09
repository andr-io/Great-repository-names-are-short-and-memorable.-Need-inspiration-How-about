package com.andreyprodromov.java.medical.data.repo;

import com.andreyprodromov.java.medical.data.entity.Diagnosys;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiagnosysRepository extends JpaRepository<Diagnosys, Long> {
    Optional<Diagnosys> findByName(String name);
}
