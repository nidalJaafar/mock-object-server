package platform.media.mockobjectserver.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import platform.media.mockobjectserver.exception.MediaServiceException;
import platform.media.mockobjectserver.service.MediaService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import java.util.UUID;

@Service
public class MediaServiceImpl implements MediaService {

    private final Path fileStorageLocation;
    private final Properties prop;

    public MediaServiceImpl() {
        prop = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("messages.properties")) {
            prop.load(inputStream);
        } catch (IOException e) {
            throw new MediaServiceException("Messages file not found", e);
        }

        fileStorageLocation = Paths.get("./uploads/files").toAbsolutePath().normalize();
        try {
            Files.createDirectories(fileStorageLocation);
        } catch (Exception ex) {
            throw new MediaServiceException(prop.getProperty("message.could-not-create-file"), ex);
        }
    }


    @Override
    public String upload(MultipartFile request) {
        String fileName = UUID.randomUUID() + "-" + request.getOriginalFilename();
        try {
            Path targetLocation = fileStorageLocation.resolve(fileName);
            Files.copy(request.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException ex) {
            throw new MediaServiceException(prop.getProperty("message.could-not-store-file"), ex);
        }
    }
}
