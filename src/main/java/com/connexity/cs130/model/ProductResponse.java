package com.connexity.cs130.model;

import java.util.List;

/**
 * Created by 161497 on 4/20/17.
 */
public class ProductResponse {
    public Products product;

    @Override
    public String toString() {
        return "ProductResponse{" +
                "product=" + product +
                '}';
    }

    public static class Products {
        public List<Offer> offers;

        @Override
        public String toString() {
            return "Products{" +
            "offers=" + offers +
            '}';
        }


    }

    public static class Offer {
        public String title;
        public String description;
        public String manufacturer;

        @Override
        public String toString() {
            return "Offer{" +
                    "title='" + title + '\'' +
                    "description='" + description + '\'' +
                    "manufacturer='" + manufacturer + '\'' +
                    '}';
        }
    }


}

