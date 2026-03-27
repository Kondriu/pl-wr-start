package org.example;

import com.microsoft.playwright.Download;
import org.example.base.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileDownloadingTest extends BaseTest {

    @Test
    @DisplayName("downloading files")
    public void downloadFilesTest() {
        page.navigate("https://demoqa.com/upload-download");
        ///  event of downloading handling
        // waiting for downloading
        //обработчик будущих загрузок, без него тест не узнает что нужно будет принять файл
        Download download = page.waitForDownload(() -> {
            page.click("#downloadButton");
        });

        // sawing to file

        try {
            // temp path
            Path tempDir = Files.createTempDirectory("playwright-downloads");
            Path filePath = tempDir.resolve(download.suggestedFilename());

            // saving file
            download.saveAs(filePath);
            System.out.println("File saved: " + filePath);

            //assert file
            assertTrue(Files.exists(filePath));

            long fileSize = Files.size(filePath);
            System.out.println("File size: " + fileSize);
            assertTrue(fileSize > 0);

            assertTrue(filePath.toString().endsWith(".jpeg"), "file should have 'jpeg' extension");

            String mimeType = Files.probeContentType(filePath);
            System.out.println("MIME type: " + mimeType);
            assertEquals("image/jpeg", mimeType, "Mime type should be image/jpeg");

            byte[] bytes = Files.readAllBytes(filePath);
            assertTrue(bytes.length > 1000);

            //delete files
            Files.delete(filePath);
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }
}
