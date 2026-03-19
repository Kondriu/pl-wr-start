package org.example;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestTwo {

    public static Playwright  playwright;
    public static Browser browser;
    public BrowserContext context;
    public Page page;


    @BeforeAll
    public static void init() {

        ArrayList<String> argument =  new ArrayList<>();
        argument.add("--start-maximized");

        playwright = Playwright.create();
        browser = playwright
                .chromium()
                .launch(new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setSlowMo(500)); // замедление для демонстрации
    }

    @AfterAll
    public static void destroy() {
        browser.close();
        playwright.close();
    }

    //изоляция каждого теста, с помощью создаваемого и уничтожаемого контекста
    @BeforeEach
    public void createContextAndPage() {
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterEach
    public void destroyContext() {
        context.close();
    }

    @Test
    @DisplayName("Основы Playwright: навигация, поиск элементов и взаимодействие")
    public void testPlaywrightFundamentals() {
        ///     1. Навигация и ожидания
        page.navigate("https://demoqa.com");

        //  Явное ожидание вместо Thread.sleep()
        page.waitForSelector(".card", new Page.WaitForSelectorOptions().setTimeout(10000));
        // явное ожидание загрузки страницы

        ///     2. Поиск Элементов
        // стабильный CSS-селектор
        Locator elementCard = page.locator("div.card:has-text('Elements')");
        elementCard.click();

        //Поиск по тексту
        //page.locator("li.btn-light:has-text('Text Box')").click();
        page.locator("span:has-text('Text Box')").click();

        //page.locator("span:has-text()")
        // (Best Practice) поиск по роли ARIA
        Locator fullNameLabel = page.getByRole(AriaRole.TEXTBOX,
                new Page.GetByRoleOptions().setName("Full Name"));

        ///  Взаимодействие с элементами
        // fill vs type
        fullNameLabel.fill("Иванов Бьорн"); // ввести текст

        Locator emailInput = page.locator("#userEmail");
        emailInput.type("test@example.com"); //посимвольный ввод

        Locator addressInput = page.locator("#currentAddress");
        addressInput.fill("Awsome street 16");

        //button click
        Locator submitButton = page.locator("#submit");
        submitButton.click();

        ///     4. Валидация данных

        // ожидание появления результата
        page.waitForSelector("#output");
        // playwright по умолчанию ждет 30 секунд появления указанного элемента

        //check text
        Locator nameResult =  page.locator("#name");
        // textContent() или innerText()
        assertTrue(nameResult.textContent().contains("Иванов Бьорн"), "Не верное имя в результате");

        //check attribute
        Locator emailResult =  page.locator("#email");
        assertEquals("test@example.com",
                emailResult.textContent().replace("Email:", "").trim(),
                "не верный email в результате"
        );

        ///     5. Работа с чекбоксами и радио-баттонами

        page.locator("li:has-text('Check Box')").click();

        // check box:
        Locator homeCheckbox = page.locator("label:has-text('Home') .rct-checkbox");
        homeCheckbox.check();
        assertTrue(homeCheckbox.isChecked(), "Should be selected");


        //radio-button
        page.locator("span:has-text('Radio Button')").click();
        //page.locator("li:has-text('Radio Button')").click();
        Locator impressiveRadio = page.locator("label:has-text('Impressive')");
        impressiveRadio.check();
        assertTrue(impressiveRadio.isChecked(), "'Impressive' Should be selected");
    }
}
