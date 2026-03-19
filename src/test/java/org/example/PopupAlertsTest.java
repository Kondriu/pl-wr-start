package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PopupAlertsTest extends BaseTest {

    @Test
    @DisplayName("handling of dialogues")
    public void handlingOfDialogues(){
        ///  alersts

        page.navigate("https://demoqa.com/alerts");

        page.onceDialog(dialog -> {
            page.waitForTimeout(1000);
            assertEquals("alert", dialog.type());
            assertEquals("You clicked a button", dialog.message());
            dialog.accept();
        });

        page.click("#alertButton");


        ///  Confirm/accept

        page.onceDialog(alertDialog -> {
            page.waitForTimeout(1000);
            assertEquals("confirm", alertDialog.type());
            assertEquals("Do you confirm action?",  alertDialog.message());
            alertDialog.accept();
        });

        page.click("#confirmButton");
        assertEquals("You selected Ok", page.locator("#confirmResult").innerText());


        /// Confirm/cancel

        page.onceDialog(alertDialog -> {
            page.waitForTimeout(1000);
            assertEquals("confirm", alertDialog.type());
            alertDialog.dismiss();

        });
        page.click("#confirmButton");
        assertEquals("You selected Cancel", page.locator("#confirmResult").innerText());

        /// prompts
        page.onceDialog(alertDialog -> {
            page.waitForTimeout(1000);
            assertEquals("prompt",  alertDialog.type());
            assertEquals("Please enter your name",  alertDialog.message());
            alertDialog.accept("John Dick");

        });
        page.click("#promtButton");
        assertEquals("You entered John Dick", page.locator("#promptResult").innerText());

        ///  parallel
        page.onDialog(alertDialog -> {
            if ("alert".equals(alertDialog.type())) {
                alertDialog.accept();
            }
        });
        page.click("#timerAlertButton");
        page.waitForTimeout(6000);
    }
}
