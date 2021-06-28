package is.core;

import is.core.services.AzureStorageService;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * These tests should be included in the pipeline build due to usage of Azure File Storage!
 * They can be used to quickly test if uploading/pulling/deleting is possible locally
 */
@SpringBootTest
public class AzureFileStorageTest {

    @Autowired
    AzureStorageService azureBlobAdapter;

    @Disabled
    @Test
    public void getFile() {
        byte[] content = azureBlobAdapter.getFile("filetje.txt");
        assertNotNull(content);
    }

    @Disabled
    @Test
    public void upLoadFile() {
        String fileName = "test_model.h5";
        File file = new File(fileName);
        FileInputStream input = null;
        try {
            input = new FileInputStream(file);
            byte[] bytes = IOUtils.toByteArray(input);
            MultipartFile multipartFile = new MockMultipartFile("file",
                    file.getName(), "text/plain", bytes);
            azureBlobAdapter.upload(multipartFile, "test_data/models/");
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Disabled
    @Test
    public void deleteFile() {
        assertTrue(azureBlobAdapter.deleteFile("test.txt"));
    }

    @Disabled
    @Test
    public void uploadContentsOfFolder() {
        String dir = "./images/";
        try (Stream<Path> stream = Files.list(Paths.get(dir))) {
            stream.filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .forEach(path -> {
                        File file = new File(dir + path);
                        FileInputStream input = null;
                        try {
                            input = new FileInputStream(file);
                            byte[] bytes = IOUtils.toByteArray(input);
                            MultipartFile multipartFile = new MockMultipartFile("file",
                                    file.getName(), "text/plain", bytes);
                            azureBlobAdapter.upload(multipartFile, "test_data/images/");
                        } catch (IOException e) {
                            e.printStackTrace();
                            fail();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
