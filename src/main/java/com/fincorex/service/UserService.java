package com.fincorex.service;

import com.fincorex.entity.User;
import java.util.List;

public interface UserService {
    User createUser(User user);

    List<User> getAllUsers();
}