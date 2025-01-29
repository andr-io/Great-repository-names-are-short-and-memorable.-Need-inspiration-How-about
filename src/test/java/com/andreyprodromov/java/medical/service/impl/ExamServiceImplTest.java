package com.andreyprodromov.java.medical.service.impl;

import com.andreyprodromov.java.medical.data.entity.*;
import com.andreyprodromov.java.medical.data.repo.*;
import com.andreyprodromov.java.medical.dto.NewExamDTO;
import com.andreyprodromov.java.medical.service.exceptions.PatientDoesNotExistException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamServiceImplTest {

    @Mock
    private ExamRepository examRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private SickLeaveDocumentRepository sickLeaveDocumentRepository;

    @Mock
    private DiagnosysRepository diagnosysRepository;

    @InjectMocks
    private ExamServiceImpl examService;

    private Exam exam;
    private Patient patient;
    private Doctor doctor;
    private SickLeaveDocument sickLeaveDocument;
    private Diagnosys diagnosys;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setEGN("1234567890");

        doctor = new Doctor();
        doctor.setName("Dr. John Doe");
        doctor.setUniqueDoctorCode("234");

        exam = new Exam();
        exam.setPatient(patient);
        exam.setDoctor(doctor);
        exam.setConductedDate(LocalDate.now());

        sickLeaveDocument = new SickLeaveDocument();
        sickLeaveDocument.setStartDate(LocalDate.now());
        sickLeaveDocument.setDaysOfSickLeave(5);
        exam.setSickLeaveDocument(sickLeaveDocument);

        diagnosys = new Diagnosys();
        diagnosys.setName("Covid");
        exam.setDiagnosys(new HashSet<>(Set.of(diagnosys)));
    }

    @Test
    void testGetExams() {
        when(examRepository.findAll()).thenReturn(List.of(exam));

        List<Exam> exams = examService.getExams();

        assertNotNull(exams);
        assertEquals(1, exams.size());
        assertEquals("Dr. John Doe", exams.get(0).getDoctor().getName());
    }

    @Test
    void testGetExam() {
        when(examRepository.findById(1L)).thenReturn(Optional.of(exam));

        Exam foundExam = examService.getExam(1L);

        assertNotNull(foundExam);
        assertEquals("Dr. John Doe", foundExam.getDoctor().getName());
    }

    @Test
    void testGetExamNotFound() {
        when(examRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> examService.getExam(1L));
    }

    @Test
    void testCreateExam() {
        when(examRepository.save(exam)).thenReturn(exam);

        Exam createdExam = examService.createExam(exam);

        assertNotNull(createdExam);
        assertEquals("Dr. John Doe", createdExam.getDoctor().getName());
    }

    @Test
    void testCreateExamWithDTO() {
        NewExamDTO newExamDTO = new NewExamDTO();
        newExamDTO.setEGN("1234567890");
        newExamDTO.setUniqueDoctorCode("234");
        newExamDTO.setDiagnosys(Set.of("Covid"));
        newExamDTO.setConductedDate(LocalDate.now());
        newExamDTO.setSickLeaveDuration(5);
        newExamDTO.setSickLeaveStartDate(LocalDate.now());

        when(patientRepository.findByEGN(newExamDTO.getEGN())).thenReturn(Optional.of(patient));
        when(doctorRepository.findByUniqueDoctorCode(newExamDTO.getUniqueDoctorCode())).thenReturn(doctor);
        when(examRepository.save(any(Exam.class))).thenReturn(exam);
        when(diagnosysRepository.save(any(Diagnosys.class))).thenReturn(diagnosys);
        when(sickLeaveDocumentRepository.save(any(SickLeaveDocument.class))).thenReturn(sickLeaveDocument);

        Exam createdExam = examService.createExam(newExamDTO);

        assertNotNull(createdExam);
        assertEquals("Dr. John Doe", createdExam.getDoctor().getName());
        assertEquals(5, createdExam.getSickLeaveDocument().getDaysOfSickLeave());
    }

    @Test
    void testCreateExamPatientDoesNotExist() {
        NewExamDTO newExamDTO = new NewExamDTO();
        newExamDTO.setEGN("1234567890");
        newExamDTO.setUniqueDoctorCode("234");

        when(patientRepository.findByEGN(newExamDTO.getEGN())).thenReturn(Optional.empty());

        Exception exception = assertThrows(PatientDoesNotExistException.class, () -> examService.createExam(newExamDTO));
        assertEquals("A patient with this SSN does not exist!", exception.getMessage());
    }

    @Test
    void testUpdateExam() {
        when(examRepository.findById(1L)).thenReturn(Optional.of(exam));
        when(examRepository.save(exam)).thenReturn(exam);

        exam.setConductedDate(LocalDate.now().plusDays(1));
        Exam updatedExam = examService.updateExam(exam, 1L);

        assertNotNull(updatedExam);
        assertEquals(LocalDate.now().plusDays(1), updatedExam.getConductedDate());
    }

    @Test
    void testDeleteExam() {
        when(examRepository.findById(1L)).thenReturn(Optional.of(exam));
        doNothing().when(examRepository).delete(exam);

        examService.deleteExam(1L);

        verify(examRepository, times(1)).delete(exam);
    }

    @Test
    void testGetMostFrequentDiagnosys() {
        when(diagnosysRepository.findAll()).thenReturn(Stream.of(diagnosys).collect(Collectors.toList()));

        Diagnosys mostFrequentDiagnosys = examService.getMostFrequentDiagnosys();

        assertNotNull(mostFrequentDiagnosys);
        assertEquals("Covid", mostFrequentDiagnosys.getName());
    }

    @Test
    void testGetMonthWithMostSickLeaves() {
        SickLeaveDocument sickLeave1 = new SickLeaveDocument();
        sickLeave1.setStartDate(LocalDate.of(2025, Month.JANUARY, 1));
        sickLeave1.setDaysOfSickLeave(3);

        SickLeaveDocument sickLeave2 = new SickLeaveDocument();
        sickLeave2.setStartDate(LocalDate.of(2025, Month.JANUARY, 2));
        sickLeave2.setDaysOfSickLeave(2);

        when(sickLeaveDocumentRepository.findAll()).thenReturn(List.of(sickLeave1, sickLeave2));

        Month monthWithMostSickLeaves = examService.getMonthWithMostSickLeaves();

        assertNotNull(monthWithMostSickLeaves);
        assertEquals(Month.JANUARY, monthWithMostSickLeaves);
    }

    @Test
    void testGetDoctorWithMostSickLeaveDocuments() {
        Exam exam1 = new Exam();
        exam1.setDoctor(doctor);
        exam1.setSickLeaveDocument(sickLeaveDocument);

        Exam exam2 = new Exam();
        exam2.setDoctor(doctor);
        exam2.setSickLeaveDocument(sickLeaveDocument);

        when(examRepository.findAll()).thenReturn(List.of(exam1, exam2));
        when(doctorRepository.findByUniqueDoctorCode(doctor.getUniqueDoctorCode())).thenReturn(doctor);

        Doctor doctorWithMostSickLeaveDocuments = examService.getDoctorWithMostSickLeaveDocuments();

        assertNotNull(doctorWithMostSickLeaveDocuments);
        assertEquals("234", doctorWithMostSickLeaveDocuments.getUniqueDoctorCode());
    }
}
