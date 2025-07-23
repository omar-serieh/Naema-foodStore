package com.universityproject.webapp.foodstore.repository;

import com.universityproject.webapp.foodstore.entity.Cart;
import com.universityproject.webapp.foodstore.entity.CartItems;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemsRepository extends JpaRepository<CartItems, Integer> {

    @Query("SELECT ci FROM CartItems ci WHERE ci.cart.cartId = :cartId")
    List<CartItems> findByCart_CartId(@Param("cartId") int cartId);
    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE cart_items  AUTO_INCREMENT = 1", nativeQuery = true)
    void resetAutoIncrement();
    @Query("SELECT ci FROM CartItems ci WHERE ci.cart.user.userId = :userId AND ci.product.productId = :productId AND ci.donation IS NULL")
    Optional<CartItems> findProductIdInCart(@Param("productId") int productId, @Param("userId") int userId);

    @Query("""
    SELECT ci FROM CartItems ci 
    WHERE ci.cart.user.userId = :userId 
      AND ci.product.productId = :productId 
      AND ci.donation.charitytId.charityId = :charityId
""")
    Optional<CartItems> findDonationItemInCart(
            @Param("userId") int userId,
            @Param("productId") int productId,
            @Param("charityId") int charityId
    );

}