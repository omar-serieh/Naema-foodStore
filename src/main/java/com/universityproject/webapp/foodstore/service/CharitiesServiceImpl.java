package com.universityproject.webapp.foodstore.service;

import com.universityproject.webapp.foodstore.entity.Charities;
import com.universityproject.webapp.foodstore.repository.CharitiesRepository;
import com.universityproject.webapp.foodstore.service.CharitiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CharitiesServiceImpl implements CharitiesService {

    @Autowired
    private CharitiesRepository charitiesRepository;

    @Override
    public List<Charities> getAllCharities() {
        return charitiesRepository.findAll();
    }

    @Override
    public Charities createCharity(Charities charity) {
        return charitiesRepository.save(charity);
    }

    @Override
    public void deleteCharityById(int charityId) {
        if (charitiesRepository.existsById(charityId)) {
            charitiesRepository.deleteById(charityId);
        } else {
            throw new IllegalArgumentException("Charity with ID " + charityId + " does not exist.");
        }
    }


}