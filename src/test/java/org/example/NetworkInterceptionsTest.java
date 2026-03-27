package org.example;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.WaitUntilState;
import org.example.base.BaseTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NetworkInterceptionsTest extends BaseTest {

    @Test
    void mockBooksApi(){
        // mocking resp
        /**
         page.route() - Метод, который умеет перехватывать запросы к серверу (от страницы?)
         две звездочки - ** - означает любой урл, соответствующий такой маске.
         **/

        page.route("**/BookStore/v1/Books", route -> {
            String mockResponse = """
            {
                "books": [
                    {
                        "isbn": "9781449325862",
                        "title": "Git Для чайников",
                        "subTitle": "Гит для полных долбоебов",
                        "author": "Ктобы Подумал",
                        "publish_date": "2020-06-04T08:48:39.000Z",
                        "publisher": "O'Reilly Media",
                        "pages": 234,
                        "description": "This pocket guide is the perfect on-the-job companion to Git и бла бла бла",
                        "website": "https://labs.com/books/1230000000561/index.html"
                    }
                ]
            }
            """;


            /// route.fulfill() - возвращает наш вариант ответа от сервера, как будто-бы сервер так ответил.
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
        Locator bookRow = page.locator("tbody > tr").first();
        bookRow.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(1000));

        //assert
        Locator bookTitle = bookRow.locator(".mr-2 a");
        assertEquals("Git Для чайников", bookTitle.textContent().trim());
    }
}
