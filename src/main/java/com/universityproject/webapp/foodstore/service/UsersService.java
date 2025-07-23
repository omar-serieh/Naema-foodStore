package com.universityproject.webapp.foodstore.service;



import com.universityproject.webapp.foodstore.entity.Users;

import java.util.List;
import java.util.Optional;
public interface UsersService {

    // Retrieve all categories
    Users saveUser(Users user);
    Users getUserById(int userId);
    List<Users> getAllUsers();
    Users updateUser(int userId, Users user);
    void deleteUser(int userId);
    Users getUserByEmail(String email);
    Users updateUser(Users user);

}
