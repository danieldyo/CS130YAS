package com.connexity.cs130.model;

import com.connexity.cs130.model.ProductResponse.Products;
import java.sql.Timestamp;

/**
 * Created by 161497 on 5/18/17.
 */
public class User {
    public String userID;
    public String password;
    public int watchlistID;

    public User(String userID, String password, int watchlistID) {
        this.userID = userID;
        this.password = password;
        this.watchlistID = watchlistID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getWatchlistID() {
        return watchlistID;
    }

    public void setWatchlistID(int watchlistID) {
        this.watchlistID = watchlistID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }


}

