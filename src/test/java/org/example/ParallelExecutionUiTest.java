package org.example;

import org.example.base.BaseParallelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.CONCURRENT)

public class ParallelExecutionUiTest extends BaseParallelTest {

    @Test
    @DisplayName("Check google headers")
    void checkGoogleHeaders() {
        page.navigate("https://google.com");
        page.waitForTimeout(5000);
        assertTrue(page.title().contains("Google"));
    }

    @Test
    @DisplayName("Check playwright docs")
    void checkPlaywrightDocs() {
        page.navigate("https://playwright.dev/java");
        page.waitForTimeout(5000);
        assertTrue(page.title().contains("Playwright"));
    }

    @Test
    @DisplayName("Check wikipedia")
    void checkWikipedia() {
        page.navigate("https://wikipedia.org");
        page.waitForTimeout(5000);
        assertTrue(page.title().contains("Wikipedia"));
    }
}
