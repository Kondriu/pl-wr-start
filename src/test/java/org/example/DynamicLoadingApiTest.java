package org.example;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Tracing;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DynamicLoadingApiTest extends BaseTest {

    @Test
    void testDynamicLoadingApi() {
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true));

        //page = context.newPage();
        page.navigate("https://the-internet.herokuapp.com/dynamic_loading/1");

        page.onResponse(response -> {
            if (response.url().contains("/dynamic_loading")) {
                assertEquals(200, response.status(),
                        "Expected 200 OK, but got " + response.status() + "for URL " + response.url());
            }
        });

        page.click("#start button");
        Locator finishText = page.locator("#finish");
        finishText.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));

        assertEquals("Hello World!", finishText.textContent().trim(),
                "Expected Hello World!, but got " + finishText.textContent());

        context.tracing().stop(new Tracing.StopOptions()
                .setPath(Paths.get("trace/trace-success.zip")));
    }
}
