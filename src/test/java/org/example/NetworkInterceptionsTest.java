package org.example;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.WaitUntilState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NetworkInterceptionsTest extends BaseTest{

    @Test
    void mockBooksApi(){
        // mocking resp
        page.route("**/BookStore/v1/Books", route -> {
            String mockResponse = """
                    {
                        "books": [
                                {
                                    "isbn": "test-isbn-123",
                                    "title": "Playwrighgt для QA",
                                    "subtitle": "Тестирование с удовольствием",
                                    "author": "Иван Тестировщиков",
                                    "publisher_date": "2023-07-16T0:48:39.000Z",
                                    "publisher": "Тест-Издат",
                                    "pages": "333",
                                    "description": "Лучшая книга по тестированию с Playwright",
                                    "website": "https://example.com",
                                }
                            ]
                    }
                    """;
            route.fulfill(new Route.FulfillOptions()
                    .setStatus(200)
                    .setContentType("application/json")
                    .setBody(mockResponse)
            );
        });

        // navigate with awaiting DOM content loaded
        page.navigate("https://demoqa.com/books", new Page.NavigateOptions()
                .setWaitUntil(WaitUntilState.DOMCONTENTLOADED));

        // simulate delay
        page.waitForTimeout(2000);

        // waiting displaying data table
        Locator bookRow = page.locator(".rt-tbody .rt-tr-group").first();
        bookRow.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(1000));

        //assert
        Locator bookTitle = bookRow.locator(".rt-td:nth-child(2)");
        assertEquals("Playwrighgt для QA", bookTitle.textContent().trim());
    }
}
