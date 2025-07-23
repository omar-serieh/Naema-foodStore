package com.universityproject.webapp.foodstore.service;

import com.universityproject.webapp.foodstore.entity.*;
import com.universityproject.webapp.foodstore.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class DonationsServiceImpl implements DonationsService {

    @Autowired
    private CartItemsRepository cartItemsRepository;

    @Autowired
    private DonationsRepository donationsRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UsersRepository userRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CharitiesRepository charitiesRepository;

    @Override
    public CartItems donateProduct(int buyerId, int productId, int quantity, int charityId) throws Exception {
        // جلب أو إنشاء السلة
        Cart cart = cartRepository.findByUser_UserId(buyerId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(userRepository.findById(buyerId)
                            .orElseThrow(() -> new RuntimeException("User not found")));
                    newCart.setTotalPrice(0.0);
                    return cartRepository.save(newCart);
                });

        // جلب المنتج
        Products product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // تحقق من التوفر
        if (product.getQuantity() <= 0) {
            throw new RuntimeException("Product is out of stock");
        }

        // جلب الجمعية
        Charities charity = charitiesRepository.findById(charityId)
                .orElseThrow(() -> new RuntimeException("Charity not found"));

        // تحقق إذا التبرع موجود سابقاً
        Optional<CartItems> existingDonationOpt = cartItemsRepository
                .findDonationItemInCart(buyerId, productId, charityId);

        CartItems donationCartItem;
        int actualQuantity;

        if (existingDonationOpt.isPresent()) {
            donationCartItem = existingDonationOpt.get();
            actualQuantity = Math.min(quantity, product.getQuantity());

            int newQuantity = donationCartItem.getQuantity() + actualQuantity;
            donationCartItem.setQuantity(newQuantity);
            donationCartItem.setPrice(product.getProductPrice() * newQuantity);
        } else {
            actualQuantity = Math.min(quantity, product.getQuantity());

            // إنشاء التبرع
            Donations donation = new Donations();
            donation.setDonationDate(new Date());
            donation.setCharitytId(charity);
            donationsRepository.save(donation);

            // إنشاء العنصر
            donationCartItem = new CartItems();
            donationCartItem.setQuantity(actualQuantity);
            donationCartItem.setPrice(product.getProductPrice() * actualQuantity); // أو 0.0 إذا بدك تبرع حقيقي
            donationCartItem.setProduct(product);
            donationCartItem.setDonation(donation);
            donationCartItem.setCart(cart);
        }

        // خصم الكمية من المنتج
        product.setQuantity(product.getQuantity() - actualQuantity);
        productRepository.save(product);

        // تحديث السعر الكلي للسلة
        double updatedTotalPrice = cart.getTotalPrice() + (product.getProductPrice() * actualQuantity);
        cart.setTotalPrice(updatedTotalPrice);
        cartRepository.save(cart);

        return cartItemsRepository.save(donationCartItem);
    }

}