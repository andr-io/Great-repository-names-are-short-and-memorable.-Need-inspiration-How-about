package com.andreyprodromov.java.medical.data.repo;

import com.andreyprodromov.java.medical.data.entity.SickLeaveDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SickLeaveDocumentRepository extends JpaRepository<SickLeaveDocument, Long> {
}
