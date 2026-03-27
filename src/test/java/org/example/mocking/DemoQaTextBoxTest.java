package org.example.mocking;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.example.base.BaseTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class DemoQaTextBoxTest extends BaseTest {

    record FormatData(String fullName, String email, String currentAddress, String permanentAddress) {
    }

    static Stream<Arguments> testFormatDataProvider() {
        return Stream.of(
                Arguments.of(new DemoQaTestFormTest.FormatData("Иван Дурак", "ivan@test.com", "Lublyanska 20", "Givno str 11")),
                Arguments.of(new DemoQaTestFormTest.FormatData("Joshn Doe", "john@mail.com", "Pokoyi 3", "Pokoyu 3"))
        );
    }

    @ParameterizedTest(name = "Тест формы {0}")
    @MethodSource("testFormatDataProvider")
    void testTextBox(DemoQaTestFormTest.FormatData data) {

        page.navigate("https://demoqa.com/text-box");
        // Ожидание готовности формы перед заполнением
        page.waitForSelector("#userName",
                new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
        fillField("#userName", data.fullName());
        fillField("#userEmail", data.email());
        fillField("#currentAddress", data.currentAddress());
        fillField("#permanentAddress", data.permanentAddress());

        clickWithRetry("#submit", 3);
        waitForOutput();
    }

    void clickWithRetry(String locator, int attempts) {
        Locator button = page.locator(locator);
        for (int i = 0; i < attempts; i++) {
            try {
                button.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
                button.click();
                page.waitForTimeout(300);
                return;
            } catch (TimeoutError e) {
                if (i == attempts - 1) throw e;
                page.reload(); /// ??????? кто поля заполнит заново?
                page.waitForSelector("#userName", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
            }

        }
    }

    void fillField(String locator, String value) {
        Locator field = page.locator(locator);
        field.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        field.fill(value);
    }

    void waitForOutput() {
        try {
            page.waitForSelector("#output", new Page.WaitForSelectorOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(5000));

        } catch (TimeoutError e) {
            Locator output = page.locator("#output");
            output.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.ATTACHED)
                    .setTimeout(5000));

            page.waitForFunction("document.querySelector('#output').style.display !== 'none'");

        }
    }
}
