package com.universityproject.webapp.foodstore.service;

import com.universityproject.webapp.foodstore.entity.Cart;

import java.util.List;
import java.util.Optional;

public interface CartService {
    Cart createCart(int userId);
    Cart getCartById(int cartId);
    Optional<Cart> getCartByUserId(int userId);
    void updateCartTotalPrice(int cartId, double newTotalPrice);
    List<Cart> getAllCarts();
    void cancelCart(int cartId); // Added method
    void cleanCart(int cartId);
    void deleteCart(int cartId);
}