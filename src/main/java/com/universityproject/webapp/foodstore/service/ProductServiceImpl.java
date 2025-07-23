package com.universityproject.webapp.foodstore.service;

import com.universityproject.webapp.foodstore.entity.Categories;
import com.universityproject.webapp.foodstore.entity.Products;
import com.universityproject.webapp.foodstore.entity.Users;
import com.universityproject.webapp.foodstore.repository.ProductRepository;
import com.universityproject.webapp.foodstore.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UsersRepository usersRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, UsersRepository usersRepository) {
        this.productRepository = productRepository;
        this.usersRepository = usersRepository;
    }

    @Override
    public List<Products> getAllProductsBySellerId(Users seller) {
        return productRepository.findAllBySellerId(seller);
    }

    @Override
    public Optional<Products> getProductByIdAndSellerId(int productId, Users seller) {
        return productRepository.findByIdAndSellerId(productId, seller);
    }

    @Override
    public Products saveProduct(Products product) {
        // Validate that the seller exists in the database
        if (product.getSellerId() == null || product.getSellerId().getUserId() == 0) {
            throw new RuntimeException("Seller information is required to save the product.");
        }

        // Fetch the seller from the database
        Users seller = usersRepository.findById(product.getSellerId().getUserId())
                .orElseThrow(() -> new RuntimeException("Seller not found."));

        // Validate the seller's role
        if (seller.getRoleId() == null || !"Seller".equalsIgnoreCase(seller.getRoleId().getRoleName())) {
            throw new RuntimeException("Only users with the role of 'Seller' can create or update products.");
        }

        // Assign the fetched seller to the product
        product.setSellerId(seller);

        // Save the product
        return productRepository.save(product);
    }
    @Transactional
    @Override
    public void deleteProductByIdAndSellerId(int productId, Users seller) {
        // Use the repository method to delete the product
        productRepository.deleteByIdAndSellerId(productId, seller);
    }

    @Override
    public List<Products> getProductsByCategoryAndSellerId(Categories categoryId, Users seller) {
        return productRepository.findAllByCategoryAndSellerId(categoryId, seller);
    }

    @Override
    public List<Products> getAllProducts() {
            return productRepository.findAll();
    }
    @Override
    public List<Products> searchProductsByName(String name) {
        return productRepository.searchByName(name);
    }

    @Override
    public List<Products> getProductsByCategory(Categories category) {
        return productRepository.findAllByCategory(category);
    }

}
