package com.universityproject.webapp.foodstore.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;
    @Column(name = "name_user" , nullable = false)
    private String UserName;
    @Column(name = "email" , nullable = false)
    private String email;
    @Column(name = "password" , nullable = false)
    private String Password;
    @Column(name = "subscription_status")
    private boolean subscriptionStatus = false;
    @Column(name = "points")
    private double points =0;
    @Column(name = "phone_number")
    private String phoneNumber;
    @ManyToOne( cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "role_id" , nullable = false)
    private UserRoles roleId;
    @OneToMany(mappedBy = "sellerId" , cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Products> productsList;
    @Transient
    private int roleIdInput; // Temporary field for JSON input
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Location location;
    private boolean isVerified = false;
    @Column(name = "revenue",nullable = false)
    private double revenue = 0;

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public int getRoleIdInput() {
        return roleIdInput;
    }

    public void setRoleIdInput(int roleIdInput) {
        this.roleIdInput = roleIdInput;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public Users() {}

    public Users(String userName, String email, String password, boolean subscriptionStatus, double points) {
        UserName = userName;
        this.email = email;
        Password = password;
        this.subscriptionStatus = subscriptionStatus;
        this.points = points;
    }

    public List<Products> getProductsList() {
        return productsList;
    }

    public void setProductsList(List<Products> productsList) {
        this.productsList = productsList;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public UserRoles getRoleId() {
        return roleId;
    }

    public void setRoleId(UserRoles roleId) {
        this.roleId = roleId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public boolean isSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(boolean subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }

    public double getPoints() {
        return points;
    }

    public void setPoints(double points) {
        this.points = points;
    }


}
