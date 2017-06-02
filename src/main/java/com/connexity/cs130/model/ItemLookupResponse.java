package com.connexity.cs130.model;

import java.util.List;

/**
 * Created by 161497 on 4/20/17.
 */
public class ItemLookupResponse {
    public Items Items;

    @Override
    public String toString() {
        return  "ItemLookupResponse: {" + Items +
                '}';
    }

    public static class Items {
        public Item Item;

        @Override
        public String toString() {
            return "Items:" + Item +
                    '}';
        }

    }

    public static class Item {
        public OfferSummary OfferSummary;

        @Override
        public String toString() {
            return "OfferSummary:" + OfferSummary +
                    '}';
        }

    }

    public static class OfferSummary {
        public LowestNewPrice LowestNewPrice;
        public LowestUsedPrice LowestUsedPrice;
        //public LowestRefurbishedPrice LowestRefurbishedPrice;

        @Override
        public String toString() {
            return "LowestNewPrice: '" + LowestNewPrice + '\'' +
                    ", LowestUsedPrice: '" + LowestUsedPrice + '\'' +
                    //", LowestRefurbishedPRice: '" + LowestRefurbishedPrice + '\'' +
                    '}';
        }
    }


    public static class LowestNewPrice {
        public String FormattedPrice = "N/A";

        @Override
        public String toString() {
            return "FormattedPrice: " +
                    FormattedPrice;
        }
    }

    public static class LowestUsedPrice {
        public String FormattedPrice = "N/A";

        @Override
        public String toString() {
            return "FormattedPrice: " +
                    FormattedPrice;
        }
    }

    /*public static class LowestRefurbishedPrice {
        public String FormattedPrice = "N/A";

        @Override
        public String toString() {
            return "FormattedPrice: " +
                    FormattedPrice;
        }
    }*/

}

