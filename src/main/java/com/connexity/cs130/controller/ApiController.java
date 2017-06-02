package com.connexity.cs130.controller;

import com.connexity.cs130.amazon.SignedRequestsHelper;
import com.connexity.cs130.model.OfferResponse;
import com.connexity.cs130.model.ProductResponse;
import com.connexity.cs130.model.ItemLookupResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Created by 161497 on 4/20/17.
 */
@Controller
@RequestMapping("/api")
public class ApiController {

    @Autowired
    RestTemplate restTemplate;

    @Value("${api.key}")
    String apiKey;

    @Value("${publisher.id}")
    Long publisherId;

    @Value("${amazon.accesskey}")
    String amazonAccessKey;

    @Value("${amazon.secretkey}")
    String amazonSecretKey;

    @Value("${amazon.associatetag}")
    String amazonAssociateTag;


    @RequestMapping("/searchInitial")
    public String search(@RequestParam("keyword") String keyword, Map<String, Object> context) {
        String sort = "relevancy_desc";

        return getProductResponse(keyword, sort, context, 0,"", "");
    }

    @RequestMapping("/search")
    public String search(@RequestParam("keyword") String keyword, Map<String, Object> context, @RequestParam("minPrice") String minPrice, @RequestParam("maxPrice") String maxPrice, @RequestParam("sort") String sort) {
        //String sort = "relevancy_desc";

        return getProductResponse(keyword, sort, context, 0,minPrice, maxPrice);
    }

    @RequestMapping("/nextPage")
    public String nextPage(@RequestParam("keyword") String keyword, @RequestParam("next") int pageNo, Map<String, Object> context, @RequestParam("minPrice") String minPrice, @RequestParam("maxPrice") String maxPrice, @RequestParam("sort") String sort) {
        //String sort = "relevancy_desc";
        return getProductResponse(keyword, sort, context,  pageNo + 1 , minPrice, maxPrice);
    }

    @RequestMapping("/prevPage")
    public String prevPage(@RequestParam("keyword") String keyword, @RequestParam("prev") int pageNo, Map<String, Object> context, @RequestParam("minPrice") String minPrice, @RequestParam("maxPrice") String maxPrice, @RequestParam("sort") String sort) {
        //String sort = "relevancy_desc";

        if (pageNo - 1 < 0)
            return getProductResponse(keyword, sort, context,  0, minPrice, maxPrice);
        else
            return getProductResponse(keyword, sort, context,  pageNo - 1 , minPrice, maxPrice);
    }

    @RequestMapping("/searchId")
    public String IdSearch(@RequestParam("id") String id, Map<String, Object> context) {
        return getIdResponse(id, context);
    }

    private String getProductResponse(String keyword, String sort, Map<String,Object> context, int page, String minPrice, String maxPrice) {

        // If no search term is inputted
        if (keyword == "") {
            context.put("message", "Please input a product name.");
            context.put("searchTerm", "");
            context.put("maxPrice", "");
            context.put("minPrice", "");
            context.put("sort", "relevancy_desc");
            return "dynamicSearch";
        }

        ProductResponse response;

        // Retain search term, as well as price ranges
        context.put("searchTerm", keyword);
        context.put("maxPrice", maxPrice);
        context.put("minPrice", minPrice);
        context.put("sort", sort);


        // Validate price strings
        final String regExp = "(\\d+(.\\d{2})?)?";

        if (!minPrice.matches(regExp) || !maxPrice.matches(regExp)) {
            context.put("message", "Input valid price range");
            return "dynamicSearch";
        }

        // Create URL for Connexity API, then call API and grab response
        String url = createProductInfoRequestUrl(keyword, sort, minPrice, maxPrice, page);
        getAmazonResponse(context, "031398155409");
        response = restTemplate.getForEntity(url, ProductResponse.class).getBody();

        ArrayList<ProductResponse.Product> prs = new ArrayList<>();

        for (int i = 0; i != response.products.product.size(); i++) {
            prs.add(response.products.product.get(i));
        }
        context.put("products", prs);
        context.put("message", response.products);
        context.put("searchTerm", keyword);
        context.put("pageNo", page);

        if (prs.size() == 0) {
            context.put("message", "No results found.");
            return "dynamicSearch";
        }

        context.put("products", prs);
        return "dynamicSearch";
    }

    private String createProductInfoRequestUrl(String keyword, String sort, String minPrice, String maxPrice, int page) {
        // Connexity API requires price parameter to be in units of cents
        // If prices are empty, set the values to default (min = 0, max = 10,000)
        if (minPrice == "")
            minPrice = "0";
        if (maxPrice == "")
            maxPrice = "100000";
        // If price contains decimal, simply remove decimal to make value from $X.XX to XXX cents
        if (minPrice.indexOf('.') != -1)
            minPrice = minPrice.replaceAll("\\D+","");
            // Else if price is of form $XX, change it to XX00 cents
        else
            minPrice = minPrice + "00";
        if (maxPrice.indexOf('.') != -1)
            maxPrice = maxPrice.replaceAll("\\D+","");
        else
            maxPrice = maxPrice + "00";

        // Create the URL for Connexity API
        String url = "http://catalog.bizrate.com/services/catalog/v1/api/product?apiKey="
                + apiKey + "&publisherId=" + publisherId + "&keyword=" + keyword + "&format=json" + "&sort=" + sort + "&minPrice=" + minPrice + "&maxPrice=" + maxPrice + "&start=" + Integer.toString(page);
        return url;
    }

