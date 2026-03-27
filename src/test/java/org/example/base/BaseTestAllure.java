package org.example.base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Video;
import io.qameta.allure.Attachment;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.extension.TestWatcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BaseTestAllure {

    public static Playwright playwright;
    public static Browser browser;
    public BrowserContext context;
    public Page page;

    public Video video;
    public Path screenshotDir;

    @BeforeAll
    public static void init() {

        playwright = Playwright.create();
        browser = playwright
                .firefox()
                .launch(new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setSlowMo(500)); // замедление для демонстрации
    }


    //изоляция каждого теста, с помощью создаваемого и уничтожаемого контекста
    @BeforeEach
    public void createContextAndPage() {
        screenshotDir = Paths.get("screenshots/");
        try {
            Files.createDirectories(screenshotDir);

        } catch (IOException e) {
            throw new RuntimeException("Could not create screenshots directory", e);
        }

        //добавление автоматического принятия загрузок
        context = browser.newContext(new Browser.NewContextOptions()
                .setAcceptDownloads(true)
                .setRecordVideoDir(Paths.get("videos/"))
        );
        page = context.newPage();
        video = page.video();
    }

    @AfterEach
    public void tearDown(TestInfo testInfo) throws IOException {
        //if (page != null) page.close();
        if (context != null) {
            if (video != null) {
                String videoName = testInfo.getDisplayName() + ".webm";
                Path videoPath = Paths.get("videos/" + videoName);
                video.saveAs(videoPath);
                attachedVideo(videoName);
            }

            context.close();
        }
    }

    @AfterAll
    static void closeBrowser() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @RegisterExtension
    TestWatcher watcher = new TestWatcher() {
        @Override
        public void testFailed(ExtensionContext extensionContext, Throwable cause) {
            try {
                if (page != null && !page.isClosed()) {
                    // make file
                    String testName = extensionContext.getDisplayName();
                    Path screenshotPath = screenshotDir.resolve(testName + ".png");

                    //Make screenshot
                    byte[] screenshot = page.screenshot(new Page.ScreenshotOptions()
                            .setPath(screenshotPath)
                            .setFullPage(true)
                    );

                    //attach to allure
                    saveScreenshotToAllure(screenshot, testName);
                    System.out.println("Screenshot saved to: " + screenshotPath);

                }

            } catch (Exception e) {
                System.out.println("error with saving screenshot");
                System.err.println("Error with saving screenshot" + e.getMessage());
            }
        }
    };

    @Attachment(value = "Скриншот при падении: {name}", type = "image/png")
    private byte[] saveScreenshotToAllure(byte[] screenshot, String name) {
        return screenshot;
    }

    @Attachment(value = "Видео теста {name}", type = "video/webm")
    private byte[] attachedVideo(String name) throws IOException {
        return Files.readAllBytes(Paths.get("videos/" + name + ".webm"));
    }


}
