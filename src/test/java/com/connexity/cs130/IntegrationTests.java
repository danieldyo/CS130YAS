package com.connexity.cs130;

import com.connexity.cs130.model.User;
import com.connexity.cs130.service.UserService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IntegrationTests {

    @Autowired
    private UserService userService;

    private WebDriver webDriver = new PhantomJSDriver();

    @BeforeClass
    public static void beforeClass() throws Exception {
        DemoCs130Application.main(new String[]{});
    }

    @AfterClass
    public static void afterClass() throws Exception {
        DemoCs130Application.exit();
    }


    @After
    public void tearDown() {
        try {
            webDriver.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        try {
            webDriver.quit();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Test
    public void visitIndexPage() throws Exception {
        // basic loading, existence of login link
        webDriver.get("http://localhost:8080/");
        WebElement link = (new WebDriverWait(webDriver, 10)).
                until(ExpectedConditions.presenceOfElementLocated(By.partialLinkText("Login")));

        Assert.assertThat(link.getText(), is(equalTo("Login")));
    }

    @Test
    public void registrationAndLogin() throws Exception {
        String firstName = getSaltString(20);
        String lastName = getSaltString(20);
        String email = getSaltString(10) + "@" + getSaltString(10) + ".com";
        String password = getSaltString(20);

        // go to login page
        webDriver.get("http://localhost:8080/registration");

        WebElement firstNameElem = waitFor().
                until(ExpectedConditions.presenceOfElementLocated(By.name("name")));
        WebElement lastNameElem = webDriver.findElement(By.name("lastName"));
        WebElement emailElem = webDriver.findElement(By.name("email"));
        WebElement passElem = webDriver.findElement(By.name("password"));

        firstNameElem.sendKeys(firstName);
        lastNameElem.sendKeys(lastName);
        emailElem.sendKeys(email);
        passElem.sendKeys(password);
        webDriver.findElement(By.className("account-submit")).click();

        // at this point, the account should exist
        // check by logging in

        // first, log out by deleting any cookies
        webDriver.manage().deleteAllCookies();

        webDriver.get("http://localhost:8080/login");
        emailElem = waitFor().
                until(ExpectedConditions.presenceOfElementLocated(By.name("email")));
        passElem = webDriver.findElement(By.name("password"));

        emailElem.sendKeys(email);
        passElem.sendKeys(password);

        webDriver.findElement(By.className("account-submit")).click();

        Assert.assertTrue("Good login redirects to /.",
            waitFor().until(
                ExpectedConditions.urlToBe("http://localhost:8080/")));

        // cleanup database
        User newUser = userService.findUserByEmail(email);
        userService.deleteUser(newUser);
    }

    protected WebDriverWait waitFor() {
        return new WebDriverWait(webDriver, 10);
    }

    protected WebDriverWait waitFor(int timeOutInSeconds) {
        return new WebDriverWait(webDriver, timeOutInSeconds);
    }

    protected String getSaltString(int len) {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < len) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }
}
