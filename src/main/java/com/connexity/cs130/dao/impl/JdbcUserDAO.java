package com.connexity.cs130.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import com.connexity.cs130.dao.UserDAO;
import com.connexity.cs130.model.User;

public class JdbcUserDAO implements UserDAO
{
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insert(User user){

        String sql = "INSERT INTO USER " +
                "(userID, password, watchlistID) VALUES (?, ?, ?)";
        Connection conn = null;

        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUserID());
            ps.setString(2, user.getPassword());
            ps.setInt(3, user.getWatchlistID());
            ps.executeUpdate();
            ps.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);

        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {}
            }
        }
    }

    public User findByUserID(String userID){

        String sql = "SELECT * FROM USER WHERE UserID = ?";

        Connection conn = null;

        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, userID);
            User user = null;
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                user = new User(
                        rs.getString("userID"),
                        rs.getString("password"),
                        rs.getInt("watchlistID")
                );
            }
            rs.close();
            ps.close();
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {}
            }
        }
    }
}