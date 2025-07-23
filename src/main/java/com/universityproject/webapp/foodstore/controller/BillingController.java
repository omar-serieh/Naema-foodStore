package com.universityproject.webapp.foodstore.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.universityproject.webapp.foodstore.entity.*;
import com.universityproject.webapp.foodstore.repository.BillingRepository;
import com.universityproject.webapp.foodstore.repository.UsersRepository;
import com.universityproject.webapp.foodstore.service.BillingService;
import com.universityproject.webapp.foodstore.service.NotificationService;
import com.universityproject.webapp.foodstore.service.UsersService;
import com.universityproject.webapp.foodstore.service.SubscriptionsService;
import com.universityproject.webapp.foodstore.repository.PointsSystemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    @Autowired
    private BillingService billingService;
    @Autowired
    private BillingRepository billingRepository;

    @Autowired
    private UsersService usersService;
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private SubscriptionsService subscriptionsService;

    @Autowired
    private PointsSystemRepository pointsSystemRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private ObjectMapper objectMapper;


    public static class CheckoutRequest {
        private String paymentMethod;
        private double amount;

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;

        }
    }

    private double rewardPoints(Users user, double amount) {
        if (!user.isSubscriptionStatus()) return 0;  // النقاط فقط للمشتركين

        double earnedPoints =  amount / 1000;
        if (earnedPoints > 0) {
            user.setPoints(user.getPoints() + earnedPoints);
            usersService.saveUser(user);
            PointsSystem entry = new PointsSystem();
            entry.setUserId(user);
            entry.setPointsEarned(earnedPoints);
            entry.setDate(new Date());
            pointsSystemRepository.save(entry);
        }
        return earnedPoints;
    }

    @PostMapping("/checkout")
    public ResponseEntity<BillingResponseDTO> checkout(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CheckoutRequest checkoutRequest
    ) {
        Users user = usersService.getUserByEmail(userDetails.getUsername());
        Billing.paymentMethod method = Billing.paymentMethod.valueOf(checkoutRequest.getPaymentMethod().toUpperCase());

        if (method == Billing.paymentMethod.CASH && !user.isSubscriptionStatus()) {
            return ResponseEntity.badRequest().body(null);
        }

        Billing billing = billingService.checkout(user.getUserId(), method);

        Map<Integer, Double> sellerTotals = new HashMap<>();
        Map<Integer, Users> sellerMap = new HashMap<>();

        for (CartItems item : billing.getCartId().getCartItems()) {
            Users seller = item.getProduct().getSellerId();
            int sellerId = seller.getUserId();
            double itemTotal = item.getProduct().getProductPrice() * item.getQuantity();

            sellerTotals.merge(sellerId, itemTotal, Double::sum);
            sellerMap.putIfAbsent(sellerId, seller);
        }

        for (Map.Entry<Integer, Double> entry : sellerTotals.entrySet()) {
            Users seller = sellerMap.get(entry.getKey());
            double total = entry.getValue();
            seller.setRevenue(seller.getRevenue() + total);
            usersRepository.save(seller);

            String message = String.format("Your products have been sold for a total of %.2f SYP in a new order.", total);
            notificationService.createNotification(
                    new Notification(
                            seller,
                            "📦 New Order Received",
                            message,
                            Notification.NotificationType.INFO
                    )
            );
        }

        List<BillingResponseDTO.ProductItemDetail> itemDetails = billing.getCartId().getCartItems().stream()
                .map(item -> {
                    Products product = item.getProduct();

                    String productName = (product != null && product.getProductName() != null)
                            ? product.getProductName()
                            : "منتج غير متوفر";

                    double price = (product != null) ? product.getProductPrice() : 0.0;

                    return new BillingResponseDTO.ProductItemDetail(
                            productName,
                            item.getQuantity(),
                            price
                    );
                }).collect(Collectors.toList());

        double earnedPoints = rewardPoints(user, billing.getTotalAmount());
        billing.setEarnedPoints(earnedPoints);

        try {
            String snapshotJson = objectMapper.writeValueAsString(itemDetails);
            billing.setItemsSnapshot(snapshotJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        billingRepository.save(billing);

        notificationService.createNotification(
                new Notification(
                        user,
                        "✅ Payment Successful",
                        "Thank you for shopping with us! Your order has been confirmed.",
                        Notification.NotificationType.INFO
                )
        );

        notificationService.createNotification(
                new Notification(
                        user,
                        "🎉 You've earned " + earnedPoints + " points!",
                        "Points have been added to your account. Enjoy your rewards!",
                        Notification.NotificationType.POINTS
                )
        );

        // -------- إعداد الرد --------
        BillingResponseDTO response = new BillingResponseDTO();
        response.setBuyerName(user.getUserName());
        response.setTotalAmount(billing.getTotalAmount());
        response.setPaymentMethod(billing.getPaymentMethod().name());
        response.setPaymentStatus(billing.getPaymentStatus().name());
        response.setCreatedAt(billing.getCreatedAt().toString());
        response.setEarnedPoints(earnedPoints);
        response.setItems(itemDetails);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/subscribe")
    public ResponseEntity<BillingResponseDTO> subscribe(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CheckoutRequest checkoutRequest
    ) {
        Users user = usersService.getUserByEmail(userDetails.getUsername());
        Billing.paymentMethod method = Billing.paymentMethod.valueOf(checkoutRequest.getPaymentMethod().toUpperCase());

        if (method == Billing.paymentMethod.CASH) {
            return ResponseEntity.badRequest().body(null);
        }

        subscriptionsService.extendSubscription(user.getUserId());
        user = usersService.getUserById(user.getUserId()); // Refresh user with updated subscription

        Billing billing = new Billing();
        billing.setTotalAmount(30000);
        billing.setCreatedAt(new Timestamp(new Date().getTime()));
        billing.setPaymentMethod(method);
        billing.setPaymentStatus(Billing.paymentStatus.COMPLETED);
        billing.setUser(user);

        double earnedPoints = rewardPoints(user, billing.getTotalAmount());
        billing.setEarnedPoints(earnedPoints);
        billingService.save(billing);

        notificationService.createNotification(
                new Notification(
                        user,
                        "✅ Subscription Activated",
                        "Your subscription has been successfully activated. Enjoy full access!",
                        Notification.NotificationType.SUBSCRIPTION
                )
        );

        notificationService.createNotification(
                new Notification(
                        user,
                        "🎉 You've earned " + earnedPoints + " points!",
                        "Points have been added to your account. Enjoy your rewards!",
                        Notification.NotificationType.POINTS
                )
        );

        BillingResponseDTO response = new BillingResponseDTO();
        response.setBuyerName(user.getUserName());
        response.setTotalAmount(billing.getTotalAmount());
        response.setPaymentMethod(billing.getPaymentMethod().name());
        response.setPaymentStatus(billing.getPaymentStatus().name());
        response.setCreatedAt(billing.getCreatedAt().toString());
        response.setEarnedPoints(earnedPoints);
        response.setItems(Collections.singletonList(
                new BillingResponseDTO.SubscriptionItemDetail("Subscription", 30000)
        ));

        return ResponseEntity.ok(response);
    }


    @PostMapping("/pay-by-points")
    public ResponseEntity<?> payByPoints(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Users user = usersService.getUserByEmail(userDetails.getUsername());
        Billing billing = billingService.checkout(user.getUserId(), Billing.paymentMethod.BY_POINTS);

        double totalAmount = billing.getTotalAmount();
        int requiredPoints = (int) Math.ceil(totalAmount / 100.0);

        if (user.getPoints() < requiredPoints) {
            return ResponseEntity.badRequest().body("Insufficient points. You need " + requiredPoints + " points to complete this transaction.");
        }

        user.setPoints(user.getPoints() - requiredPoints);
        usersService.saveUser(user);

        // Prepare item details
        List<BillingResponseDTO.ProductItemDetail> itemDetails = billing.getCartId().getCartItems().stream()
                .map(item -> new BillingResponseDTO.ProductItemDetail(
                        item.getProduct().getProductName(),
                        item.getQuantity(),
                        item.getProduct().getProductPrice()
                )).collect(Collectors.toList());

        // Save snapshot
        try {
            String snapshotJson = objectMapper.writeValueAsString(itemDetails);
            billing.setItemsSnapshot(snapshotJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        billing.setEarnedPoints(0.0);
        billingRepository.save(billing);

        // Send notification to buyer
        notificationService.createNotification(
                new Notification(
                        user,
                        "✅ Payment Successful (Points)",
                        "Your order has been paid successfully using your points.",
                        Notification.NotificationType.INFO
                )
        );

        // Handle seller revenue + notification
        Map<Integer, Double> sellerTotals = new HashMap<>();
        Map<Integer, Users> sellerMap = new HashMap<>();

        for (CartItems item : billing.getCartId().getCartItems()) {
            Users seller = item.getProduct().getSellerId();
            int sellerId = seller.getUserId();
            double itemTotal = item.getProduct().getProductPrice() * item.getQuantity();

            sellerTotals.merge(sellerId, itemTotal, Double::sum);
            sellerMap.putIfAbsent(sellerId, seller);
        }

        for (Map.Entry<Integer, Double> entry : sellerTotals.entrySet()) {
            Users seller = sellerMap.get(entry.getKey());
            double total = entry.getValue();
            seller.setRevenue(seller.getRevenue() + total);
            usersRepository.save(seller);

            String message = String.format(
                    "Your products have been sold for a total of %.2f SYP in a new order.", total
            );

            notificationService.createNotification(
                    new Notification(
                            seller,
                            "📦 New Order Received",
                            message,
                            Notification.NotificationType.INFO
                    )
            );
        }

        // Response
        BillingResponseDTO response = new BillingResponseDTO();
        response.setBuyerName(user.getUserName());
        response.setTotalAmount(billing.getTotalAmount());
        response.setPaymentMethod(billing.getPaymentMethod().name());
        response.setPaymentStatus(billing.getPaymentStatus().name());
        response.setCreatedAt(billing.getCreatedAt().toString());
        response.setEarnedPoints(0);
        response.setItems(itemDetails);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<BillingResponseDTO>> getMyBillings(@AuthenticationPrincipal UserDetails userDetails) {
        Users user = usersService.getUserByEmail(userDetails.getUsername());

        List<Billing> billings = billingRepository.findAllByUser_UserId(user.getUserId());

        List<BillingResponseDTO> responses = billings.stream().map(billing -> {
            BillingResponseDTO response = new BillingResponseDTO();
            response.setBuyerName(user.getUserName());
            response.setTotalAmount(billing.getTotalAmount());
            response.setPaymentMethod(billing.getPaymentMethod().name());
            response.setPaymentStatus(billing.getPaymentStatus().name().toUpperCase());
            response.setCreatedAt(billing.getCreatedAt().toString());

            // إعداد العناصر
            List<BillingResponseDTO.ProductItemDetail> itemDetails = Collections.emptyList();

            if (billing.getItemsSnapshot() != null) {
                try {
                    itemDetails = objectMapper.readValue(
                            billing.getItemsSnapshot(),
                            new TypeReference<List<BillingResponseDTO.ProductItemDetail>>() {}
                    );
                } catch (Exception e) {
                    e.printStackTrace(); // سجل الخطأ فقط، لا توقف التنفيذ
                }
            } else if (billing.getCartId() != null && billing.getCartId().getCartItems() != null) {
                itemDetails = billing.getCartId().getCartItems().stream()
                        .map(item -> new BillingResponseDTO.ProductItemDetail(
                                item.getProduct().getProductName(),
                                item.getQuantity(),
                                item.getProduct().getProductPrice()
                        )).collect(Collectors.toList());
            }

            response.setItems(itemDetails);

            // النقاط
            response.setEarnedPoints(billing.getEarnedPoints() != null ? billing.getEarnedPoints() : 0);

            return response;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }



    public static class BillingResponseDTO {
        private String buyerName;
        private List<?> items;
        private double totalAmount;
        private String paymentMethod;
        private String paymentStatus;
        private String createdAt;
        private double earnedPoints;

        public double getEarnedPoints() { return earnedPoints; }
        public void setEarnedPoints(double earnedPoints) { this.earnedPoints = earnedPoints; }
        public static class ProductItemDetail {
            private String productName;
            private int quantity;
            private double price;

            public ProductItemDetail(String productName, int quantity, double price) {
                this.productName = productName;
                this.quantity = quantity;
                this.price = price;
            }

            public String getProductName() { return productName; }
            public int getQuantity() { return quantity; }
            public double getPrice() { return price; }
        }

        public static class SubscriptionItemDetail {
            private String type;
            private double price;

            public SubscriptionItemDetail(String type, double price) {
                this.type = type;
                this.price = price;
            }

            public String getType() { return type; }
            public double getPrice() { return price; }
        }

        public String getBuyerName() { return buyerName; }
        public void setBuyerName(String buyerName) { this.buyerName = buyerName; }

        public List<?> getItems() { return items; }
        public void setItems(List<?> items) { this.items = items; }

        public double getTotalAmount() { return totalAmount; }
        public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

        public String getPaymentStatus() { return paymentStatus; }
        public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    }
}