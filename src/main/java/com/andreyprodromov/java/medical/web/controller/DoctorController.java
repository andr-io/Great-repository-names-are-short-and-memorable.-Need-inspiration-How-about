package com.andreyprodromov.java.medical.web.controller;

import com.andreyprodromov.java.medical.data.entity.Speciality;
import com.andreyprodromov.java.medical.dto.EditDoctorDTO;
import com.andreyprodromov.java.medical.service.DoctorService;
import com.andreyprodromov.java.medical.web.model.DoctorViewModel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;


import java.util.List;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping("/doctors")
    public String showDoctors(Model model) {
        List<DoctorViewModel> doctors =
            doctorService.getDoctors()
                         .stream()
                         .map(d ->
                             new DoctorViewModel(
                                 d.getId(),
                                 d.getName(),
                                 d.getUsername(),
                                 d.getUniqueDoctorCode(),
                                 d.getSpeciality(),
                                 d.getSpeciality().stream().map(Speciality::getName).collect(Collectors.joining(","))
                             )
                         ).toList();
        model.addAttribute("doctorsList", doctors);
        model.addAttribute("editDoctorDTO", new EditDoctorDTO());
        return "doctors";
    }

    @PostMapping("/edit-doctor")
    public String editDoctor(@ModelAttribute("editDoctorDTO") EditDoctorDTO editDoctorDTO) {
        doctorService.updateDoctor(editDoctorDTO);
        return "redirect:/doctors";
    }

    @PostMapping("/delete-doctor/{id}")
    public String deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return "redirect:/doctors";
    }
}
