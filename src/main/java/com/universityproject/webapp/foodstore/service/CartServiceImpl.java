package com.universityproject.webapp.foodstore.service;

import com.universityproject.webapp.foodstore.entity.*;
import com.universityproject.webapp.foodstore.repository.CartItemsRepository;
import com.universityproject.webapp.foodstore.repository.CartRepository;
import com.universityproject.webapp.foodstore.repository.ProductRepository;
import com.universityproject.webapp.foodstore.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemsRepository cartItemsRepository;

    @Override
    public Cart createCart(int userId) {
        Cart cart = new Cart();
        cart.setUser(usersRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")));
        cart.setTotalPrice(0.0);
        cart.setCreatedAt(Timestamp.from(Instant.now())); // Set createdAt
        cart.setUpdatedAt(Timestamp.from(Instant.now())); // Set updatedAt
        return cartRepository.save(cart);
    }

    @Override
    public void updateCartTotalPrice(int cartId, double newTotalPrice) {
        Cart cart = getCartById(cartId);
        cart.setTotalPrice(newTotalPrice);
        cart.setUpdatedAt(Timestamp.from(Instant.now())); // Set updatedAt
        cartRepository.save(cart);
    }

    @Override
    public Cart getCartById(int cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new RuntimeException("Cart not found"));

        // Add priceAfterDiscount to the cart JSON
        double priceAfterDiscount = calculateDiscountedPrice(cart);
        cart.setPriceAfterDiscount(priceAfterDiscount);

        return cart;
    }

    @Override
    public Optional<Cart> getCartByUserId(int userId) {
        Optional<Cart> optionalCart = cartRepository.findByUser_UserId(userId);

        optionalCart.ifPresent(cart -> {
            // Add priceAfterDiscount to the cart JSON
            double priceAfterDiscount = calculateDiscountedPrice(cart);
            cart.setPriceAfterDiscount(priceAfterDiscount);
        });

        return optionalCart;
    }

    @Override
    public List<Cart> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();

        // Calculate priceAfterDiscount for all carts
        carts.forEach(cart -> {
            double priceAfterDiscount = calculateDiscountedPrice(cart);
            cart.setPriceAfterDiscount(priceAfterDiscount);
        });

        return carts;
    }

    @Override
    public void cancelCart(int cartId) {
        Cart cart = getCartById(cartId);

        // Iterate over all cart items in the cart
        for (CartItems cartItem : cart.getCartItems()) {
            // Get the product associated with the cart item
            Products product = cartItem.getProduct();

            // Add the cart item's quantity back to the product's available quantity
            product.setQuantity(product.getQuantity() + cartItem.getQuantity());

            // Save the updated product
            productRepository.save(product);

            // Delete the cart item
            cartItemsRepository.delete(cartItem);
        }
        cartItemsRepository.resetAutoIncrement();
        // Finally, delete the cart
        cartRepository.delete(cart);
    }

    @Override
    @Transactional
    public void cleanCart(int cartId) {
        Cart cart = getCartById(cartId);

        List<CartItems> cartItems = cartItemsRepository.findByCart_CartId(cartId);

        for (CartItems cartItem : cartItems) {
            Products product = cartItem.getProduct();
            product.setQuantity(product.getQuantity() + cartItem.getQuantity());
            product.setAvailabilityStatus(true); // ✅ إعادة التوفر
            productRepository.save(product);
            cartItemsRepository.delete(cartItem);
        }

        cart.setTotalPrice(0.0);
        cartRepository.save(cart);

        cartItemsRepository.resetAutoIncrement(); // إذا فعلاً عندك هيك ميثود
    }


    @Override
    public void deleteCart(int cartId) {
        Cart cart = getCartById(cartId);
        cartItemsRepository.resetAutoIncrement();
        cartRepository.delete(cart);
        cartRepository.resetAutoIncrementcart();
    }


    private double calculateDiscountedPrice(Cart cart) {
        Users user = cart.getUser();

        // Check if the user has an active subscription
        boolean hasSubscription = user.isSubscriptionStatus();

        // Apply discount if the user has a subscription
        if (hasSubscription) {
            return cart.getTotalPrice() * 0.85;
        }

        // No discount applied
        return cart.getTotalPrice();
    }
}