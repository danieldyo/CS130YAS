package com.connexity.cs130.controller;

import com.connexity.cs130.amazon.SignedRequestsHelper;
import com.connexity.cs130.model.OfferResponse;
import com.connexity.cs130.model.ProductResponse;
import com.connexity.cs130.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import com.connexity.cs130.model.User;
import com.connexity.cs130.service.UserService;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Controller;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.servlet.ModelAndView;

import com.connexity.cs130.model.User;
import com.connexity.cs130.service.UserService;


/**
 * Created by 161497 on 4/20/17.
 */
@Controller
public class ApiController {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    UserService userService;

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

    @RequestMapping("/")
    public String landing(Map<String, Object> context) {

        loginToggle(context);
        return "index";
    }

    @RequestMapping("/searchInitial")
    public String search(@RequestParam("keyword") String keyword, Map<String, Object> context) {
        String sort = "relevancy_desc";
        return getProductResponse(keyword, sort, context, 0,"", "");
    }

    @RequestMapping("/search")
    public String search(@RequestParam("keyword") String keyword, Map<String, Object> context, @RequestParam("minPrice") String minPrice, @RequestParam("maxPrice") String maxPrice, @RequestParam("sort") String sort) {
        return getProductResponse(keyword, sort, context, 0,minPrice, maxPrice);
    }

    @RequestMapping("/nextPage")
    public String nextPage(@RequestParam("keyword") String keyword, @RequestParam("next") int pageNo, Map<String, Object> context, @RequestParam("minPrice") String minPrice, @RequestParam("maxPrice") String maxPrice, @RequestParam("sort") String sort) {
        return getProductResponse(keyword, sort, context,  pageNo + 1 , minPrice, maxPrice);
    }

    @RequestMapping("/prevPage")
    public String prevPage(@RequestParam("keyword") String keyword, @RequestParam("prev") int pageNo, Map<String, Object> context, @RequestParam("minPrice") String minPrice, @RequestParam("maxPrice") String maxPrice, @RequestParam("sort") String sort) {
        if (pageNo - 1 < 0)
            return getProductResponse(keyword, sort, context,  0, minPrice, maxPrice);
        else
            return getProductResponse(keyword, sort, context,  pageNo - 1 , minPrice, maxPrice);
    }

