package com.universityproject.webapp.foodstore.controller;

import com.universityproject.webapp.foodstore.entity.Categories;
import com.universityproject.webapp.foodstore.entity.Location;
import com.universityproject.webapp.foodstore.entity.Products;
import com.universityproject.webapp.foodstore.entity.Users;
import com.universityproject.webapp.foodstore.repository.CategoriesRepository;
import com.universityproject.webapp.foodstore.repository.ProductRepository;
import com.universityproject.webapp.foodstore.repository.UsersRepository;
import com.universityproject.webapp.foodstore.service.CategoriesService;
import com.universityproject.webapp.foodstore.service.ProductService;
import com.universityproject.webapp.foodstore.service.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductsController {
    @Autowired
    private HttpServletRequest servletRequest;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductService productService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;
    @Autowired
    private UsersService usersService;

    public static class ProductRequest {
        private Integer productId;
        private String productName;
        private String productDescription;
        private double productPrice;
        private int categoryIdInput;
        private boolean availabilityStatus;
        private int quantity;
        private String expiryDate;

        public Integer getProductId() { return productId; }
        public void setProductId(Integer productId) { this.productId = productId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public String getProductDescription() { return productDescription; }
        public void setProductDescription(String productDescription) { this.productDescription = productDescription; }
        public double getProductPrice() { return productPrice; }
        public void setProductPrice(double productPrice) { this.productPrice = productPrice; }
        public int getCategoryIdInput() { return categoryIdInput; }
        public void setCategoryIdInput(int categoryIdInput) { this.categoryIdInput = categoryIdInput; }
        public boolean isAvailabilityStatus() { return availabilityStatus; }
        public void setAvailabilityStatus(boolean availabilityStatus) { this.availabilityStatus = availabilityStatus; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public String getExpiryDate() { return expiryDate; }
        public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
    }

    private Users getAuthenticatedSeller(UserDetails user) {
        if (user == null) return null;
        return usersRepository.findByEmail(user.getUsername());
    }

    @GetMapping
    public ResponseEntity<List<Products>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PostMapping(value = "/seller", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Products> createProduct(
            @AuthenticationPrincipal UserDetails user,
            @ModelAttribute ProductRequest productRequest,
            @RequestParam("image") MultipartFile image) {

        Users seller = getAuthenticatedSeller(user);
        if (seller == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Products product = new Products();
        product.setProductName(productRequest.getProductName());
        product.setProductDescription(productRequest.getProductDescription());
        product.setProductPrice(productRequest.getProductPrice());
        product.setcategoryIdInput(productRequest.getCategoryIdInput());
        product.setAvailabilityStatus(productRequest.isAvailabilityStatus());
        product.setQuantity(productRequest.getQuantity());

        if (productRequest.getExpiryDate() != null && !productRequest.getExpiryDate().isEmpty()) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                product.setExpiryDate(formatter.parse(productRequest.getExpiryDate()));
            } catch (ParseException e) {
                return ResponseEntity.badRequest().body(null);
            }
        }

        product.setSellerId(seller);

        // ✅ معالجة الصورة بمسار نسبي ديناميكي
        if (image != null && !image.isEmpty()) {
            String baseDir = System.getProperty("user.dir"); // ← مسار المشروع الحالي
            String imagesDir = baseDir + File.separator + "Images";

            File dir = new File(imagesDir);
            if (!dir.exists()) dir.mkdirs();

            String filePath = imagesDir + File.separator + image.getOriginalFilename();

            try {
                image.transferTo(new File(filePath));

                // نحفظ فقط المسار النسبي في قاعدة البيانات
                String relativePath = "/images/" + image.getOriginalFilename();
                product.setImagePath(relativePath);

            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }

        // ✅ ربط الصنف
        if (product.getcategoryIdInput() > 0) {
            Categories category = categoriesRepository.findById(product.getcategoryIdInput()).orElse(null);
            if (category == null) return ResponseEntity.badRequest().body(null);
            product.setCategoryId(category);
        }

        return ResponseEntity.ok(productService.saveProduct(product));
    }

    @GetMapping("/nearby")
    public ResponseEntity<?> getNearbyProducts(@AuthenticationPrincipal UserDetails userDetails) {

        Users user = usersService.getUserByEmail(userDetails.getUsername());

        if (!user.isSubscriptionStatus()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("هذه الخدمة للمشتركين فقط.");
        }

        Location userLocation = user.getLocation(); // تأكد من الربط
        double lat1 = userLocation.getLatitude();
        double lon1 = userLocation.getLongitude();

        List<Products> allProducts = productRepository.findAll(); // تأكد أنه يحتوي على موقع البائع

        List<Products> nearby = allProducts.stream()
                .filter(p -> p.getSellerId().getLocation() != null)
                .sorted(Comparator.comparingDouble(p -> {
                    Location sellerLocation = p.getSellerId().getLocation();
                    return haversine(lat1, lon1, sellerLocation.getLatitude(), sellerLocation.getLongitude());
                }))
                .collect(Collectors.toList());

        return ResponseEntity.ok(nearby);
    }
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }


    @PutMapping("/edit")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<Products> updateProduct(
            @AuthenticationPrincipal UserDetails user,
            @ModelAttribute ProductRequest productRequest,
            @RequestParam(value = "image", required = false) MultipartFile image,
            HttpServletRequest servletRequest) {

        Users seller = getAuthenticatedSeller(user);
        if (seller == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Products product = productRepository.findById(productRequest.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (productRequest.getProductName() != null && !productRequest.getProductName().isEmpty()) {
            product.setProductName(productRequest.getProductName());
        }

        if (productRequest.getProductDescription() != null && !productRequest.getProductDescription().isEmpty()) {
            product.setProductDescription(productRequest.getProductDescription());
        }

        if (productRequest.getProductPrice() != 0) {
            product.setProductPrice(productRequest.getProductPrice());
        }

        if (productRequest.getQuantity() != 0) {
            product.setQuantity(productRequest.getQuantity());
        }

        String availabilityParam = servletRequest.getParameter("availabilityStatus");
        if (availabilityParam != null) {
            boolean newStatus = Boolean.parseBoolean(availabilityParam);
            if (newStatus != product.isAvailabilityStatus()) {
                product.setAvailabilityStatus(newStatus);
            }
        }

        if (productRequest.getExpiryDate() != null && !productRequest.getExpiryDate().isEmpty()) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                product.setExpiryDate(formatter.parse(productRequest.getExpiryDate()));
            } catch (ParseException e) {
                return ResponseEntity.badRequest().body(null);
            }
        }

        // ✅ معالجة الصورة بمسار ديناميكي آمن
        if (image != null && !image.isEmpty()) {
            String baseDir = System.getProperty("user.dir");
            String imagesDir = baseDir + File.separator + "Images";

            File dir = new File(imagesDir);
            if (!dir.exists()) dir.mkdirs();

            String filePath = imagesDir + File.separator + image.getOriginalFilename();

            try {
                image.transferTo(new File(filePath));
                String relativePath = "/images/" + image.getOriginalFilename();
                product.setImagePath(relativePath);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }

        if (productRequest.getCategoryIdInput() != 0) {
            Categories category = categoriesRepository.findById(productRequest.getCategoryIdInput()).orElse(null);
            if (category == null) return ResponseEntity.badRequest().body(null);
            product.setCategoryId(category);
            product.setcategoryIdInput(productRequest.getCategoryIdInput());
        }

        return ResponseEntity.ok(productService.saveProduct(product));
    }



    @DeleteMapping("/delete/{productId}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<Void> deleteProductByIdAndSeller(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable int productId) { // ← استبدل @RequestParam بـ @PathVariable

        Users seller = getAuthenticatedSeller(user);
        if (seller == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        productService.deleteProductByIdAndSellerId(productId, seller);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/seller")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<List<Products>> getAllProductsBySeller(
            @AuthenticationPrincipal UserDetails user) {

        Users seller = getAuthenticatedSeller(user);
        if (seller == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(productService.getAllProductsBySellerId(seller));
    }

    @GetMapping("/seller/category/{categoryId}")
    public ResponseEntity<List<Products>> getProductsByCategoryAndSeller(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable int categoryId) {

        Users seller = getAuthenticatedSeller(user);
        if (seller == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Categories category = categoriesRepository.findById(categoryId).orElse(null);
        if (category == null) return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(productService.getProductsByCategoryAndSellerId(category, seller));
    }
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Products>> getProductsByCategory(@PathVariable int categoryId) {


        Categories category = categoriesRepository.findById(categoryId).orElse(null);
        if (category == null) return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(productService.getProductsByCategory(category));
    }

    @GetMapping("/seller/product/{productId}")
    public ResponseEntity<Products> getProductByIdAndSeller(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable int productId) {

        Users seller = getAuthenticatedSeller(user);
        if (seller == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<Products> optionalProduct = productService.getProductByIdAndSellerId(productId, seller);
        if (optionalProduct.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(optionalProduct.get());
    }
    @GetMapping("/search")
    public ResponseEntity<List<Products>> searchProducts(@RequestParam String name) {
        return ResponseEntity.ok(productService.searchProductsByName(name));
    }

}
