package org.example.mocking;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.example.base.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class DemoQaTestFormTest extends BaseTest {

    record FormatData(String fullName, String email, String currentAddress, String permanentAddress) {
    }

    static Stream<Arguments> testFormatDataProvider() {
        return Stream.of(
                Arguments.of(new FormatData("Иван Дурак", "ivan@test.com", "Lublyanska 20", "Givno str 11")),
                Arguments.of(new FormatData("Joshn Doe", "john@mail.com", "Pokoyi 3", "Pokoyu 3"))
        );
    }

    @ParameterizedTest(name = "Тест формы {0}")
    @MethodSource("testFormatDataProvider")
    void testTextBox(FormatData data) {

        page.navigate("https://demoqa.com/text-box");
        // Ожидание готовности формы перед заполнением
        page.waitForSelector("#userName",
                new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));

        // fill form
        page.locator("#userName").fill(data.fullName());
        page.locator("#userEmail").fill(data.email());
        page.locator("#currentAddress").fill(data.currentAddress());
        page.locator("#permanentAddress").fill(data.permanentAddress());

        // click with loader handling
        Locator submitButton = page.locator("#submit");
        submitButton.click();

        // wait for results
        Locator output = page.locator("#output");
        output.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));

        // assert
        String resultText = output.innerText();
        Assertions.assertTrue(resultText.contains(data.fullName()));
        Assertions.assertTrue(resultText.contains(data.email()));
        Assertions.assertTrue(resultText.contains(data.currentAddress()));
        Assertions.assertTrue(resultText.contains(data.permanentAddress()));

    }
}
