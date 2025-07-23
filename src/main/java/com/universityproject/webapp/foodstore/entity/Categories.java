package com.universityproject.webapp.foodstore.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jdk.jfr.Enabled;

import java.util.List;

@Entity
@Table(name = "categories")
public class Categories {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private int categoriesId;
    @Column(name = "category_name")
    private String categoriesName;
    @Column(name = "description_categories")
    private String categoriesDescription;
    @OneToMany(mappedBy = "categoryId")
    @JsonIgnore
    private List<Products> products;

    public Categories() {
    }

    public Categories(String categoriesName, String categoriesDescription) {
        this.categoriesName = categoriesName;
        this.categoriesDescription = categoriesDescription;
    }

    public List<Products> getProducts() {
        return products;
    }

    public void setProducts(List<Products> products) {
        this.products = products;
    }

    public int getCategoriesId() {
        return categoriesId;
    }

    public void setCategoriesId(int categoriesId) {
        this.categoriesId = categoriesId;
    }

    public String getCategoriesName() {
        return categoriesName;
    }

    public void setCategoriesName(String categoriesName) {
        this.categoriesName = categoriesName;
    }

    public String getCategoriesDescription() {
        return categoriesDescription;
    }

    public void setCategoriesDescription(String categoriesDescription) {
        this.categoriesDescription = categoriesDescription;
    }
}
