package com.andreyprodromov.java.medical.web.controller;

import com.andreyprodromov.java.medical.data.entity.Diagnosys;
import com.andreyprodromov.java.medical.data.entity.Exam;
import com.andreyprodromov.java.medical.data.entity.Patient;
import com.andreyprodromov.java.medical.data.entity.Speciality;
import com.andreyprodromov.java.medical.dto.EditDoctorDTO;
import com.andreyprodromov.java.medical.dto.EditPatientDTO;
import com.andreyprodromov.java.medical.service.PatientService;
import com.andreyprodromov.java.medical.service.exceptions.NonExistentDoctorCodeException;
import com.andreyprodromov.java.medical.web.model.DoctorViewModel;
import com.andreyprodromov.java.medical.web.model.PatientViewModel;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @GetMapping("/patients")
    public String showPatients(@RequestParam(name = "diagnosis", required = false) String diagnosis, @RequestParam(name="doctorCode", required = false) String doctorCode, Model model, HttpServletRequest request) {
        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        List<Patient> patientList = patientService.getPatients();

        if (diagnosis != null) {
            patientList = patientList.stream().filter(patient -> {
                var exams = patient.getExams();
                for (Exam exam : exams) {
                    for (Diagnosys diagnosys : exam.getDiagnosys()) {
                        if (diagnosys.getName().equals(diagnosis)) {
                            return true;
                        }
                    }
                }
                return false;
            }).toList();
        }

        if (doctorCode != null) {
            patientList = patientList.stream().filter(patient ->
               patient.getPersonalDoctor().getUniqueDoctorCode().equals(doctorCode)
            ).toList();
        }

        List<PatientViewModel> patients =
                patientList.stream()
                         .map(p ->
                             new PatientViewModel(
                                 p.getId(),
                                 p.getName(),
                                 p.getUsername(),
                                 p.getEGN(),
                                 p.isHasPaidHisInsuranceInTheLastSixMonths(),
                                 p.getPersonalDoctor().getUniqueDoctorCode()
                             )
                         ).toList();

        if (inputFlashMap != null) {
            String errorMessage = (String) inputFlashMap.get("error");

            if (errorMessage != null) {
                model.addAttribute("error", errorMessage);
            }
        }

        model.addAttribute("patientsList", patients);
        model.addAttribute("editPatientDTO", new EditPatientDTO());
        return "patients";
    }



    @PostMapping("/edit-patient")
    public String editPatient(Model model, @ModelAttribute("editPatientDTO") EditPatientDTO editPatientDTO, RedirectAttributes redirectAttributes) {
        try {
            patientService.updatePatient(editPatientDTO);
        } catch (NonExistentDoctorCodeException ex) {
            redirectAttributes.addFlashAttribute("error", "This doctor code does not exist!");
        }

        return "redirect:/patients";
    }

    @PostMapping("/delete-patient/{id}")
    public String deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return "redirect:/patients";
    }

}
