package com.andreyprodromov.java.medical.web.controller;

import com.andreyprodromov.java.medical.data.entity.Doctor;
import com.andreyprodromov.java.medical.data.entity.Speciality;
import com.andreyprodromov.java.medical.data.entity.User;
import com.andreyprodromov.java.medical.dto.CreateUserDTO;
import com.andreyprodromov.java.medical.service.DoctorService;
import com.andreyprodromov.java.medical.service.PatientService;
import com.andreyprodromov.java.medical.service.UserService;
import com.andreyprodromov.java.medical.service.exceptions.NonExistentDoctorCodeException;
import com.andreyprodromov.java.medical.service.exceptions.UsernameAlreadyExistsException;
import com.andreyprodromov.java.medical.web.model.UserViewModel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final DoctorService doctorServiceService;
    private final PatientService patientService;

    @GetMapping("/manage-user")
    public String showUserCreationForm(Model model) {
        List<UserViewModel> users = userService.getAll()
                                      .stream()
                                      .map(u -> new UserViewModel(u.getId(), u.getUsername())).toList();
        model.addAttribute("userDTO", new CreateUserDTO());
        model.addAttribute("usersList", users);
        return "manage-user";
    }

    @PostMapping("/manage-user")
    public String createUserForm(@ModelAttribute("userDTO") CreateUserDTO userDTO, Model model) {
        try {
            userService.create(userDTO);
        } catch (NonExistentDoctorCodeException ex) {
            List<UserViewModel> users = userService.getAll()
                                                   .stream()
                                                   .map(u -> new UserViewModel(u.getId(), u.getUsername())).toList();

            model.addAttribute("usersList", users);
            model.addAttribute("error", "This doctor code does not exist!");
            return "manage-user";
        } catch (UsernameAlreadyExistsException ex) {
            List<UserViewModel> users = userService.getAll()
                                                   .stream()
                                                   .map(u -> new UserViewModel(u.getId(), u.getUsername())).toList();
            model.addAttribute("usersList", users);
            model.addAttribute("error", "Email already exists!");
            return "manage-user";
        }

        return "redirect:/manage-user";
    }

    @PostMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/manage-user";
    }

}