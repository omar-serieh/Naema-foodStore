package com.universityproject.webapp.foodstore.controller;

import com.universityproject.webapp.foodstore.entity.EmailVerificationCode;
import com.universityproject.webapp.foodstore.entity.Location;
import com.universityproject.webapp.foodstore.entity.UserRoles;
import com.universityproject.webapp.foodstore.entity.Users;
import com.universityproject.webapp.foodstore.repository.EmailVerificationCodeRepository;
import com.universityproject.webapp.foodstore.repository.UserRolesRepository;
import com.universityproject.webapp.foodstore.repository.UsersRepository;
import com.universityproject.webapp.foodstore.security.JwtUtil;
import com.universityproject.webapp.foodstore.service.EmailVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsersRepository usersRepository;
    private final UserRolesRepository userRolesRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailVerificationCodeRepository emailVerificationCodeRepository;
    private final EmailVerificationService emailVerificationService;


    @Autowired
    public AuthController(EmailVerificationService emailVerificationService,EmailVerificationCodeRepository emailVerificationCodeRepository,UsersRepository usersRepository, UserRolesRepository userRolesRepository, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.emailVerificationService = emailVerificationService;
        this.emailVerificationCodeRepository = emailVerificationCodeRepository;
        this.usersRepository = usersRepository;
        this.userRolesRepository = userRolesRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // Registration Endpoint
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationRequest request) {
        if (usersRepository.findByEmail(request.getEmail()) != null) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        Optional<UserRoles> selectedRole = userRolesRepository.findById(request.getRoleId());
        if (selectedRole.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid role selected");
        }

        // إنشاء مستخدم جديد
        Users newUser = new Users();
        newUser.setUserName(request.getName());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setPhoneNumber(request.getPhoneNumber());
        newUser.setRoleId(selectedRole.get());
        newUser.setVerified(false); // المستخدم غير مفعل حالياً

        Location location = new Location();
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());
        location.setAddress(request.getAddress());
        location.setUser(newUser);
        newUser.setLocation(location);

        // حفظ المستخدم أولاً
        Users savedUser = usersRepository.save(newUser);

        // ✅ توليد كود التحقق (OTP)
        String verificationCode = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(10);

        // إنشاء كود التحقق وربطه بالمستخدم
        EmailVerificationCode code = new EmailVerificationCode();
        code.setCode(verificationCode);
        code.setExpiry(expiryTime);
        code.setUser(savedUser);
        emailVerificationCodeRepository.save(code);

        // ✅ إرسال الكود بالإيميل
        emailVerificationService.sendVerificationCode(savedUser.getEmail(), verificationCode);

        return ResponseEntity.ok("Registration successful. Please verify your email using the code sent.");
    }



    // Login Endpoint
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        System.out.println("🔍 Attempting login for: " + request.getEmail());

        Users user = usersRepository.findByEmail(request.getEmail());

        if (user == null) {
            System.out.println("❌ User not found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            System.out.println("❌ Password incorrect");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }

        if (!user.isVerified()) {
            System.out.println("❌ User not verified");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Please verify your email before logging in.");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRoleId().getRoleName());

        return ResponseEntity.ok(
                new LoginResponse(
                        user.getUserId(),
                        user.getUserName(),
                        user.getRoleId().getRoleName(),
                        token,
                        user.getEmail(),
                        null // لا ترجع الباسورد
                )
        );
    }


    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody VerifyCodeRequest request) {
        Optional<EmailVerificationCode> optionalCode = emailVerificationCodeRepository.findByUser_Email(request.getEmail());
        if (optionalCode.isEmpty()) {
            return ResponseEntity.badRequest().body("Verification code not found");
        }

        EmailVerificationCode codeEntity = optionalCode.get();

        if (!codeEntity.getCode().equals(request.getCode())) {
            return ResponseEntity.badRequest().body("Invalid verification code");
        }

        if (codeEntity.getExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Verification code expired");
        }

        Users user = codeEntity.getUser();
        user.setVerified(true);
        usersRepository.save(user);

        // حذف كود التحقق بعد التفعيل (اختياري)
        emailVerificationCodeRepository.delete(codeEntity);

        // توليد التوكن
        String token = jwtUtil.generateToken(user.getEmail(), user.getRoleId().getRoleName());

        return ResponseEntity.ok(
                new RegistrationResponse(
                        user.getUserId(),
                        user.getUserName(),
                        user.getRoleId().getRoleName(),
                        token,
                        user.getEmail(),
                        null
                )
        );
    }
    @PostMapping("/resend-code")
    public ResponseEntity<?> resendVerificationCode(@RequestBody EmailRequest request) {
        System.out.println("📩 Resend code for email: " + request.getEmail());

        Users user = usersRepository.findByEmail(request.getEmail());
        if (user == null) {
            System.out.println("❌ User not found");
            return ResponseEntity.badRequest().body("Invalid request");
        }

        if (user.isVerified()) {
            System.out.println("✅ User already verified");
            return ResponseEntity.badRequest().body("User already verified");
        }
        emailVerificationCodeRepository.findByUser_Email(user.getEmail())
                .ifPresent(emailVerificationCodeRepository::delete);

        String newCode = String.format("%06d", new Random().nextInt(999999));
        EmailVerificationCode code = new EmailVerificationCode();
        code.setCode(newCode);
        code.setExpiry(LocalDateTime.now().plusMinutes(10));
        code.setUser(user);

        emailVerificationCodeRepository.save(code);
        emailVerificationService.sendVerificationCode(user.getEmail(), newCode);

        return ResponseEntity.ok("New verification code sent.");
    }


    public static class EmailRequest{
        private String email;
        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;

        }
    }

    // DTOs for Request and Response
    public static class VerifyCodeRequest {
        private String email;
        private String code;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    public static class RegistrationRequest {
        private String name;
        private String email;
        private String password;
        private String phoneNumber;
        private int roleId; // Role ID (e.g., 1 for buyer, 3 for seller)
        private double latitude;
        private double longitude;
        private String address;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public int getRoleId() {
            return roleId;
        }

        public void setRoleId(int roleId) {
            this.roleId = roleId;
        }
    }

    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class LoginResponse {
        private int userId;
        private String userName;
        private String roleName;
        private String token;
        private String email;
        private String password;

        public LoginResponse(int userId, String userName, String roleName, String token, String email, String password) {
            this.userId = userId;
            this.userName = userName;
            this.roleName = roleName;
            this.token = token;
            this.email = email;
            this.password = password;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getRoleName() {
            return roleName;
        }

        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class RegistrationResponse {
        private int userId;
        private String userName;
        private String roleName;
        private String token;
        private String email;
        private String password;

        public RegistrationResponse(int userId, String userName, String roleName, String token, String email, String password) {
            this.userId = userId;
            this.userName = userName;
            this.roleName = roleName;
            this.token = token;
            this.email = email;
            this.password = password;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getRoleName() {
            return roleName;
        }

        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
