package com.andreyprodromov.java.medical.service;

import com.andreyprodromov.java.medical.data.entity.User;
import com.andreyprodromov.java.medical.dto.CreateUserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    User create(User user);

    void create(CreateUserDTO userDTO);

    List<User> getAll();

    void deleteUser(Long id);
}
