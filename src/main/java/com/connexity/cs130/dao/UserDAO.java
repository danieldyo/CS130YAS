package com.connexity.cs130.dao;

import com.connexity.cs130.model.User;

public interface UserDAO
{
    void insert(User user);
    User findByUserID(String userID);
}