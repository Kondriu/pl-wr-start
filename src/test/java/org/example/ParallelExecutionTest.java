package org.example;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.CONCURRENT)
public class ParallelExecutionTest {

    private Playwright playwright;
    private Browser browser;

    @BeforeEach
    public void setupBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    }

    @AfterEach
    void teardownBrowser() {
        browser.close();
        playwright.close();
    }

    @Test
    @DisplayName("Check google headers")
    void checkGoogleHeaders() {
        BrowserContext context = browser.newContext();
        Page page = context.newPage();
        page.navigate("https://google.com");
        assertTrue(page.title().contains("Google"));
        context.close();
    }

    @Test
    @DisplayName("check playwright doc")
    public void checkPlaywrightDoc() {
        BrowserContext context = browser.newContext();
        Page page = context.newPage();
        page.navigate("https://playwright.dev/java");
        assertTrue(page.title().contains("Playwright"));
        context.close();

    }

    @Test
    @DisplayName("check wikipedia")
    public void checkWikipedia() {
        BrowserContext context = browser.newContext();
        Page page = context.newPage();
        page.navigate("https://wikipedia.org");
        assertTrue(page.title().contains("Wikipedia"));
        context.close();
    }
}
