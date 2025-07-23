package com.universityproject.webapp.foodstore.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "products")
public class Products {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private int productId;
    @Column(name = "name_product")
    private String productName;
    @Column(name = "description_product")
    private String productDescription;
    @Column(name = "expiration_date")
    private Date expiryDate;
    @Column(name = "price")
    private double productPrice;
    @ManyToOne(fetch=FetchType.EAGER,
            cascade = {CascadeType.PERSIST,
                    CascadeType.MERGE,
                    CascadeType.REFRESH,
                    CascadeType.DETACH})
    @JoinColumn(name = "category_id")
    private Categories categoryId;
    @Column(name = "Quantity")
    private int quantity;
    @Column(name = "availability_status")
    private boolean availabilityStatus;
    @ManyToOne(fetch=FetchType.EAGER,
            cascade = {CascadeType.PERSIST,
                    CascadeType.MERGE,
                    CascadeType.REFRESH,
                    CascadeType.DETACH})
    @JoinColumn(name = "seller_id")
    private Users sellerId;
    @Transient
    private int sellerIdInput; // Temporary field for JSON input
    @Transient
    private int categoryIdInput;
    // Temporary field for JSON input
    @Column(name = "image_path")
    private String imagePath;
    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<CartItems> cartItems;

    public List<CartItems> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItems> cartItems) {
        this.cartItems = cartItems;
    }

    // Getter and Setter for imagePath
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    public int getcategoryIdInput() {
        return categoryIdInput;
    }

    public void setcategoryIdInput(int categoryIdInput) {
        this.categoryIdInput = categoryIdInput;
    }

    public int getSellerIdInput() {
        return sellerIdInput;
    }

    public void setSellerIdInput(int sellerIdInput) {
        this.sellerIdInput = sellerIdInput;
    }


    public Products() {
    }

    public Products(String productName, String productDescription, Date expiryDate, double productPrice, boolean availabilityStatus) {
        this.productName = productName;
        this.productDescription = productDescription;
        this.expiryDate = expiryDate;
        this.productPrice = productPrice;

        this.availabilityStatus = availabilityStatus;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }



    public Users getSellerId() {
        return sellerId;
    }

    public void setSellerId(Users sellerId) {
        this.sellerId = sellerId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public Categories getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Categories categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(boolean availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }
}
