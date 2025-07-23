package com.universityproject.webapp.foodstore.service;


import com.universityproject.webapp.foodstore.entity.Categories;
import com.universityproject.webapp.foodstore.repository.CategoriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriesServiceImpl implements CategoriesService {

    private final CategoriesRepository categoriesRepository;

    @Autowired
    public CategoriesServiceImpl(CategoriesRepository categoriesRepository) {
        this.categoriesRepository = categoriesRepository;
    }

    @Override
    public List<Categories> getAllCategories() {
        return categoriesRepository.findAll();
    }

    @Override
    public Optional<Categories> getCategoryById(int id) {
        return categoriesRepository.findById(id);
    }

    @Override
    public Optional<Categories> getCategoryByName(String name) {
        return categoriesRepository.findByCategoriesName(name);
    }

    @Override
    public Categories saveCategory(Categories category) {
        return categoriesRepository.save(category);
    }
    @Transactional
    @Override
    public void deleteCategory(int id) {
        if (categoriesRepository.existsById(id)) {
            categoriesRepository.deleteById(id);
        } else {
            throw new RuntimeException("Category with ID " + id + " not found.");
        }
    }

    @Override
    public boolean existsByCategoryName(String name) {
        return categoriesRepository.existsByCategoriesName(name);
    }
}
