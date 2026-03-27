package org.example;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.ViewportSize;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.WaitUntilState;
import org.example.base.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BrowserManagement extends BaseTest {


    Page.WaitForSelectorOptions waitForSelectorOptions = new Page.WaitForSelectorOptions().setTimeout(100000);
    Page.NavigateOptions navigateOptions = new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED);


    @Test
    @DisplayName("Управление браузером: контексты станиц")
    void testAdvancedBrowserManagement() {
        ///     1. Работа с контекстами

        BrowserContext context1 = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1280, 760)
                .setLocale("en-US")
                .setPermissions(List.of("geolocation"))
        );

        Page page1 = context1.newPage();

        //изменение стратегии ожидания загрузки
        page1.navigate("https://demoqa.com/login",
                new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));

        //явное ожидание появление формы
        page1.waitForSelector("#userForm",
                new Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.ATTACHED)
                        .setTimeout(15000));

        //auth
        page1.fill("#userName", "testUser");
        page1.fill("#password", "Test123!");

        // Ожидание кликабельной кнопки

        page1.waitForSelector("#login", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.ATTACHED));
        page1.click("#login");

        // Ожидание завершения авторизации
        page1.waitForSelector("#userName-value", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));

        assertTrue(page1.textContent("#userName-value").contains("testuser"),
                "user should be authorised in context 1");


        ///     2. Работа со страницами

        Page page2 = context1.newPage();
        page2.navigate("http://demoqa.com/profile", new Page.NavigateOptions()
                .setWaitUntil(WaitUntilState.DOMCONTENTLOADED));

        page2.waitForSelector("#userName-value", new Page.WaitForSelectorOptions()
                .setTimeout(10000));


        ///     3. isolated contexts

        BrowserContext context2 = browser.newContext();
        Page page3 = context2.newPage();

        page3.navigate("https://demoqa.com/login", new Page.NavigateOptions()
                .setWaitUntil(WaitUntilState.DOMCONTENTLOADED));

        page3.waitForSelector("#userForm", waitForSelectorOptions);

        assertTrue(page3.isVisible("#userForm"),
                "user should be visible in context 2");


        ///     4. device emulation
        // iphone12

        Browser.NewContextOptions iPhone12ProOptions = new Browser.NewContextOptions()
                .setUserAgent("Mozilla/5.0 (iPhone OS 14_0 like Mac OS X) " +
                        "AppleWebKit/605.1.15 (KHTML. like Gecko) Version/14.0 Mobile/15E148 Safari/604.1 )")
                .setViewportSize(390, 844)
                .setDeviceScaleFactor(3)
                .setIsMobile(true)
                .setHasTouch(true);

        BrowserContext iPhone12ProContext = browser.newContext(iPhone12ProOptions);
        Page mobilePage = iPhone12ProContext.newPage();

        mobilePage.navigate("http://demoqa.com/", navigateOptions);

        ViewportSize size = mobilePage.viewportSize();

        assertEquals(390, size.width, "width should be iPhone");
        assertEquals(844, size.height, "height should be iPhone");

        mobilePage.tap("text=Elements");
        mobilePage.waitForSelector(".element-list", waitForSelectorOptions);

        assertTrue(mobilePage.isVisible(".element-list"), "mobile menu should be visible");

        ///     5. Сохранение состояния

        context1.storageState(new BrowserContext.StorageStateOptions()
                .setPath(Paths.get("auth-state.json")));

        BrowserContext restoredContext = browser.newContext(
                new Browser.NewContextOptions()
                        .setStorageStatePath(Paths.get("auth-state.json"))
        );

        // close context
        context1.close();
        context2.close();
        iPhone12ProContext.close();
        restoredContext.close();

    }
}
