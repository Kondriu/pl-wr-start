package org.example.trace;


import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.WaitUntilState;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TracingDebug {

    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;

    @BeforeAll
    static void beforeAll() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
        );
    }

    @BeforeEach
    void beforeEach(TestInfo testInfo) {
        context = browser.newContext();
        context.tracing().start(
                new Tracing.StartOptions()
                        .setScreenshots(true)
                        .setSnapshots(true) //записывает  DOM
                        .setSources(true) //сохраняет код страницы

        );
        page = context.newPage();
    }

    @Test
    void succeedsLogin(){
        Allure.step("1. open page ", () -> {
            page.navigate("https://demoqa.com/login",
                    new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
        });

        Allure.step("2. fill values ", () -> {
            page.locator("#userName").waitFor();
            page.fill("#userName", "testuser");
            page.fill("#password", "Test@123");

        });

        Allure.step("3. submit login ", () -> {
            Locator button = page.locator("#login");
            button.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
            button.click();
        });

        Allure.step("4. assert values ", () -> {
            Locator userName = page.locator("#userName-value");
            userName.waitFor( new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(3000));
            Assertions.assertTrue(userName.isVisible());
        });

    }


    @AfterEach
    void saveTrace(TestInfo testInfo) throws IOException {
        String testName = testInfo.getTestMethod().get().getName();
        Path traceDir = Paths.get("traces");
        Files.createDirectories(traceDir);

        Path traceFile = traceDir.resolve(testName + ".zip");
        context.tracing().stop(new Tracing.StopOptions().setPath(traceFile));
        attachTrace(traceFile.getFileName().toString());

        context.close(); // контекст надо закрывать только после сохранения трассировки
    }

    @AfterAll
    static void afterAll() {
        playwright.close();
    }

    @Attachment(value = "Trace for {fileName}", type = "appication/zip") //добавить в отчет аллюра
    private byte[] attachTrace(String fileName) throws IOException {
        return Files.readAllBytes(Paths.get("traces/" + fileName));
    }
}
