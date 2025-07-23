package com.universityproject.webapp.foodstore.service;




import com.universityproject.webapp.foodstore.entity.Users;
import com.universityproject.webapp.foodstore.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;

    @Autowired
    public UsersServiceImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public Users saveUser(Users user) {
        return usersRepository.save(user);
    }

    @Override
    public Users getUserById(int userId) {
        return usersRepository.findById(userId).orElse(null);
    }

    @Override
    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    @Override
    public Users updateUser(int userId, Users updatedUser) {
        Optional<Users> optionalUser = usersRepository.findById(userId);
        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();
            if (updatedUser.getUserName() != null) user.setUserName(updatedUser.getUserName());
            if (updatedUser.getEmail() != null) user.setEmail(updatedUser.getEmail());
            if (updatedUser.getPassword() != null) user.setPassword(updatedUser.getPassword());
            if (updatedUser.getPhoneNumber() != null) user.setPhoneNumber(updatedUser.getPhoneNumber());
            user.setPoints(updatedUser.getPoints());
            user.setSubscriptionStatus(updatedUser.isSubscriptionStatus());
            if (updatedUser.getRoleId() != null) user.setRoleId(updatedUser.getRoleId());
            return usersRepository.save(user);
        }
        return null;
    }

    @Override
    public void deleteUser(int userId) {
        usersRepository.deleteById(userId);
    }
    @Override
    public Users getUserByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    @Override
    public Users updateUser(Users user) {
        return usersRepository.save(user);
    }

}


