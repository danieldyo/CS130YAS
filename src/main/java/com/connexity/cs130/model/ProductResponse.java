package com.connexity.cs130.model;

import java.util.List;

/**
 * Created by 161497 on 4/20/17.
 */
public class ProductResponse {
    public Products products;

    @Override
    public String toString() {
        return "ProductResponse{" +
                "products=" + products +
                '}';
    }

    public static class Products {
        public List<Product> product;

        @Override
        public String toString() {
            return "Products{" +
                    "product=" + product +
            '}';
        }


    }

    public static class Product {
        public String title;
        public String description;
        public String manufacturer;

        @Override
        public String toString() {
            return "Product{" +
                    "title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    ", manufacturer='" + manufacturer + '\'' +
                    '}';
        }
    }


}

