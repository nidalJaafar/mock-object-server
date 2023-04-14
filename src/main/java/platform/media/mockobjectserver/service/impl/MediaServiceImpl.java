package platform.media.mockobjectserver.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import platform.media.mockobjectserver.exception.MediaServiceException;
import platform.media.mockobjectserver.response.UploadResponse;
import platform.media.mockobjectserver.service.MediaService;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    @Value("${base.folder}")
    private String baseFolder = "uploads/files/";
    private final Path fileStorageLocation = Paths.get(baseFolder).toAbsolutePath().normalize();


    @PostConstruct
    public void postConstruct() {
        try {
            Files.createDirectories(fileStorageLocation);
        } catch (Exception ex) {
            throw new MediaServiceException("Exception.could-not-create-file", ex, 500);
        }
    }

    @Override
    public Mono<UploadResponse> upload(FilePart request) {
        String fileName = getFileName(request);
        Path targetLocation = fileStorageLocation.resolve(fileName);
        return request
                .transferTo(targetLocation)
                .doOnError(ex -> {
                    throw new MediaServiceException("Exception.could-not-store-file", ex, 500);
                })
                .doOnSuccess(v -> {
                    log.info("uploaded file {}", targetLocation.toString());
                })
                .then(Mono.just(new UploadResponse().setFileName(fileName)));
    }

    protected String getFileName(FilePart request) {
        return UUID.randomUUID() + "-" + request.filename();
    }

    @Override
    public Mono<FileSystemResource> download(String name) {
        return Mono.just(new File(baseFolder + name))
                .filter(File::exists)
                .switchIfEmpty(Mono.error(new MediaServiceException("Exception.file-not-found", new FileNotFoundException(), 404)))
                .map(FileSystemResource::new)
                .doOnSuccess(resource -> {
                    log.info("downloaded file {}", fileStorageLocation.resolve(name).toString());
                });
    }

}
