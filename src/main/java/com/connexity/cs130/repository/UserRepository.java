package com.connexity.cs130.repository;

/**
 * Created by vlad on 5/24/17.
 */

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.connexity.cs130.model.User;

import javax.transaction.Transactional;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    @Transactional
    void deleteUserById(Integer user_id);
}
