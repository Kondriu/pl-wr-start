package org.example;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;

public class BaseTest {

    public static Playwright playwright;
    public static Browser browser;
    public BrowserContext context;
    public Page page;

    @BeforeAll
    public static void init() {

        ArrayList<String> argument = new ArrayList<>();
        argument.add("--start-maximized");

        playwright = Playwright.create();
        browser = playwright
                .firefox()
                .launch(new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setSlowMo(500)); // замедление для демонстрации
    }

//    @AfterAll
//    public static void destroy() {
//        browser.close();
//        playwright.close();
//    }

    //изоляция каждого теста, с помощью создаваемого и уничтожаемого контекста
    @BeforeEach
    public void createContextAndPage() {
        //context = browser.newContext();
        //добавление автоматического принятия загрузок
        context = browser.newContext(new Browser.NewContextOptions().setAcceptDownloads(true));
        page = context.newPage();
    }

    @AfterEach
    public void tearDown() {
        if (page != null) page.close();
        if (context != null) context.close();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
}
