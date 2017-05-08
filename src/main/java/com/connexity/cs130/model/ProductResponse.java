package com.connexity.cs130.model;

import java.util.List;

/**
 * Created by 161497 on 4/20/17.
 */
public class ProductResponse {
    public Products products;

    @Override
    public String toString() {
        return  "Products: {" + products +
                '}';
    }

    public static class Products {
        public List<Product> product;

        @Override
        public String toString() {
            return "Product: {" + product +
            '}';
        }


    }

    public static class Product {
        public String title;
        public String description;
        public String manufacturer;
        public URL url;
        public Price price;
        public Images images;
        public String merchantName;

        @Override
        public String toString() {
            return "title: '" + title + '\'' +
                    ", description: '" + description + '\'' +
                    ", manufacturer: '" + manufacturer + '\'' +
                    ", " +  url + '\'' +
                    ", " +  images + '\'' +
                    ", " +  price + '\'' +
                    ", merchantName: '" + merchantName + '\'' +
                    '}';
        }
    }

    public static class Images {
        public List<Image> image;

        @Override
        public String toString() {
            return "Images: {" +
                    image + '\'' +
                    '}';
        }
    }

    public static class Image {
        public String value;

        @Override
        public String toString() {
            return  "image: {'" + value + '\'' +
                    '}';
        }
    }

    public static class Price {
        public String value;

        @Override
        public String toString() {
            return  "price: {'" + value + '\'' +
                    '}';
        }
    }

    public static class URL {
        public String value;

        @Override
        public String toString() {
            return  "url: {'" + value + '\'' +
                    '}';
        }
    }


}

