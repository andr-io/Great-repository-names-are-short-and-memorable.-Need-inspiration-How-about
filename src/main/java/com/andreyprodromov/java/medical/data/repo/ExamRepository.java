package com.andreyprodromov.java.medical.data.repo;

import com.andreyprodromov.java.medical.data.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRepository  extends JpaRepository<Exam, Long> {
}
