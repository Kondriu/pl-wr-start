package org.example;

import com.microsoft.playwright.Locator;
import org.example.base.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FormElementsTest extends BaseTest {
    @Test
    @DisplayName("Working with elements of form")
    void testFormElements() {
        page.navigate("https://demoqa.com/automation-practice-form");

        /// radio buttons

        page.locator("label[for='gender-radio-1']").click();
        boolean isYesSelected = page.locator("#gender-radio-1").isChecked();

        assertTrue(isYesSelected, "Radio button 'Male' should be selected");

        ///Checkboxes
        //select two together
        page.locator("label[for='hobbies-checkbox-1']").click();
        page.locator("label[for='hobbies-checkbox-3']").click();

        //check status
        boolean isSPortSelected = page.locator("#hobbies-checkbox-1").isChecked();
        boolean isMusicSelected = page.locator("#hobbies-checkbox-3").isChecked();

        assertTrue(isSPortSelected, "Checkbox 'Sport' should be selected");
        assertTrue(isMusicSelected, "Checkbox 'Music' should be selected");

        //uncheck one checkbox
        page.locator("label[for='hobbies-checkbox-3']").click();
        boolean isMusicUnchecked =  !page.locator("#hobbies-checkbox-3").isChecked();
        assertTrue(isMusicUnchecked, "Checkbox 'Music' should be unchecked");


        /// Dropdowns
        // expand list
        Locator dropdownState = page.locator("//div[@id='state']");

        dropdownState.click();

        //select one option
        page.getByText("NCR").click();
        //dropdownState.selectOption("NCR"); // - это только для реальной html формы select, если дропдаун сделан на (React / Angular / Vue) - надо прямо кликать по элемнету

        String selected = page.locator("#state div.css-1dimb5e-singleValue").innerText();

        assertEquals("NCR", selected, " 'NCR' should be selected");

        //
        page.locator("//div[@id='city']").click();
        page.getByText("Delhi").click();
        //page.locator("#city").selectOption(new SelectOption().setLabel("Delhi"));

        String selectedCity = page.locator("#city div.css-1dimb5e-singleValue").innerText();
        assertEquals("Delhi", selectedCity, " 'Delhi' should be selected");
    }
}
