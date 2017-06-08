package com.connexity.cs130.service;

/**
 * Created by vlad on 5/24/17.
 */
import com.connexity.cs130.model.User;

public interface UserService {
    public User findUserByEmail(String email);
    public void saveUser(User user);
    public void deleteUser(User user);
}
