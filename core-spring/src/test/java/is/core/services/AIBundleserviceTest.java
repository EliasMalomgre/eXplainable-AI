package is.core.services;

import is.core.domain.entities.files.Data;
import is.core.domain.enums.DataType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class AIBundleserviceTest {

    @Autowired
    private AIBundleService bundleService;

    @MockBean
    private AzureStorageService storageService;

    @ParameterizedTest
    @CsvSource({
            "iVBORw0KGgoAAAANSUhEUgAAABwAAAAcCAAAAABXZoBIAAAAtElEQVR4nGNgGJKAMexANiu6GIwhd4eF4fkSBgYGhu43mFoX/YOCJL" +
                    "gQC5z1hYFh2uc4EVaGw1gs5T/1z4uB4fi/LwpwISY46+MEBgYGbk6GzQ+wuZc/XJLB9d+/STg9FPfvnzYuOb7zuCV1V/379y+eE7vk1n//" +
                    "/v37l4BdFiL5WAyrZILh0n//QuRxucgVxUFMuJRhl8zFKXn/GQM/TklmvNYc//dbGaedhxkL7+HTTCQAAApAQX+dVKUgAAAAAElFTkSuQm" +
                    "CC, 0, 28, 28, 1",
            "iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAIAAAAC64paAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAEnQAABJ0Ad" +
                    "5mH3gAAAAfSURBVDhPY7D3OEM2GtVMIhrVTCIa1UwiGnmaPc4AAJe3Ec+r5vUzAAAAAElFTkSuQmCC," +
                    "0, 20, 20, 3",
            "iVBORw0KGgoAAAANSUhEUgAAAA8AAAAUCAIAAADdpfBFAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAEnQAABJ0Ad" +
                    "5mH3gAAAAgSURBVDhPY7T3OMNANGCC0sSBUdWYYFQ1JhhVjQ4YGAAK2AF7zj9zsQAAAABJRU5ErkJggg==," +
                    "0, 20, 15, 3"
    })
    public void successImageValidationTest(String byteString, int dim_1, int dim_2, int dim_3, int dim_4) {
        byte[] imageBytes = Base64.getDecoder().decode(byteString.getBytes());
        assertTrue(bundleService.validateImage(imageBytes, Stream.of(dim_1, dim_2, dim_3, dim_4).collect(Collectors.toList())));
    }

    @ParameterizedTest
    @CsvSource({
            "iVBORw0KGgoAAAANSUhEUgAAABwAAAAcCAAAAABXZoBIAAAAtElEQVR4nGNgGJKAMexANiu6GIwhd4eF4fkSBgYGhu43mFoX/YOCJL" +
                    "gQC5z1hYFh2uc4EVaGw1gs5T/1z4uB4fi/LwpwISY46+MEBgYGbk6GzQ+wuZc/XJLB9d+/STg9FPfvnzYuOb7zuCV1V/379y+eE7vk1n//" +
                    "/v37l4BdFiL5WAyrZILh0n//QuRxucgVxUFMuJRhl8zFKXn/GQM/TklmvNYc//dbGaedhxkL7+HTTCQAAApAQX+dVKUgAAAAAElFTkSuQm" +
                    "CC, 0, 30, 30, 1",
            "iVBORw0KGgoAAAANSUhEUgAAABwAAAAcCAAAAABXZoBIAAAAtElEQVR4nGNgGJKAMexANiu6GIwhd4eF4fkSBgYGhu43mFoX/YOCJL" +
                    "gQC5z1hYFh2uc4EVaGw1gs5T/1z4uB4fi/LwpwISY46+MEBgYGbk6GzQ+wuZc/XJLB9d+/STg9FPfvnzYuOb7zuCV1V/379y+eE7vk1n//" +
                    "/v37l4BdFiL5WAyrZILh0n//QuRxucgVxUFMuJRhl8zFKXn/GQM/TklmvNYc//dbGaedhxkL7+HTTCQAAApAQX+dVKUgAAAAAElFTkSuQm" +
                    "CC, 0, 28, 28, 3",
            "iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAIAAAAC64paAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAEnQAABJ0Ad" +
                    "5mH3gAAAAfSURBVDhPY7D3OEM2GtVMIhrVTCIa1UwiGnmaPc4AAJe3Ec+r5vUzAAAAAElFTkSuQmCC," +
                    "0, 20, 20, 1",
            "iVBORw0KGgoAAAANSUhEUgAAAA8AAAAUCAIAAADdpfBFAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAEnQAABJ0Ad" +
                    "5mH3gAAAAgSURBVDhPY7T3OMNANGCC0sSBUdWYYFQ1JhhVjQ4YGAAK2AF7zj9zsQAAAABJRU5ErkJggg==," +
                    "0, 15, 20, 3"
    })
    public void failedImageValidationTest(String byteString, int dim_1, int dim_2, int dim_3, int dim_4) {
        byte[] imageBytes = Base64.getDecoder().decode(byteString.getBytes());
        assertFalse(bundleService.validateImage(imageBytes, Stream.of(dim_1, dim_2, dim_3, dim_4).collect(Collectors.toList())));
    }

    @Test
    public void ImagesValidateTest() {
        List<String> base64Strings = Stream.of(
                "iVBORw0KGgoAAAANSUhEUgAAABwAAAAcCAAAAABXZoBIAAAAtElEQVR4nGNgGJKAMexANiu6GIwhd4eF4fkSBgYGhu43mFoX/YOCJL" +
                        "gQC5z1hYFh2uc4EVaGw1gs5T/1z4uB4fi/LwpwISY46+MEBgYGbk6GzQ+wuZc/XJLB9d+/STg9FPfvnzYuOb7zuCV1V/379y+eE7vk1n//" +
                        "/v37l4BdFiL5WAyrZILh0n//QuRxucgVxUFMuJRhl8zFKXn/GQM/TklmvNYc//dbGaedhxkL7+HTTCQAAApAQX+dVKUgAAAAAElFTkSuQmCC",
                "iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAIAAAAC64paAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAEnQAABJ0Ad" +
                        "5mH3gAAAAfSURBVDhPY7D3OEM2GtVMIhrVTCIa1UwiGnmaPc4AAJe3Ec+r5vUzAAAAAElFTkSuQmCC",
                "iVBORw0KGgoAAAANSUhEUgAAAA8AAAAUCAIAAADdpfBFAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAEnQAABJ0Ad" +
                        "5mH3gAAAAgSURBVDhPY7T3OMNANGCC0sSBUdWYYFQ1JhhVjQ4YGAAK2AF7zj9zsQAAAABJRU5ErkJggg=="
        ).collect(Collectors.toList());

        when(storageService.upload(any(), anyString())).thenReturn("dummy");

        List<MultipartFile> dataList = new ArrayList<>();
        dataList.add(new MockMultipartFile("0", Base64.getDecoder().decode(base64Strings.get(0).getBytes())));
        dataList.add(new MockMultipartFile("1", Base64.getDecoder().decode(base64Strings.get(1).getBytes())));
        dataList.add(new MockMultipartFile("2", Base64.getDecoder().decode(base64Strings.get(2).getBytes())));

        List<MultipartFile> invalid = new ArrayList<>();
        List<Data> valid = bundleService.uploadImages(dataList, "/dummy/", DataType.IMAGE,
                Stream.of(0, 28, 28, 1).collect(Collectors.toList()), invalid);

        assertEquals(1, valid.size());
        assertEquals(2, invalid.size());
        assertTrue(invalid.stream().map(MultipartFile::getName).collect(Collectors.toList())
                .containsAll(Stream.of("1", "2").collect(Collectors.toList())));
    }
}