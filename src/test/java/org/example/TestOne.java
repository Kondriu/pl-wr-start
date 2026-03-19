package org.example;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestOne {

    Playwright playwright;
    Browser browser;
    Page page;

    @BeforeEach
    public void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        //browser = playwright.chromium().launch();
        page = browser.newPage();
    }

    @Test
    public void shouldOpenBrowser() {
        page.navigate("https://google.com");
        String title = page.title();

        Assertions.assertEquals("Google", title);
    }

    @AfterEach
    public void tearDown() {
        browser.close();
        playwright.close();
    }
}
