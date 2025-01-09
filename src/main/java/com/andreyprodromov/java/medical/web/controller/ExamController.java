package com.andreyprodromov.java.medical.web.controller;

import com.andreyprodromov.java.medical.data.entity.Diagnosys;
import com.andreyprodromov.java.medical.data.entity.Doctor;
import com.andreyprodromov.java.medical.data.entity.Exam;
import com.andreyprodromov.java.medical.data.entity.Patient;
import com.andreyprodromov.java.medical.data.entity.User;
import com.andreyprodromov.java.medical.dto.EditExamDTO;
import com.andreyprodromov.java.medical.dto.EditPatientDTO;
import com.andreyprodromov.java.medical.dto.NewExamDTO;
import com.andreyprodromov.java.medical.service.DoctorService;
import com.andreyprodromov.java.medical.service.ExamService;
import com.andreyprodromov.java.medical.service.PatientService;
import com.andreyprodromov.java.medical.service.exceptions.NonExistentDoctorCodeException;
import com.andreyprodromov.java.medical.service.exceptions.PatientDoesNotExistException;
import com.andreyprodromov.java.medical.web.model.ExamViewModel;
import com.andreyprodromov.java.medical.web.model.PatientViewModel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class ExamController {

    private final ExamService examService;

    @GetMapping("/exams")
    public String showExams(@RequestParam(name="patientEmail", required = false) String patientEmail,
                            @RequestParam(name="doctorCode", required = false) String doctorCode,
                            @RequestParam(name="startDate", required = false) LocalDate startDate,
                            @RequestParam(name="endDate", required = false) LocalDate endDate,
                            @AuthenticationPrincipal User user,
                            Model model, HttpServletRequest request) {
        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);

        List<ExamViewModel> patients =
            examService.getExams()
                          .stream()
                          .filter(e -> patientEmail == null || e.getPatient().getUsername().equals(patientEmail))
                          .filter(e -> doctorCode == null || e.getDoctor().getUniqueDoctorCode().equals(doctorCode))
                          .filter(e -> startDate == null || endDate== null || e.getConductedDate().isAfter(startDate)&& e.getConductedDate().isBefore(endDate))
                          .map(e ->
                              new ExamViewModel(
                                  e.getId(),
                                  e.getDoctor().getUniqueDoctorCode(),
                                  e.getPatient().getEGN(),
                                  e.getConductedDate(),
                                  e.getDiagnosys().stream().map(Diagnosys::getName).collect(Collectors.toSet()),
                                  e.getSickLeaveDocument() == null ? null : e.getSickLeaveDocument().getStartDate(),
                                  e.getSickLeaveDocument() == null ? 0 : e.getSickLeaveDocument().getDaysOfSickLeave()
                              )
                          ).toList();

        if (user instanceof Patient patient) {
            patients = patients.stream()
                               .filter(p -> p.getPatientSSN().equals(patient.getEGN()))
                               .collect(Collectors.toList());
        }

        if (inputFlashMap != null) {
            String errorMessage = (String) inputFlashMap.get("error");

            if (errorMessage != null) {
                model.addAttribute("error", errorMessage);
            }
        }

        model.addAttribute("examsList", patients);
        model.addAttribute("newExamDTO", new NewExamDTO());
        model.addAttribute("editExamDTO", new EditExamDTO());

        if (user instanceof Doctor doctor) {
            model.addAttribute("doctorCode", doctor.getUniqueDoctorCode());
        }

        return "exams";
    }

    @PostMapping("/create-exam")
    public String createExam(@AuthenticationPrincipal User user, Model model, @ModelAttribute("newExamDTO") NewExamDTO newExamDTO, RedirectAttributes redirectAttributes) {
        try {
            if (user instanceof Doctor) {
                Doctor doctor = (Doctor) user;
                newExamDTO.setUniqueDoctorCode(doctor.getUniqueDoctorCode());
            }

            examService.createExam(newExamDTO);
        } catch (PatientDoesNotExistException ex) {
            redirectAttributes.addFlashAttribute("error", "A patient with this SSN does not exist!");
        }

        return "redirect:/exams";
    }

    @PostMapping("/edit-exam")
    public String editExam(@AuthenticationPrincipal User user, Model model, @ModelAttribute("editExamDTO") EditExamDTO editExamDTO, RedirectAttributes redirectAttributes) {
        if (user instanceof Doctor doctor) {
            if (!editExamDTO.getUniqueDoctorCode().equals(doctor.getUniqueDoctorCode())) {
                throw new RuntimeException("You're not the doctor that conducted this exam!");
            }
        }

       try {
           examService.editExam(editExamDTO);
       } catch (PatientDoesNotExistException ex) {
           redirectAttributes.addFlashAttribute("error", "A patient with this SSN does not exist!");
       }

        return "redirect:/exams";
    }

    @PostMapping("/delete-exam/{id}")
    public String deleteExam(@AuthenticationPrincipal User user, @PathVariable Long id) {
        if (user instanceof Doctor doctor) {
            Exam exam = examService.getExam(id);

            if (!exam.getDoctor().getUniqueDoctorCode().equals(doctor.getUniqueDoctorCode())) {
                throw new RuntimeException("You're not the doctor that conducted this exam!");
            }
        }

        examService.deleteExam(id);
        return "redirect:/exams";
    }
}
