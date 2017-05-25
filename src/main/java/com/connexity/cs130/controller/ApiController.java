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
}
