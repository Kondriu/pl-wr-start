package org.example;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.LocatorAssertions;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class WaitersTest extends BaseTest {


    @Test
    void testsWaitersTest(){
        ///     1.  Автоматические ожидания
        page.navigate("https://demoqa.com/dynamic-properties");

        //кнопка станет неактивной через 5 секунд
        page.locator("#enableAfter").click();
        //кнопка станет активной через 5 сек.


        ///     2. Явные ожидания для сложных условий

        //ждем появления элеменат с таймаутом 7 сек:
//        page.waitForSelector("#visibleAfter", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.valueOf("visible")).setTimeout(7000));

        //ждем изменения ЦСС-свойств (кастомное условие)
        page.waitForFunction(
                "() => window.getComputedStyle(document.querySelector('#colourChange')).colour === 'rgb(255, 0, 0)'",
                new Page.WaitForSelectorOptions().setTimeout(8000)
        );

        // Ожидание перехода на страницу
        page.waitForURL(
                "**/checkout/confirmation",
                new Page.WaitForURLOptions().setTimeout(5000)
        );

/*
        // Ожидание конкретного условия через JS
        page.waitForFunction("() => document.querySelector('.loader').style.display === 'none'",
                new Page.WaitForFunctionOptions().setTimeout(5000));

        // Ожидание сетевого ответа
        page.waitForResponse(response -> response.url().contains("/api/save") && response.status() == 200,
                () -> page.click("#save-btn"));
*/

        ///     3. Умные ассерты PW

        // Проверка текста с автоматическим ожиданием
        assertThat(page.locator("//h1[@class='text-center']/../p")).hasText("This text has random Id", new LocatorAssertions.HasTextOptions().setTimeout(5000));

        //
        Locator checkoutButton = page.locator("#visibleAfter");

        assertThat(checkoutButton).isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(3000));
        assertThat(checkoutButton).isEnabled(new LocatorAssertions.IsEnabledOptions().setTimeout(3000));

        assertThat(checkoutButton).hasAttribute("data-status", "active", new LocatorAssertions.HasAttributeOptions().setTimeout(3000));

    }
}
