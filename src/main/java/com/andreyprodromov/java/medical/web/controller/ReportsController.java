package com.andreyprodromov.java.medical.web.controller;

import com.andreyprodromov.java.medical.data.entity.Diagnosys;
import com.andreyprodromov.java.medical.data.entity.Doctor;
import com.andreyprodromov.java.medical.data.entity.Patient;
import com.andreyprodromov.java.medical.service.DoctorService;
import com.andreyprodromov.java.medical.service.ExamService;
import com.andreyprodromov.java.medical.service.PatientService;
import com.andreyprodromov.java.medical.web.model.ReportDoctorViewModel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@AllArgsConstructor
public class ReportsController {

    private final ExamService examService;
    private final DoctorService doctorService;
    private final PatientService patientService;

    @GetMapping("/reports")
    public String showReports() {
        return "reports";
    }

    @GetMapping("/reports/most-frequent-diagnosis")
    public String getMostFrequentDiagnosis(Model model) {
        Diagnosys mostFrequent = examService.getMostFrequentDiagnosys();
        model.addAttribute("mostFrequentDiagnosis", mostFrequent.getName());
        return "reports";
    }

    @GetMapping("/reports/patient-count-per-doctor")
    public String getPatientCountPerDoctor(Model model) {
        List<Patient> patients = patientService.getPatients();
        List<ReportDoctorViewModel> doctors = doctorService.getDoctors()
                                            .stream()
                                            .map(d -> new ReportDoctorViewModel(
                                                d.getUniqueDoctorCode(), patients.stream()
                                                                                 .filter(p -> p.getPersonalDoctor().getUniqueDoctorCode().equals(d.getUniqueDoctorCode()))
                                                .count()
                                            )).toList();

        model.addAttribute("reportDoctors", doctors);

        return "reports";
    }

    @GetMapping("/reports/month-with-most-sick-leave")
    public String getMonthWithMostSickLeave(Model model) {
        String monthWithMostSickLeave = examService.getMonthWithMostSickLeaves().toString();

        model.addAttribute("monthWithMostSickLeave", monthWithMostSickLeave);

        return "reports";
    }

    @GetMapping("/reports/doctors-most-sick-leave")
    public String getDoctorsMostSickLeave(Model model) {
        Doctor doctor = examService.getDoctorWithMostSickLeaveDocuments();

        model.addAttribute("doctorsMostSickLeave", doctor.getUniqueDoctorCode());

        return "reports";
    }
}
