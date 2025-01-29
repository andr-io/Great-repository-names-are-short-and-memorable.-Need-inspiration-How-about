package com.andreyprodromov.java.medical.service.impl;

import com.andreyprodromov.java.medical.data.entity.Diagnosys;
import com.andreyprodromov.java.medical.data.entity.Doctor;
import com.andreyprodromov.java.medical.data.entity.Exam;
import com.andreyprodromov.java.medical.data.entity.Patient;
import com.andreyprodromov.java.medical.data.entity.SickLeaveDocument;
import com.andreyprodromov.java.medical.data.repo.DiagnosysRepository;
import com.andreyprodromov.java.medical.data.repo.DoctorRepository;
import com.andreyprodromov.java.medical.data.repo.ExamRepository;
import com.andreyprodromov.java.medical.data.repo.PatientRepository;
import com.andreyprodromov.java.medical.data.repo.SickLeaveDocumentRepository;
import com.andreyprodromov.java.medical.dto.EditExamDTO;
import com.andreyprodromov.java.medical.dto.NewExamDTO;
import com.andreyprodromov.java.medical.service.ExamService;
import com.andreyprodromov.java.medical.service.exceptions.PatientDoesNotExistException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final SickLeaveDocumentRepository sickLeaveDocumentRepository;
    private final DiagnosysRepository diagnosysRepository;

    @Override
    public List<Exam> getExams() {
        return examRepository.findAll();
    }

    @Override
    public Exam getExam(long id) {
        return this.examRepository.findById(id)
                                    .orElseThrow(() -> new RuntimeException("Exam with id=" + id + " not found!" ));
    }

    @Override
    public Exam createExam(Exam exam) {
        return this.examRepository.save(exam);
    }

    @Override
    public Exam updateExam(Exam exam, long id) {
        return this.examRepository.findById(id)
                                    .map(exam1 -> {
                                        exam1.setDiagnosys(exam.getDiagnosys());
                                        exam1.setDoctor(exam.getDoctor());
                                        exam1.setPatient(exam.getPatient());
                                        exam1.setConductedDate(exam.getConductedDate());
                                        exam1.setSickLeaveDocument(exam.getSickLeaveDocument());
                                        return this.examRepository.save(exam1);
                                    }).orElseGet(()->
                this.examRepository.save(exam)
            );
    }

    @Override
    public void deleteExam(long id) {
        Exam exam = examRepository.findById(id).get();
        examRepository.delete(exam);
    }

    @Override
    public Exam createExam(NewExamDTO newExamDTO) {
        Optional<Patient> patient = patientRepository.findByEGN(newExamDTO.getEGN());
        Doctor doctor = doctorRepository.findByUniqueDoctorCode(newExamDTO.getUniqueDoctorCode());

        if (patient.isEmpty()) {
            throw new PatientDoesNotExistException("A patient with this SSN does not exist!");
        }

        Exam exam = examRepository.save(new Exam());
        exam.setConductedDate(newExamDTO.getConductedDate());
        exam.setPatient(patient.get());
        exam.setDoctor(doctor);

        Set<Diagnosys> diagnosysSet = new HashSet<>();
        for (var diagnosysName : newExamDTO.getDiagnosys()) {
            Optional<Diagnosys> diagnosysOptional = diagnosysRepository.findByName(diagnosysName);
            Diagnosys diagnosys;

            if (diagnosysOptional.isEmpty()) {
                diagnosys = new Diagnosys();
                diagnosys.setName(diagnosysName);
                diagnosys.setExam(new HashSet<>(){{add(exam);}});
                diagnosys = diagnosysRepository.save(diagnosys);
            } else {
                diagnosys = diagnosysOptional.get();
                diagnosys.getExam().add(exam);
                diagnosys = diagnosysRepository.save(diagnosys);
            }

            diagnosysSet.add(diagnosys);
        }

        exam.setDiagnosys(diagnosysSet);

        if (newExamDTO.getSickLeaveDuration() > 0) {
            SickLeaveDocument document = new SickLeaveDocument();
            document.setStartDate(newExamDTO.getSickLeaveStartDate());
            document.setDaysOfSickLeave(newExamDTO.getSickLeaveDuration());

            document.setExam(exam);
            exam.setSickLeaveDocument(document);

            sickLeaveDocumentRepository.save(document);
        }

        return examRepository.save(exam);
    }

    @Override
    public Exam editExam(EditExamDTO editExamDTO) {
        Optional<Patient> patient = patientRepository.findByEGN(editExamDTO.getEGN());
        Doctor doctor = doctorRepository.findByUniqueDoctorCode(editExamDTO.getUniqueDoctorCode());

        if (patient.isEmpty()) {
            throw new PatientDoesNotExistException("A patient with this SSN does not exist!");
        }

        Exam exam = examRepository.findById(editExamDTO.getId()).get();
        exam.setConductedDate(editExamDTO.getConductedDate());
        exam.setPatient(patient.get());
        exam.setDoctor(doctor);

        Set<Diagnosys> diagnosysSet = new HashSet<>();
        for (var diagnosysName : editExamDTO.getDiagnosys()) {
            Optional<Diagnosys> diagnosysOptional = diagnosysRepository.findByName(diagnosysName);
            Diagnosys diagnosys;

            if (diagnosysOptional.isEmpty()) {
                diagnosys = new Diagnosys();
                diagnosys.setName(diagnosysName);
                diagnosys.setExam(new HashSet<>(){{add(exam);}});
                diagnosys = diagnosysRepository.save(diagnosys);
            } else {
                diagnosys = diagnosysOptional.get();
                diagnosys.getExam().add(exam);
                diagnosys = diagnosysRepository.save(diagnosys);
            }

            diagnosysSet.add(diagnosys);
        }

        exam.setDiagnosys(diagnosysSet);

        if (editExamDTO.getSickLeaveDuration() > 0) {
            SickLeaveDocument document;
            if (exam.getSickLeaveDocument().getDaysOfSickLeave() == 0) {
                document = new SickLeaveDocument();
            } else {
                document = exam.getSickLeaveDocument();
            }

            document.setStartDate(editExamDTO.getSickLeaveStartDate());
            document.setDaysOfSickLeave(editExamDTO.getSickLeaveDuration());

            document.setExam(exam);
            exam.setSickLeaveDocument(document);

            sickLeaveDocumentRepository.save(document);
        }

        if (editExamDTO.getSickLeaveDuration() == 0) {
            SickLeaveDocument document = exam.getSickLeaveDocument();

            if (document != null && exam.getSickLeaveDocument().getDaysOfSickLeave() != 0) {
                sickLeaveDocumentRepository.deleteById(document.getId());
                document = null;
            }

            exam.setSickLeaveDocument(document);
        }

       return examRepository.save(exam);
    }

    @Override
    public Diagnosys getMostFrequentDiagnosys() {
        List<Diagnosys> diagnosis = diagnosysRepository.findAll();
        diagnosis.sort(Comparator.comparingInt(o -> o.getExam().size()));

        return diagnosis.get(diagnosis.size() - 1);
    }

    @Override
    public Month getMonthWithMostSickLeaves() {
        var documents = sickLeaveDocumentRepository.findAll();

        return documents.stream()
                 .collect(Collectors.groupingBy(
                     d -> d.getStartDate().getMonth(), // Group by month
                     Collectors.counting() // Count documents for each month
                 ))
                 .entrySet()
                 .stream()
                 .max(Map.Entry.comparingByValue())
                 .get().getKey();

    }

    @Override
    public Doctor getDoctorWithMostSickLeaveDocuments() {
        var exams = examRepository.findAll();

        String doctorCode = exams.stream()
                                 .filter(exam -> exam.getSickLeaveDocument() != null)
                                 .collect(Collectors.groupingBy(
                                     d -> d.getDoctor().getUniqueDoctorCode(), // Group by month
                                     Collectors.counting() // Count documents for each month
                                 ))
                                 .entrySet()
                                 .stream()
                                 .max(Map.Entry.comparingByValue())
                                 .get().getKey();

        return doctorRepository.findByUniqueDoctorCode(doctorCode);
    }
}
