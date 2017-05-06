package com.connexity.cs130.controller;

import com.connexity.cs130.model.MerchantsResponse;
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
    public String catalog(Map<String, Object> context) {
        MerchantsResponse response;
        String url = createMerchantInfoRequestUrl(401L);
        response = restTemplate.getForEntity(url, MerchantsResponse.class).getBody();
        context.put("message", response);
        return "helloDynamic";
    }

    @RequestMapping(value="/proxy", produces = "Application/json")
    @ResponseBody
    public MerchantsResponse proxy() {
        MerchantsResponse response;
        String url = createMerchantInfoRequestUrl(401L);
        response = restTemplate.getForEntity(url, MerchantsResponse.class).getBody();

        return response;
    }

    private String createMerchantInfoRequestUrl(Long merchantId) {
        String url = "http://catalog.bizrate.com/services/catalog/v1/api/merchantinfo?apiKey="
                + "f94ab04178d1dea0821d5816dfb8af8d" + "&publisherId=" + "608865" + "&merchantId=" + merchantId;
        return url;
    }
}
