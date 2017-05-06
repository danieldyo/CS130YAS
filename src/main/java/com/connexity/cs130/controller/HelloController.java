package com.connexity.cs130.controller;

import com.connexity.cs130.model.MerchantsResponse;
import com.connexity.cs130.model.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Created by 161497 on 4/20/17.
 */
@Controller
@RequestMapping("/api")
public class HelloController {

    @Autowired
    RestTemplate restTemplate;

    @Value("${api.key}")
    String apiKey;

    @Value("${publisher.id}")
    Long publisherId;

    @RequestMapping("/hello")
    public String hello(@RequestParam(name = "name") String name, Map<String, Object> context) {
        String message = "Hello " + name;
        context.put("message", message);
        return "helloDynamic";
    }

    @RequestMapping("/catalog")
    public String catalog(@RequestParam(name = "search") String keyword, Map<String, Object> context) {
        ProductResponse response;
        String url = createMerchantInfoRequestUrl(keyword);
        response = restTemplate.getForEntity(url, ProductResponse.class).getBody();
        context.put("message", response);
        return "helloDynamic";
    }

    /*@RequestMapping(value="/proxy", produces = "Application/json")
    @ResponseBody
    public ProductResponse proxy() {
        ProductResponse response;
        String url = createMerchantInfoRequestUrl("bed");
        response = restTemplate.getForEntity(url, ProductResponse.class).getBody();

        return response;
    }*/

    private String createMerchantInfoRequestUrl(String keyword) {
        String url = "http://catalog.bizrate.com/services/catalog/v1/api/product?apiKey="
                + "f94ab04178d1dea0821d5816dfb8af8d" + "&publisherId=" + "608865" + "&keyword=" + keyword + "&format=json";
        return url;
    }
}
