package is.core.services;

import is.core.domain.entities.files.Result;
import is.core.repositories.ResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResultService {

    private final AzureStorageService storageService;
    private final ResultRepository resultRepository;

    public Result postResult(MultipartFile image, String storagepath) {
        Result result = resultRepository.save(new Result(storageService.upload(image,
                storagepath + "/result/")));
        log.info("Result({}) successfully saved", result.getId());
        return result;
    }

    public byte[] getResult(String resultId) {
        Result result = resultRepository.findById(resultId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Result(%s) can not be found in the database", resultId)));
        return storageService.getFile(result.getStoragePath());
    }
}
