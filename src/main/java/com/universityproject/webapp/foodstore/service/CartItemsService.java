package com.universityproject.webapp.foodstore.service;

import com.universityproject.webapp.foodstore.entity.CartItems;

import java.util.List;

public interface CartItemsService {
    CartItems addItemToCart(int buyerId,int cartId, int productId, int quantity, double price);
    List<CartItems> getItemsByCartId(int cartId);
    void removeItemFromCart(int cartItemId);
    void updateCartItemQuantity(int cartItemId, int newQuantity);
}