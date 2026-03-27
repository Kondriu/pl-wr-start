package org.example.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.Cookie;
import com.microsoft.playwright.options.RequestOptions;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.WaitUntilState;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApiAndUitest {

    private static final String BASE_URL = "https://demoqa.com";
    private static final String LOGIN_URL = BASE_URL + "/Account/v1/Login";
    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "Test@123";
    private static final int TIMEOUT = 30000;
    private static final long COOKIE_EXPIRATION_DAYS = 1;

    static Playwright playwright;
    static Browser browser;
    static APIRequestContext apiRequestContext;
    static String authToken;
    static String userId;
    static String userName;

    BrowserContext browserContext;
    Page page;

    @BeforeAll
    static void globalSetup() {
        playwright = Playwright.create();
        authenticateUser();
        setupApiContest();
        launchBrowser();
    }

    @BeforeEach
    void setupBrowserContext() {
        browserContext = browser.newContext();
        addAuthCookiesToContest();
        page = browserContext.newPage();
    }

    @Test
    void verifyProfileAfterLogin() {
        navigateToProfile();
        verifyUserProfileDate();
    }

    private void navigateToProfile() {
        Allure.step("1. go to profile by UI", () -> {
            page.navigate(BASE_URL + "/profile", new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));

            waitForLoaderToDisappear();
            waitForProfileWrapper();

            Locator userNameLocator = page.locator("#userName-value");
            assertTrue(userNameLocator.isVisible(), "element \"#userName-value\" not visible");

            String actualUserName = userNameLocator.textContent();
            assertNotNull(actualUserName, "user name wasn't display");
            System.out.println("displayed user mane is: " + actualUserName);
        });
    }

    private void verifyUserProfileDate() {
        Allure.step("2. verify user data", () -> {
            JsonObject apiUserData = fetchDataFromApi();
            String apiUserName = apiUserData.get("username").getAsString();

            String uiUserName = page.textContent("#userName-value");
            assertEquals(apiUserName, uiUserName, "ui name not math to api");
        });
    }

    @AfterEach
    void closeBrowserContext() {
        browserContext.close();
    }

    @AfterAll
    static void globalCleanup() {
        closeResources();
    }


    private JsonObject fetchDataFromApi() {
        APIResponse apiResponse = apiRequestContext.get("/Account/v1/User/" + userId);
        assertEquals(200, apiResponse.status());
        return JsonParser.parseString(apiResponse.text()).getAsJsonObject();
    }

    private static void closeResources() {
        if (apiRequestContext != null) {
            apiRequestContext.dispose();
        }
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    private static void authenticateUser() {
        APIRequestContext tempContext = playwright.request().newContext();

        try {
            APIResponse authResponse = tempContext.post(
                    LOGIN_URL,
                    RequestOptions.create().setData(Map.of(
                            "userName", USERNAME,
                            "password", PASSWORD
                    ))
            );
            assertEquals(200, authResponse.status(), "Auth error");

            JsonObject responseJSON = JsonParser.parseString(authResponse.text()).getAsJsonObject();
            authToken = responseJSON.get("token").getAsString();
            userId = responseJSON.get("userId").getAsString();
            userName = responseJSON.get("username").getAsString();

            assertNotNull(authToken, "auth token is null");
            assertNotNull(userId, "no user Id");

        } finally {
            tempContext.dispose();
        }
    }

    private static void setupApiContest() {
        apiRequestContext = playwright.request().newContext(new APIRequest.NewContextOptions()
                .setBaseURL(BASE_URL)
                .setExtraHTTPHeaders(Map.of(
                        "Authorization", "Bearer " + authToken
                )));
    }

    private static void launchBrowser(){
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setSlowMo(500)
        );
    }

    private void addAuthCookiesToContest(){
            long expirationTime = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(COOKIE_EXPIRATION_DAYS);
            Date expirationDate = new Date(expirationTime);

            browserContext.addCookies(Arrays.asList(
                    createCookie("Tocken", authToken, expirationDate),
                    createCookie("userID", userId, expirationDate),
                    createCookie("userName", userName, expirationDate),
                    createCookie("expires", expirationDate.toString(), expirationDate)
            ));
    }

    private Cookie createCookie(String name, String value, Date expires )
    {
        return new Cookie(name, value)
                .setDomain("demoqa.com")
                .setPath("/")
                .setExpires(expires.getTime() / 1000);
    }

    private void waitForLoaderToDisappear(){
        page.locator(".loader:has-text('Loading')")
                .waitFor(new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.HIDDEN)
                        .setTimeout(TIMEOUT));
    }

    private void waitForProfileWrapper(){
        //page.locator(".profile-wrapper")
        page.locator("#notLoggin-wrapper")
                .waitFor(new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(TIMEOUT));
    }
}
