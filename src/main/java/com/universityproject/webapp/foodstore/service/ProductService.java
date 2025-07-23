package com.universityproject.webapp.foodstore.service;

import com.universityproject.webapp.foodstore.entity.Categories;
import com.universityproject.webapp.foodstore.entity.Products;
import com.universityproject.webapp.foodstore.entity.Users;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<Products> getAllProductsBySellerId(Users seller);
    Optional<Products> getProductByIdAndSellerId(int productId, Users seller);
    void deleteProductByIdAndSellerId(int productId, Users seller);
    List<Products> getProductsByCategoryAndSellerId(Categories categoryId, Users seller);
    Products saveProduct(Products product);
    List<Products> getAllProducts();
    List<Products> searchProductsByName(String name);

    List<Products> getProductsByCategory(Categories category);

}