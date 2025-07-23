package com.universityproject.webapp.foodstore.controller;

import com.universityproject.webapp.foodstore.entity.Cart;
import com.universityproject.webapp.foodstore.entity.Users;
import com.universityproject.webapp.foodstore.service.CartService;
import com.universityproject.webapp.foodstore.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private UsersService usersService;
    @Autowired
    private CartService cartService;

    @PostMapping("/create")
    public Cart createCart(@AuthenticationPrincipal UserDetails userDetails) {
        Users user = usersService.getUserByEmail(userDetails.getUsername());
        return cartService.createCart(user.getUserId());
    }


    @GetMapping("/{cartId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Cart> getCartById(@PathVariable int cartId) {
        Cart cart = cartService.getCartById(cartId);
        return ResponseEntity.ok(cart);
    }

    @GetMapping("/me")
    public ResponseEntity<Cart> getCartByUser(@AuthenticationPrincipal UserDetails userDetails) {
        Users user = usersService.getUserByEmail(userDetails.getUsername());
        Cart cart = cartService.getCartByUserId(user.getUserId()).orElse(null);
        return ResponseEntity.ok(cart);
    }
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Cart> getAllCarts() {
        return cartService.getAllCarts();
    }

    @DeleteMapping("/{cartId}/cancel")
    public void cancelCart(@PathVariable int cartId) {
        cartService.cancelCart(cartId);
    }
    @DeleteMapping("/{cartId}/clean")
    public ResponseEntity<String> cleanCart(@PathVariable int cartId) {
        cartService.cleanCart(cartId);
        return ResponseEntity.ok("Cart cleaned successfully.");
    }
    @DeleteMapping("/{cartId}")
    public void deleteCart(@PathVariable int cartId) {
        cartService.deleteCart(cartId);
    }
}