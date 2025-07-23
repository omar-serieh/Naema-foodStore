package com.universityproject.webapp.foodstore.repository;

import com.universityproject.webapp.foodstore.entity.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Integer> {
    Optional<Categories> findByCategoriesName(String categoriesName);
    boolean existsByCategoriesName(String categoriesName);
}


