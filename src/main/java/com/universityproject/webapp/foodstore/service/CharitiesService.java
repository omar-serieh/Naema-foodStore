package com.universityproject.webapp.foodstore.service;

import com.universityproject.webapp.foodstore.entity.Charities;

import java.util.List;

public interface CharitiesService {
    List<Charities> getAllCharities();
    Charities createCharity(Charities charity);
    void deleteCharityById(int charityId);
}