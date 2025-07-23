package com.universityproject.webapp.foodstore.controller;

import com.universityproject.webapp.foodstore.entity.Categories;
import com.universityproject.webapp.foodstore.service.CategoriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
public class CategoriesController {

    private final CategoriesService categoriesService;

    @Autowired
    public CategoriesController(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    @GetMapping
    public List<Categories> getAllCategories() {
        return categoriesService.getAllCategories();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categories> getCategoryById(@PathVariable int id) {
        Optional<Categories> category = categoriesService.getCategoryById(id);
        return category.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Categories createCategory(@RequestBody Categories category) {
        return categoriesService.saveCategory(category);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categories> updateCategory(@PathVariable int id, @RequestBody Categories updatedCategory) {
        if (!categoriesService.getCategoryById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        updatedCategory.setCategoriesId(id);
        return ResponseEntity.ok(categoriesService.saveCategory(updatedCategory));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable int id) {
        if (!categoriesService.getCategoryById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        categoriesService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
