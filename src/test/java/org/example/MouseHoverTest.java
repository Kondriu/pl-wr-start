package org.example;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.KeyboardModifier;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MouseHoverTest extends BaseTest {

    @Test
    public void testHoverActions(){

        page.navigate("https://demoqa.com/menu");

        ///  base hover

        Locator mainItem = page.locator("'Main Item 2'");
        mainItem.hover(); // mouse hover

        //check submenu appering
        Locator submenu = page.locator("'SUB SUB LIST »'");
        assertTrue(submenu.isVisible(), "should be displayed after mouse hover");

        // mouse move on element sub menu
        submenu.hover();

        //assert inner menu
        Locator subItem = page.locator("'Sub Sub Item 1'");
        subItem.hover();
        assertTrue(subItem.isVisible(), "should be displayed after mouse hover");

        /// modified clicks
        page.navigate("https://demoqa.com/links/");

        // --- Shift+Click
        Page newPage = context.waitForPage(() -> {
            page.locator("#simpleLink").click(new Locator.ClickOptions()
                    .setModifiers(Arrays.asList(KeyboardModifier.SHIFT)));
        });

        //validate new tab
        newPage.waitForLoadState();
        assertEquals("https://demoqa.com/", newPage.url(), "should be open new tab");

        //bring back focus
        page.bringToFront();

        // Ctrl+Click - add to bookmarks
        page.locator("#dynamicLink").click(new Locator.ClickOptions()
                .setModifiers(Arrays.asList(KeyboardModifier.CONTROL)));

        // no new page
        assertEquals("https://demoqa.com/links/", page.url(), "should be on old tab");

        ///  elements tree

        page.navigate("https://demoqa.com/checkbox");

        page.locator("//span[@class='rc-tree-switcher rc-tree-switcher_close']").first().click();
        page.locator("//span[@class='rc-tree-switcher rc-tree-switcher_close']").nth(1).click(); // documents
        page.locator("//span[@class='rc-tree-switcher rc-tree-switcher_close']").nth(3).click(); // workspace


        ///  ctrl+click for multiple choice

        Locator desktopLabel =  page.locator("//span[@Aria-label='Select Desktop']");
        desktopLabel.click(new Locator.ClickOptions()
                .setModifiers(Arrays.asList(KeyboardModifier.CONTROL)));

        // assert
        Locator desctopcheckbox =  page.locator("//span[@Aria-label='Select Desktop']");
        assertTrue(desctopcheckbox.isChecked(), "should be checked desktop after Ctrl+Click");

        // documents
        Locator documentsLabel = page.locator("//span[@Aria-label='Select Documents']");
        documentsLabel.click(new Locator.ClickOptions()
                .setModifiers(Arrays.asList(KeyboardModifier.CONTROL)));

       // Locator documentCheckbox = page.locator("//span[@Aria-label='Select Documents']");
        assertTrue(documentsLabel.isChecked(), "should be checked documents after Ctrl+Click");

    }
}