    public void getAmazonResponse(Map<String,Object> context, String upcID) {
        /*
         * Use one of the following end-points, according to the region you are
         * interested in:
         *
         *      US: ecs.amazonaws.com
         *      CA: ecs.amazonaws.ca
         *      UK: ecs.amazonaws.co.uk
         *      DE: ecs.amazonaws.de
         *      FR: ecs.amazonaws.fr
         *      JP: ecs.amazonaws.jp
         *
         */
        final String ENDPOINT = "ecs.amazonaws.com";

        /*
         * Set up the signed requests helper
         */
        SignedRequestsHelper helper;
        try {
            helper = SignedRequestsHelper.getInstance(ENDPOINT, amazonAccessKey, amazonSecretKey);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        String requestUrl;
        String lowestNewPrice = "";
        String lowestUsedPrice = "";
        String lowestRefurbishedPrice = "";
        String amazonURL = "";

        /* The helper can sign requests in two forms - map form and string form */

        /*
         * Here is an example in map form, where the request parameters are stored in a map.
         */
        System.out.println("Map form example:");
        Map<String, String> params = new HashMap<String, String>();
        params.put("Service", "AWSECommerceService");
        params.put("AssociateTag", amazonAssociateTag);
        params.put("Operation", "ItemLookup");
        params.put("IdType", "UPC");
        params.put("SearchIndex", "All");
        params.put("ItemId", upcID);
        params.put("ResponseGroup", "Offers");

        requestUrl = helper.sign(params);
        System.out.println("Signed Request is \"" + requestUrl + "\"");

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(requestUrl);
            Node lowestNewPriceNode = doc.getElementsByTagName("LowestNewPrice").item(0);
            Node lowestUsedPriceNode = doc.getElementsByTagName("LowestUsedPrice").item(0);
            Node lowestRefurbishedPriceNode = doc.getElementsByTagName("LowestRefurbishedPrice").item(0);
            Node amazonURLNode = doc.getElementsByTagName("MoreOffersUrl").item(0);
            if (lowestNewPriceNode != null) {
                lowestNewPrice = lowestNewPriceNode.getTextContent();
                lowestNewPrice = lowestNewPrice.substring(lowestNewPrice.indexOf("$"));
                context.put("amazonNewPrice", lowestNewPrice);
            }
            else {
                context.put("amazonNewPrice", "");
            }
            if (lowestUsedPriceNode != null) {
                lowestUsedPrice = lowestUsedPriceNode.getTextContent();
                lowestUsedPrice = lowestUsedPrice.substring(lowestUsedPrice.indexOf("$"));
                context.put("amazonUsedPrice", lowestUsedPrice);
            }
            else {
                context.put("amazonUsedPrice", "");
            }
            if (lowestRefurbishedPriceNode != null) {
                lowestRefurbishedPrice = lowestRefurbishedPriceNode.getTextContent();
                lowestRefurbishedPrice = lowestRefurbishedPrice.substring(lowestRefurbishedPrice.indexOf("$"));
                context.put("amazonRefurbishedPrice", lowestRefurbishedPrice);
            }
            else {
                context.put("amazonRefurbishedPrice", "");
            }
            if (amazonURLNode != null) {
                amazonURL = amazonURLNode.getTextContent();
                context.put("amazonURL", amazonURL);
            }
            else {
                context.put("amazonURL", "");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println(lowestNewPrice);
        System.out.println(lowestUsedPrice);
        System.out.println(lowestRefurbishedPrice);
        System.out.println(amazonURL);
    }

    private String createProductIdRequestUrl(String id) {
        String url = "http://catalog.bizrate.com/services/catalog/v1/api/product?apiKey="
                + apiKey + "&publisherId=" + publisherId + "&=productId=" + id + "&=productIdType=SZOID";
        return url;
    }

    private String getIdResponse(String id, Map<String,Object> context) {
        OfferResponse response;
        String url = createProductIdRequestUrl(id);
        response = restTemplate.getForEntity(url, OfferResponse.class).getBody();

        System.out.println(response);

        ArrayList<OfferResponse.Offer> prs = new ArrayList<>();

        for (int i = 0; i != response.offers.offer.size(); i++) {
            prs.add(response.offers.offer.get(i));
        }

        context.put("products", prs);
        context.put("message", "");

        if (prs.size() == 0) {
            context.put("message", "Display 404 Page");
            return "itemPage";
        }

        return "itemPage";
    }
}