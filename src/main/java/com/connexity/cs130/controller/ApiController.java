package com.connexity.cs130.controller;

import com.connexity.cs130.model.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Map;

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


    @RequestMapping("/searchInitial")
    public String search(@RequestParam("keyword") String keyword, Map<String, Object> context) {
        String sort = "relevancy_desc";

        return getProductResponse(keyword, sort, context, "", "");
    }

    @RequestMapping("/search")
    public String search(@RequestParam("keyword") String keyword, Map<String, Object> context, @RequestParam("minPrice") String minPrice, @RequestParam("maxPrice") String maxPrice) {
        String sort = "relevancy_desc";

        return getProductResponse(keyword, sort, context, minPrice, maxPrice);
    }

    @RequestMapping(value = "/searchAsc")
    public String searchAsc(@RequestParam("keyword") String keyword, Map<String, Object> context, @RequestParam("minPrice") String minPrice, @RequestParam("maxPrice") String maxPrice) {
        String sort = "price_asc";

        return getProductResponse(keyword, sort, context, minPrice, maxPrice);
    }

    @RequestMapping("/searchDesc")
    public String searchDesc(@RequestParam("keyword") String keyword, Map<String, Object> context, @RequestParam("minPrice") String minPrice, @RequestParam("maxPrice") String maxPrice) {
        String sort = "price_desc";
        return getProductResponse(keyword, sort, context, minPrice, maxPrice);
    }

    private String getProductResponse(String keyword, String sort, Map<String,Object> context, String minPrice, String maxPrice) {

        // If no search term is inputted
        if (keyword == "") {
            context.put("message", "Please input a product name.");
            context.put("searchTerm", "");
            context.put("maxPrice", "");
            context.put("minPrice", "");
            return "dynamicSearch";
        }

        ProductResponse response;

        // Retain search term, as well as price ranges
        context.put("searchTerm", keyword);
        context.put("maxPrice", maxPrice);
        context.put("minPrice", minPrice);


        // Validate price strings
        final String regExp = "(\\d+(.\\d{2})?)?";

        if (!minPrice.matches(regExp) || !maxPrice.matches(regExp)) {
            context.put("message", "Input valid price range");
            return "dynamicSearch";
        }

        // Create URL for Connexity API, then call API and grab response
        String url = createProductInfoRequestUrl(keyword, sort, minPrice, maxPrice);
        response = restTemplate.getForEntity(url, ProductResponse.class).getBody();

        ArrayList<ProductResponse.Product> prs = new ArrayList<>();

        for (int i = 0; i != response.products.product.size(); i++) {
            prs.add(response.products.product.get(i));
        }

        if (prs.size() == 0) {
            context.put("message", "No results found.");
            return "dynamicSearch";
        }

        // context.put("message", response)
        context.put("message","");
        context.put("products", prs);

        return "dynamicSearch";
    }

    private String createProductInfoRequestUrl(String keyword, String sort, String minPrice, String maxPrice) {
        // Connexity API requires price paramter to be in units of cents
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
                + apiKey + "&publisherId=" + publisherId + "&keyword=" + keyword + "&format=json" + "&sort=" + sort + "&minPrice=" + minPrice + "&maxPrice=" + maxPrice;
        return url;
    }
}
