package com.andreyprodromov.java.medical.service.impl;

import com.andreyprodromov.java.medical.data.entity.Speciality;
import com.andreyprodromov.java.medical.data.repo.SpecialityRepository;
import com.andreyprodromov.java.medical.service.SpecialityService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SpecialityServiceImpl implements SpecialityService {

    private final SpecialityRepository specialityRepository;

    List<Speciality> getAll() {
        return specialityRepository.findAll();
    }
}
