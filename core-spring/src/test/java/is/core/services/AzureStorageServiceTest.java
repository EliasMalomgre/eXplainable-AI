package is.core.services;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AzureStorageServiceTest {

    @Autowired
    AzureStorageService storageService;

    @ParameterizedTest
    @ValueSource(strings = {"test.png","Test.png","Test123.png","Test123!.png",
            "&é@'(§^è!ç{à})°-_€¨[$*]ù´%£µ`,;.:=+~0123456789 .png", "笑傳陸類生如野收首死處因、" +
            "爾是認們出學蘭。情家他望消有前同、為現保此位變登離狀下回給.png",
            "川なへ項応ヱアカマ癒障リ半編フラミ強反けへき術更らだ鏑刊検ナサシウ攻呪ヨフエ.png",
            "hello.world.png"})
    public void getCorrectFileExtension(String fileName) {
        assertEquals(".png", storageService.getFileExtension(fileName));
    }

    @ParameterizedTest
    @ValueSource(strings = {"test"})
    public void getEmptyFileExtension(String fileName) {
        assertEquals("", storageService.getFileExtension(fileName));
    }

}