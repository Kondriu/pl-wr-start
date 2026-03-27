package org.example;

import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import org.example.base.BaseTest;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class IFrameTest extends BaseTest {
    @Test
    public void testIFrame() {
        page.navigate("https://demoqa.com/frames");

        /// first frame:
        FrameLocator firsFrame = page.frameLocator("#frame1");

        //text inside frame
        assertThat(firsFrame.locator("#sampleHeading"))
                .hasText("This is a sample page");

        //borders
        page.locator("#frame1").evaluate("e => e.style.border = '3px solid red'");

        //inner frames
        page.locator("'Nested Frames'").click();

        // hierarchy
        FrameLocator parentFrame = page.frameLocator("#frame1");
        FrameLocator childFrame = parentFrame.frameLocator("iframe");

        //test in child frame
        assertThat(childFrame.locator("body"))
                .hasText("Child Iframe");


        // make screenshot of frame
        parentFrame.locator("body")
                .screenshot(new Locator.ScreenshotOptions()
                        .setPath(Paths.get("parent_frame.png")));

        ///  dynamic frames
        page.frameLocator("//iframe[contains(@id, 'frame1')]")
                .locator("body")
                .click();
        
        System.out.println("steps passed successfully");

    }

}
