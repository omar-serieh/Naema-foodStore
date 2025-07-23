package com.universityproject.webapp.foodstore.repository;

import com.universityproject.webapp.foodstore.entity.Categories;
import com.universityproject.webapp.foodstore.entity.Products;
import com.universityproject.webapp.foodstore.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Products, Integer> {
    @Query("SELECT p FROM Products p WHERE p.sellerId = :seller")
    List<Products> findAllBySellerId(@Param("seller") Users seller);

    @Query("SELECT p FROM Products p WHERE p.productId = :productId AND p.sellerId = :seller")
    Optional<Products> findByIdAndSellerId(@Param("productId") int productId, @Param("seller") Users seller);

    @Modifying
    @Query("DELETE FROM Products p WHERE p.productId = :productId AND p.sellerId = :seller")
    void deleteByIdAndSellerId(@Param("productId") int productId, @Param("seller") Users seller);

    @Query("SELECT p FROM Products p WHERE p.categoryId = :categoryId AND p.sellerId = :seller")
    List<Products> findAllByCategoryAndSellerId(@Param("categoryId") Categories categoryId, @Param("seller") Users seller);
    @Query("SELECT p FROM Products p WHERE p.categoryId = :categoryId ")
    List<Products> findAllByCategory(@Param("categoryId") Categories categoryId);

    // Find products by name (case-insensitive)
    @Query("SELECT p FROM Products p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :productName, '%'))")
    List<Products> findByNameContainingIgnoreCase(@Param("productName") String productName);

    // Find all products by category
    @Query("SELECT p FROM Products p WHERE p.categoryId = :category")
    List<Products> findAllByCategoryId(@Param("category") Categories category);

    // Find products by category and name (case-insensitive)
    @Query("SELECT p FROM Products p WHERE p.categoryId = :category AND LOWER(p.productName) LIKE LOWER(CONCAT('%', :productName, '%'))")
    List<Products> findByCategoryIdAndNameContainingIgnoreCase(@Param("category") Categories category, @Param("productName") String productName);
    @Query("SELECT p FROM Products p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Products> searchByName(@Param("keyword") String keyword);

}