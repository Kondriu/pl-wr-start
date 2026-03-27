package org.example.recording;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.WaitUntilState;
import io.qameta.allure.Allure;
import org.example.base.BaseTestAllure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class AllureReportTest extends BaseTestAllure {

    @Test
    public void testLoginWithScreenshot() {
        Allure.step("1. Open login page", () -> {
            page.navigate("https://demoqa.com/login",
                    new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
        });

        Allure.step("2. fill login form", () -> {
            page.waitForSelector("#userName", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
            page.waitForSelector("#password", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
            page.fill("#userName", "testuser");
            page.fill("#password", "INVALID_PASSWORD");

        });

        Allure.step("3. click on enter button", () -> {
            page.waitForSelector("#login", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(5000));
            page.click("#login");

            page.waitForCondition(() -> page.evaluate("() => document.readyState").equals("complete"),
                    new Page.WaitForConditionOptions().setTimeout(5000));

        });

        Allure.step("4. assertion for success entrance", () -> {
            page.waitForSelector("#userName-value",
                    new Page.WaitForSelectorOptions()
                            .setState(WaitForSelectorState.VISIBLE)
                            .setTimeout(10000));
            Assertions.assertTrue(page.isVisible("#userName-value"),
                    "usr name wasn't display after login");
        });


    }
}
