package com.connexity.cs130.controller;

import com.connexity.cs130.model.OfferResponse;
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

    @RequestMapping("/search")
    public String search(@RequestParam("keyword") String keyword, Map<String, Object> context) {
        String sort = "relevancy_desc";
        System.out.println("1");
        return getProductResponse(keyword, sort, context);
    }

    @RequestMapping(value = "/searchAsc")
    public String searchAsc(@RequestParam("keyword") String keyword, Map<String, Object> context) {
        String sort = "price_asc";
        System.out.println("2");
        return getProductResponse(keyword, sort, context);
    }

    @RequestMapping("/searchDesc")
    public String searchDesc(@RequestParam("keyword") String keyword, Map<String, Object> context) {
        String sort = "price_desc";
        System.out.println("3");
        return getProductResponse(keyword, sort, context);
    }

    @RequestMapping("/searchId")
    public String IdSearch(@RequestParam("id") String id, Map<String, Object> context) {
        System.out.println("4");
        return getIdResponse(id, context);

    }

    private String getProductResponse(String keyword, String sort, Map<String,Object> context) {

        if (keyword == "") {
            context.put("message", "Please input a product name.");
            context.put("searchTerm", "");
            return "dynamicSearch";
        }

        ProductResponse response;

        String url = createProductInfoRequestUrl(keyword, sort);
        response = restTemplate.getForEntity(url, ProductResponse.class).getBody();

        ArrayList<ProductResponse.Product> prs = new ArrayList<>();

        for (int i = 0; i != response.products.product.size(); i++) {
            prs.add(response.products.product.get(i));
        }
        context.put("products", prs);
        context.put("message", response);
        context.put("searchTerm", keyword);

        if (prs.size() == 0) {
            context.put("message", "No results found.");
            return "dynamicSearch";
        }

        context.put("products", prs);
        context.put("message", "");
        return "dynamicSearch";
    }


    private String createProductInfoRequestUrl(String keyword, String sort) {
        String url = "http://catalog.bizrate.com/services/catalog/v1/api/product?apiKey="
                + apiKey + "&publisherId=" + publisherId + "&keyword=" + keyword + "&format=json" + "&sort=" + sort;
        return url;
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
        context.put("message", id);

        if (prs.size() == 0) {
            context.put("message", "Display 404 Page");
            return "itemPage";
        }




        //context.put("products", prs);
        //context.put("message", id);
        return "itemPage";
    }

}
