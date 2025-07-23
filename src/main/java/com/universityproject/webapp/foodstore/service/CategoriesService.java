package com.universityproject.webapp.foodstore.service;

import com.universityproject.webapp.foodstore.entity.Categories;

import java.util.List;
import java.util.Optional;

public interface CategoriesService {

    // Retrieve all categories
    List<Categories> getAllCategories();

    // Retrieve a category by its ID
    Optional<Categories> getCategoryById(int id);

    // Retrieve a category by its name
    Optional<Categories> getCategoryByName(String name);

    // Create or update a category
    Categories saveCategory(Categories category);

    // Delete a category by its ID
    void deleteCategory(int id);

    // Check if a category exists by its name
    boolean existsByCategoryName(String name);
}
