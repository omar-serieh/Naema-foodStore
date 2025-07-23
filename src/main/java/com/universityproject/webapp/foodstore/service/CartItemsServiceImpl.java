package com.universityproject.webapp.foodstore.service;

import com.universityproject.webapp.foodstore.entity.Cart;
import com.universityproject.webapp.foodstore.entity.CartItems;
import com.universityproject.webapp.foodstore.entity.Products;
import com.universityproject.webapp.foodstore.entity.Users;
import com.universityproject.webapp.foodstore.repository.CartItemsRepository;
import com.universityproject.webapp.foodstore.repository.CartRepository;
import com.universityproject.webapp.foodstore.repository.ProductRepository;
import com.universityproject.webapp.foodstore.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartItemsServiceImpl implements CartItemsService {

    @Autowired
    private CartItemsRepository cartItemsRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productsRepository;
    @Autowired
    private UsersRepository userRepository;

    @Override
    public CartItems addItemToCart(int buyerId, int cartId, int productId, int quantity, double price) {
        // Fetch the cart by ID or create a new one if it doesn't exist
        Cart cart = cartRepository.findByUser_UserId(buyerId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(userRepository.findById(buyerId).orElseThrow(() -> new RuntimeException("User not found")));
                    newCart.setTotalPrice(0.0);
                    return cartRepository.save(newCart);
                });

        // Fetch the product by ID
        Products product = productsRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if the product's quantity is greater than zero
        if (product.getQuantity() <= 0) {
            throw new RuntimeException("Product is out of stock");
        }

        // Check if the product already exists in the cart
        Optional<CartItems> existingCartItemOpt = cartItemsRepository.findProductIdInCart(productId, buyerId);

        CartItems cartItem;
        if (existingCartItemOpt.isPresent()) {
            // If the product exists in the cart, update its quantity and price
            cartItem = existingCartItemOpt.get();

            // Ensure the quantity being added does not exceed the product's available quantity
            int newQuantity = cartItem.getQuantity() + Math.min(quantity, product.getQuantity());

            // Calculate the updated price for the cart item
            double updatedPrice = product.getProductPrice() * newQuantity;

            // Deduct the additional quantity from the product's available quantity
            int quantityAdded = newQuantity - cartItem.getQuantity();
            product.setQuantity(product.getQuantity() - quantityAdded);

            // Update the existing cart item's quantity and price
            cartItem.setQuantity(newQuantity);
            cartItem.setPrice(updatedPrice);

            // Update the cart's total price
            double updatedTotalPrice = cart.getTotalPrice() + (product.getProductPrice() * quantityAdded);
            cart.setTotalPrice(updatedTotalPrice);

        } else {
            // Ensure the quantity being added does not exceed the product's available quantity
            int actualQuantity = Math.min(quantity, product.getQuantity());

            // Calculate the total price for the new cart item
            double calculatedPrice = product.getProductPrice() * actualQuantity;

            // Deduct the actual quantity from the product's available quantity
            product.setQuantity(product.getQuantity() - actualQuantity);
            if (product.getQuantity() <= 0) {
                product.setAvailabilityStatus(false);
            }


            // Create a new cart item
            cartItem = new CartItems(actualQuantity, calculatedPrice);
            cartItem.setCart(cart);
            cartItem.setProduct(product);

            // Update the cart's total price
            double updatedTotalPrice = cart.getTotalPrice() + calculatedPrice;
            cart.setTotalPrice(updatedTotalPrice);
        }

        // Save the updated product quantity
        productsRepository.save(product);

        // Save the cart item to the database
        cartItemsRepository.save(cartItem);

        // Save the updated cart total price
        cartRepository.save(cart);

        return cartItem;
    }

    @Override
    public List<CartItems> getItemsByCartId(int cartId) {
        return cartItemsRepository.findByCart_CartId(cartId);
    }

    @Override
    public void removeItemFromCart(int cartItemId) {
        CartItems cartItem = cartItemsRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        Products product = cartItem.getProduct();
        Cart cart = cartItem.getCart();

        // ✅ رجع الكمية للمخزون
        int returnedQty = cartItem.getQuantity();
        product.setQuantity(product.getQuantity() + returnedQty);
        if (product.getQuantity() > 0) {
            product.setAvailabilityStatus(true);
        }


        // ✅ خصم السعر من السلة
        double deductedPrice = cartItem.getPrice();
        cart.setTotalPrice(Math.max(0, cart.getTotalPrice() - deductedPrice));

        // ✅ حفظ التغييرات
        productsRepository.save(product);
        cartRepository.save(cart);

        // ✅ حذف العنصر من السلة
        cartItemsRepository.deleteById(cartItemId);
    }


    @Override
    public void updateCartItemQuantity(int cartItemId, int newQuantity) {
        CartItems cartItem = cartItemsRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found"));

        Products product = cartItem.getProduct();
        Cart cart = cartItem.getCart();

        int oldQuantity = cartItem.getQuantity();
        int diff = newQuantity - oldQuantity;
        if (product.getQuantity() <= 0) {
            product.setAvailabilityStatus(false);
        }


        if (diff > 0) {
            // ✅ طلب زيادة: تحقق من الكمية المتاحة
            if (product.getQuantity() < diff) {
                throw new RuntimeException("Not enough stock to increase quantity");
            }
            product.setQuantity(product.getQuantity() - diff);
        } else if (diff < 0) {
            // ✅ طلب تقليل: رجع الكمية للمخزون
            product.setQuantity(product.getQuantity() + (-diff));
        }

        // ✅ تحديث الكمية والسعر
        cartItem.setQuantity(newQuantity);
        double newItemPrice = product.getProductPrice() * newQuantity;
        double oldItemPrice = cartItem.getPrice();
        cartItem.setPrice(newItemPrice);

        // ✅ تحديث سعر السلة
        double updatedCartPrice = cart.getTotalPrice() - oldItemPrice + newItemPrice;
        cart.setTotalPrice(updatedCartPrice);

        // ✅ حفظ كل التعديلات
        productsRepository.save(product);
        cartRepository.save(cart);
        cartItemsRepository.save(cartItem);
    }

}