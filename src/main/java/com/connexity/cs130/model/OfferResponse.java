/*****************************************************************************************************
 * Your Awesome Stuff (YAS) - Web Application where buyers or sellers can find the price of a product.
 * Copyright (C) 2017 CS130YAS
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *****************************************************************************************************
 */

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