    @RequestMapping("/searchId")
    public String IdSearch(@RequestParam(value = "id") String id, Map<String, Object> context, @RequestParam(value = "addItem", required = false) String addId) {

        if (addId != null){
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findUserByEmail(auth.getName());

            if (user == null) {
                return "login";
            }

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/yas", "cs130", "password");

                PreparedStatement stmt = con.prepareStatement("INSERT INTO wishlist (userID, productID) VALUES (?, ?)");
                stmt.setInt(1,user.getId());
                stmt.setString(2,addId);
                stmt.execute();
                con.close();
            } catch(Exception e){ System.out.println(e);}
        }
        return getIdResponse(id, context);
    }

    @RequestMapping("/profile")
    public String profile(@RequestParam(value = "id", required = false) String id, Map<String, Object> context) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());

        loginToggle(context);

        if (user == null) {
            return "login";
        }

        context.put("first_name", user.getName());
        context.put("last_name", user.getLastName());
        context.put("email", user.getEmail());

        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/yas","cs130","password");

            Statement statement = con.createStatement();
            // Delete item first
            if (id != null) {
                String query = "delete from wishlist where userID = ? and productID = ?";
                PreparedStatement preparedStmt = con.prepareStatement(query);
                preparedStmt.setInt(1, user.getId());
                preparedStmt.setString(2, id);
                preparedStmt.execute();
            }

            // SELECTING FROM DB

            // Queries the database
            ResultSet rs = statement.executeQuery("select * from wishlist where userID=" + user.getId());
            ArrayList<String> productIDs = new ArrayList<>();

            // Grabs all the product IDs for the user's wishlist
            while(rs.next()) {
                //System.out.println(rs.getInt(1) + "  " + rs.getInt(2));
                productIDs.add(rs.getString(2));
            }
            con.close();

            ArrayList<OfferResponse.Offer> wishlist = new ArrayList<>();

            // Query Connexity API for each
            for (String productID: productIDs) {
                OfferResponse response;
                String url = createProductIdRequestUrl(productID);
                response = restTemplate.getForEntity(url, OfferResponse.class).getBody();

                System.out.println(response);
                // Add each response to wishlist arraylist
                for (int i = 0; i != response.offers.offer.size(); i++) {
                    wishlist.add(response.offers.offer.get(i));
                }

            }

            context.put("wishlist", wishlist);
        }
        catch(Exception e){ System.out.println(e);}

        return "profilePage";
    }

    private String getProductResponse(String keyword, String sort, Map<String,Object> context, int page, String minPrice, String maxPrice) {

        loginToggle(context);

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

        if (prs.size() == 0) {
            context.put("message.totalResults", "0");
            context.put("pageNo", "0");
            return "dynamicSearch";
        }

        context.put("products", prs);
        context.put("message", response.products);
        context.put("searchTerm", keyword);
        context.put("pageNo", page);

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

    public void ebayTry(Map<String,Object> context, String url, String contextName) {

        String lowestPrice = "";
        String ebayURL = "";
        String imageURL = "";

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(url);
            Node lowestNewPriceNode = doc.getElementsByTagName("convertedCurrentPrice").item(0);
            Node itemURLNode = doc.getElementsByTagName("viewItemURL").item(0);
            Node itemImageNode = doc.getElementsByTagName("galleryURL").item(0);

            if (lowestNewPriceNode != null) {
                lowestPrice = lowestNewPriceNode.getTextContent();
                context.put(contextName, "$"+ lowestPrice);
            }
            else {
                context.put(contextName, "");
            }

            if (itemURLNode != null) {
                ebayURL = itemURLNode.getTextContent();
                context.put("ebayURL", ebayURL);
            }
            else {
                context.put("ebayURL", "");
            }

            if (itemImageNode != null) {
                imageURL = itemImageNode.getTextContent();
                context.put("ebayImage", imageURL);
            }
            else {
                context.put("ebayImage", "");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void getEbayResponse(Map<String,Object> context, String upcID) {

        if (upcID == "N/A") {
            context.put("ebayNewPrice", "");
            context.put("ebayUsedPrice", "");
            context.put("ebayRefurbishedPrice", "");
            context.put("ebayURL", "");
            return;
        }

        String urlNew = createEbayRequestUrl(upcID, "new");
        String urlUsed = createEbayRequestUrl(upcID, "used");
        String urlRefurb = createEbayRequestUrl(upcID, "refurbished");
        ebayTry(context, urlNew, "ebayNewPrice");
        ebayTry(context, urlUsed, "ebayUsedPrice");
        ebayTry(context, urlRefurb, "ebayRefurbishedPrice");
    }

    public void getAmazonResponse(Map<String,Object> context, String upcID) {
        String requestUrl;
        String lowestNewPrice = "";
        String lowestUsedPrice = "";
        String lowestRefurbishedPrice = "";
        String amazonURL = "";
        String amazonIMG = "";

        if (upcID == "N/A") {
            context.put("amazonNewPrice", "");
            context.put("amazonUsedPrice", "");
            context.put("amazonRefurbishedPrice", "");
            context.put("amazonURL", "");
            context.put("amazonIMG", "");
            return;
        }

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

        /* The helper can sign requests in two forms - map form and string form */

        /*
         * Here is an example in map form, where the request parameters are stored in a map.
         */
        Map<String, String> params = new HashMap<String, String>();
        params.put("Service", "AWSECommerceService");
        params.put("AssociateTag", amazonAssociateTag);
        params.put("Operation", "ItemLookup");
        params.put("IdType", "UPC");
        params.put("SearchIndex", "All");
        params.put("ItemId", upcID);
        params.put("ResponseGroup", "Offers, Images");

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
            Node amazonIMGNode = doc.getElementsByTagName("URL").item(2);
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
            if (amazonIMGNode != null) {
                amazonIMG = amazonIMGNode.getTextContent();
                context.put("amazonIMG", amazonIMG);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String createEbayRequestUrl(String upc, String condition) {
        String conditionID = "";
        switch (condition) {
            case "new":
                conditionID = "1000";
                break;
            case "refurbished":
                conditionID = "2000";
                break;
            case "used":
                conditionID = "3000";
                break;
        }

        String url = "http://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME="  +
        "findItemsByKeywords&SERVICE-VERSION=1.12.0&SECURITY-APPNAME=" +
        "DanielOn-cs130yas-PRD-569e0185b-ac847d8c&RESPONSE-DATA-FORMAT=" +
        "XML&REST-PAYLOAD&paginationInput.entriesPerPage=10&keywords=" + upc  +
        "&itemFilter(0).name=ListingType&itemFilter(0).value(0)=FixedPrice&itemFilter(0).value(1)=" +
        "StoreInventory&itemFilter(1).name=Condition&itemFilter(1).value=" + conditionID +
        "&sortOrder=PricePlusShippingLowest";

        return url;
    }

    private String createProductIdRequestUrl(String id) {
        String url = "http://catalog.bizrate.com/services/catalog/v1/api/product?apiKey="
                + apiKey + "&publisherId=" + publisherId + "&=productId=" + id + "&=productIdType=SZOID";
        return url;
    }

    private String getIdResponse(String id, Map<String,Object> context) {
        OfferResponse response;
        System.out.println(id);
        String url = createProductIdRequestUrl(id);
        response = restTemplate.getForEntity(url, OfferResponse.class).getBody();

        //System.out.println(response);

        loginToggle(context);

        ArrayList<OfferResponse.Offer> prs = new ArrayList<>();

        for (int i = 0; i != response.offers.offer.size(); i++) {
            prs.add(response.offers.offer.get(i));
        }

        if (prs.size() == 0) {
            context.put("message", "Display 404 Page");
            return "itemPage";
        }

        context.put("products", prs);
        context.put("message", "");

        String upc = response.offers.offer.get(0).upc;
        getAmazonResponse(context, upc);
        getEbayResponse(context, upc);



        return "itemPage";
    }

    private void loginToggle(Map<String, Object> context) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());

        if (user == null)
            context.put("show-login", true);
        else
            context.put("username", user.getName());

    }
}