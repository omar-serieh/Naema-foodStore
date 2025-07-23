package com.universityproject.webapp.foodstore.controller;



import com.universityproject.webapp.foodstore.entity.Location;
import com.universityproject.webapp.foodstore.entity.Users;
import com.universityproject.webapp.foodstore.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private PasswordEncoder passwordEncoder;


    private final UsersService usersService;


    public Users getAuthenticatedSeller(UserDetails userDetails) {
        if (userDetails == null) return null;

        Users user = usersService.getUserByEmail(userDetails.getUsername());
        if (user == null) return null;

        if (!user.getRoleId().getRoleName().equalsIgnoreCase("SELLER")) {
            return null;
        }

        return user;
    }

    public UserController(UsersService usersService) {
        this.usersService = usersService;
    }

    static class UserUpdateDto {
        public String userName;
        public String phoneNumber;
        public String email;
        public Boolean subscriptionStatus;
        private Double latitude;
        private Double longitude;
        private String address;
        private String password;

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Boolean getSubscriptionStatus() {
            return subscriptionStatus;
        }

        public void setSubscriptionStatus(Boolean subscriptionStatus) {
            this.subscriptionStatus = subscriptionStatus;
        }

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
        // أضف حقولًا أخرى عند الحاجة
    }

    @GetMapping("/me")
    public ResponseEntity<Users> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        Users user = usersService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(user);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Users>> getAllUsers() {
        List<Users> users = usersService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Users> getUserById(@PathVariable int id) {
        Users user = usersService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    public ResponseEntity<Users> updateMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserUpdateDto dto) {

        Users user = usersService.getUserByEmail(userDetails.getUsername());

        // ✅ تحقق من أن الحقول ليست null أو فارغة
        if (dto.userName != null && !dto.userName.trim().isEmpty()) {
            user.setUserName(dto.userName.trim());
        }

        if (dto.phoneNumber != null && !dto.phoneNumber.trim().isEmpty()) {
            user.setPhoneNumber(dto.phoneNumber.trim());
        }

        if (dto.email != null && !dto.email.trim().isEmpty()) {
            user.setEmail(dto.email.trim());
        }

        if (dto.subscriptionStatus != null) {
            user.setSubscriptionStatus(dto.subscriptionStatus);
        }

        if (dto.password != null && !dto.password.trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.password.trim()));
        }

        if (dto.getLatitude() != null && dto.getLongitude() != null) {
            Location location = user.getLocation();
            if (location == null) {
                location = new Location();
                location.setUser(user);
            }
            location.setLatitude(dto.getLatitude());
            location.setLongitude(dto.getLongitude());
            location.setAddress(
                    dto.getAddress() != null && !dto.getAddress().trim().isEmpty()
                            ? dto.getAddress().trim()
                            : location.getAddress() // حافظ على العنوان القديم إذا الجديد فارغ
            );
            user.setLocation(location);
        }

        Users updatedUser = usersService.updateUser(user);
        return ResponseEntity.ok(updatedUser);
    }



    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount(@AuthenticationPrincipal UserDetails userDetails) {
        Users user = usersService.getUserByEmail(userDetails.getUsername());
        usersService.deleteUser(user.getUserId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        usersService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/seller/revenue")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Double> getSellerRevenue(@AuthenticationPrincipal UserDetails user) {
        Users seller = getAuthenticatedSeller(user);
        if (seller == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(seller.getRevenue());
    }

}