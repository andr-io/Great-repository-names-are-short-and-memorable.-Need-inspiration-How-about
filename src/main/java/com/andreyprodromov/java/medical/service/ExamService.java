package com.andreyprodromov.java.medical.service;

import com.andreyprodromov.java.medical.data.entity.Diagnosys;
import com.andreyprodromov.java.medical.data.entity.Doctor;
import com.andreyprodromov.java.medical.data.entity.Exam;
import com.andreyprodromov.java.medical.dto.EditExamDTO;
import com.andreyprodromov.java.medical.dto.NewExamDTO;

import java.time.Month;
import java.util.List;

public interface ExamService {
    List<Exam> getExams();

    Exam getExam(long id);

    Exam createExam(Exam exam);

    Exam updateExam(Exam exam, long id);

    void deleteExam(long id);

    Exam createExam(NewExamDTO newExamDTO);

    Exam editExam(EditExamDTO editExamDTO);

    Diagnosys getMostFrequentDiagnosys();

    Month getMonthWithMostSickLeaves();

    Doctor getDoctorWithMostSickLeaveDocuments();
}
