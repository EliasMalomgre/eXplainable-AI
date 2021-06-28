package is.core.services;

import com.azure.storage.blob.BlobClientBuilder;
import is.core.config.TempStorageConfig;
import is.core.config.persistence.FileStorageConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AzureStorageService {

    private final TempStorageConfig storageConfig;
    private final FileStorageConfig fileStorageConfig;

    private BlobClientBuilder getClient() {
        BlobClientBuilder client = new BlobClientBuilder();
        client.connectionString(fileStorageConfig.getConnectionString());
        client.containerName(fileStorageConfig.getContainername());
        return client;
    }

    /**
     * Uploads a file to Azure file storage
     *
     * @param file       The file that has to be uploaded
     * @param prefixName The prefix before the generated file name
     * @return the file name
     */
    public String upload(MultipartFile file, String prefixName) {
        if (file != null && file.getSize() > 0) {
            try {
                // Get the extension of the original file name
                String extension;
                if (file.getOriginalFilename() == null) {
                    extension = "";
                } else {
                    extension = getFileExtension(file.getOriginalFilename());
                }

                // Generate a file name
                String fileName = prefixName + UUID.randomUUID() + extension;

                // Upload the file
                log.debug("FileStorage: Uploading file[{}]", file.getOriginalFilename());
                getClient().blobName(fileName).buildClient().upload(file.getInputStream(), file.getSize());
                return fileName;
            } catch (Exception e) {
                log.error("FileStorage: Error occurred while uploading file[{}]", file.getOriginalFilename());
                return null;
            }
        }
        return null;
    }

    /**
     * Pull a file out of Azure file storage
     *
     * @param storagePath The file that has to be pulled
     * @return The bytes of the requested file
     */
    public byte[] getFile(String storagePath) {
        try {
            // Create a path the the standard temp path
            File appData = new File(System.getProperty("java.io.tmpdir"));

            // Create a folder for saving temp files
            String tempPath = appData.getAbsolutePath() + File.separator
                    + storageConfig.getAppDataFolderName() + File.separator
                    + storageConfig.getTempFolderName();
            Files.createDirectories(Paths.get(tempPath));

            // Create a temp file
            File tempFile = new File(tempPath + File.separator + UUID.randomUUID());

            // Download the file of Azure file storage
            getClient().blobName(storagePath).buildClient().downloadToFile(tempFile.getPath());

            // Convert the file in bytes
            byte[] content = Files.readAllBytes(Paths.get(tempFile.getPath()));

            // Remove the temp file
            boolean succeeded = tempFile.delete();
            if (!succeeded) {
                log.error("FileStorage: temporary file for path{} not deleted from temp directory", storagePath);
            }

            return content;
        } catch (Exception e) {
            log.error("FileStorage: Error occurred while retrieving file with path[{}]", storagePath);
            return null;
        }
    }

    /**
     * Deletes a file in Azure file storage
     *
     * @param filename The file that has to be deleted
     * @return Whether the deleting was successful
     */
    public boolean deleteFile(String filename) {
        try {
            getClient().blobName(filename).buildClient().delete();
            return true;
        } catch (Exception e) {
            log.warn("FileStorage: Failed to delete file with path[{}]", filename);
            return false;
        }
    }

    /**
     * Gets the file extension of an incoming filename
     *
     * @param fileName The incoming filename
     * @return The file extension
     */
    public String getFileExtension(String fileName) {
        int i = fileName.lastIndexOf('.');

        // Check if the file has an extension
        if (i > 0) {
            return fileName.substring(i);
        }
        return "";
    }

    /**
     * Converts a byte array into a Resource so it can be send as a file via an api request
     *
     * @param bytes    The bytes of the file
     * @param filename The name of the file
     * @return A Resource of the byte array
     */
    @Deprecated
    public ByteArrayResource getByteResource(byte[] bytes, String filename) {
        return new ByteArrayResource(bytes) {
            // Is necessary other wise I won't be received as a file
            @Override
            public String getFilename() {
                // Generate a random name with the file extension of the original file
                return UUID.randomUUID() + getFileExtension(filename);
            }
        };
    }
}