package com.connexity.cs130.model;

import java.util.List;

/**
 * Created by 161497 on 4/20/17.
 */
public class OfferResponse {
    public Offers offers;

    @Override
    public String toString() {
        return  "Offers: {" + offers +
                '}';
    }

    public static class Offers {
        public List<Offer> offer;

        @Override
        public String toString() {
            return "Offer: {" + offer +
                    '}';
        }

    }

    public static class Offer {
        public String title;
        public String description;
        public String manufacturer;
        public URL url;
        public Price price = new Price();
        public Images images;
        public String merchantName;
        public String upc = "N/A";
        public String id;

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
            return "Images: " +
                    image;
        }
    }

    public static class Image {
        public String value;

        @Override
        public String toString() {
            return value;
        }
    }

    public static class Price {
        public String value = "N/A";

        @Override
        public String toString() {
            return  value;
        }
    }

    public static class URL {
        public String value;

        @Override
        public String toString() {
            return value;
        }
    }


}

