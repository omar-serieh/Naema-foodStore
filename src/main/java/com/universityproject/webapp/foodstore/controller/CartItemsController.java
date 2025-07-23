package com.universityproject.webapp.foodstore.controller;

import com.universityproject.webapp.foodstore.entity.CartItems;
import com.universityproject.webapp.foodstore.entity.Users;
import com.universityproject.webapp.foodstore.service.CartItemsService;
import com.universityproject.webapp.foodstore.service.DonationsService;
import com.universityproject.webapp.foodstore.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart-items")
public class CartItemsController {

    @Autowired
    private CartItemsService cartItemsService;
    @Autowired
    private DonationsService donationsService;
    @Autowired
    private UsersService usersService;

    @PostMapping("/add")
    public CartItems addItemToCart(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CartItemRequest cartItemRequest) {
        Users user = usersService.getUserByEmail(userDetails.getUsername());
        return cartItemsService.addItemToCart(
                user.getUserId(),
                cartItemRequest.getCartId(),
                cartItemRequest.getProductId(),
                cartItemRequest.getQuantity(),
                0.0
        );
    }

    @GetMapping("/{cartId}")
    public List<CartItems> getItemsByCartId(@PathVariable int cartId) {
        return cartItemsService.getItemsByCartId(cartId);
    }

    @DeleteMapping("/{cartItemId}")
    public void removeItemFromCart(@PathVariable int cartItemId) {
        cartItemsService.removeItemFromCart(cartItemId);
    }

    @PutMapping("/{cartItemId}/update-quantity")
    public void updateCartItemQuantity(@PathVariable int cartItemId, @RequestBody CartItemRequest cartItemRequest) {
        cartItemsService.updateCartItemQuantity(cartItemId, cartItemRequest.getQuantity());
    }

    @PostMapping("/donate")
    public CartItems donateProduct(@AuthenticationPrincipal UserDetails userDetails, @RequestBody DonationRequest donationRequest) throws Exception {
        Users user = usersService.getUserByEmail(userDetails.getUsername());
        return donationsService.donateProduct(
                user.getUserId(),
                donationRequest.getProductId(),
                donationRequest.getQuantity(),
                donationRequest.getCharityId()
        );
    }

    public static class DonationRequest {
        private int productId;
        private int quantity;
        private int charityId;

        public int getCharityId() {
            return charityId;
        }

        public void setCharityId(int charityId) {
            this.charityId = charityId;
        }

        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }

    public static class CartItemRequest {
        private int cartId;
        private int productId;
        private int quantity;

        public int getCartId() {
            return cartId;
        }

        public void setCartId(int cartId) {
            this.cartId = cartId;
        }

        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}