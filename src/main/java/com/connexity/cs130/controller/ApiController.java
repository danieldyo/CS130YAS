package com.connexity.cs130.controller;

import com.connexity.cs130.model.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

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
    public String catalog(@RequestParam(name = "keyword") String keyword, Map<String, Object> context) {
        ProductResponse response;
        String url = createProductInfoRequestUrl(keyword);
        response = restTemplate.getForEntity(url, ProductResponse.class).getBody();
        context.put("message", response);
        return "helloDynamic";
    }

    private String createProductInfoRequestUrl(String keyword) {
        String url = "http://catalog.bizrate.com/services/catalog/v1/api/product?apiKey="
                + apiKey + "&publisherId=" + publisherId + "&keyword=" + keyword + "&format=json";
        return url;
    }
}
