package com.universityproject.webapp.foodstore.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.universityproject.webapp.foodstore.entity.*;
import com.universityproject.webapp.foodstore.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
public class BillingServiceImpl implements BillingService {

    @Autowired
    private BillingRepository billingRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    private CartItemsRepository cartItemsRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional
    public Billing checkout(int userId, Billing.paymentMethod paymentMethod) {
        Cart cart = cartService.getCartByUserId(userId).orElseThrow();
        List<CartItems> items = cart.getCartItems();

        double totalAmount = items.stream()
                .mapToDouble(item -> item.getProduct().getProductPrice() * item.getQuantity())
                .sum();

        Billing billing = new Billing();
        billing.setCartId(cart);
        billing.setUser(cart.getUser()); // ← ربط مباشر بالمستخدم (مستحسن)
        billing.setTotalAmount(totalAmount);
        billing.setCreatedAt(Timestamp.from(Instant.now()));
        billing.setPaymentMethod(paymentMethod);
        billing.setPaymentStatus(Billing.paymentStatus.COMPLETED);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String snapshotJson = objectMapper.writeValueAsString(items); // items: List<ProductItemDetail>
            billing.setItemsSnapshot(snapshotJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // أو سجّله بطريقة مناسبة
            throw new RuntimeException("خطأ أثناء تحويل عناصر الفاتورة إلى JSON", e);
        }


        Billing saved = billingRepository.save(billing);
        cartItemsRepository.deleteAll(cart.getCartItems());


        return saved;
    }


    @Override
    public Billing save(Billing billing) {
        return billingRepository.save(billing);
    }
}