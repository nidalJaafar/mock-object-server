package platform.media.mockobjectserver.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.codec.multipart.FilePart;
import platform.media.mockobjectserver.exception.MediaServiceException;
import platform.media.mockobjectserver.response.UploadResponse;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class MediaServiceImplTest {

    @Mock
    private FilePart filePart;
    @Spy
    private final MediaServiceImpl mediaService = new MediaServiceImpl();

    @BeforeAll
    public static void createDirectory() {
        File file = new File("uploads/files");
        if (file.mkdirs()) {
            log.info("new folders created");
        }
    }

    @AfterAll
    public static void destroyDirectory() throws IOException {
        try (Stream<Path> pathStream = Files.walk(Path.of("uploads"))) {
            pathStream.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(file -> {
                        if (file.delete()) {
                            log.info("test file: {} deleted", file.getName());
                        }
                    });
        }
    }

    @Test
    public void testPostConstructCreatesFileStorageDirectory() {
        Path fileStorageLocation = Paths.get("./uploads/files");
        mediaService.postConstruct();
        assertTrue(Files.isDirectory(fileStorageLocation));
    }

    @Test
    public void testUploadSuccess() throws IOException {
        String content = "Hello World!";
        Path tempFile = Path.of("uploads/files/123-test.txt");
        Files.createFile(tempFile);
        Files.write(tempFile, content.getBytes());

        when(mediaService.getFileName(filePart)).thenReturn("123-test.txt");
        when(filePart.transferTo(any(Path.class))).thenReturn(Mono.empty());
        UploadResponse uploadedFileName = mediaService.upload(filePart).block();

        assertNotNull(uploadedFileName);
        Path uploadedFilePath = new File("uploads/files/" + uploadedFileName.getFileName()).toPath();
        assertTrue(uploadedFilePath.toFile().exists());
        assertEquals(content, new String(Files.readAllBytes(uploadedFilePath)));

        Files.delete(tempFile);
    }

    @Test
    public void testDownloadFileNotFound() {
        assertThrows(MediaServiceException.class, () -> mediaService.download("nonexistent-file.txt").block());
    }

    @Test
    public void testDownloadSuccess() throws IOException {
        String content = "Hello World!";
        Path tempFile = Files.createFile(Path.of("uploads/files/test-file.txt"));
        Files.write(tempFile, content.getBytes());

        FileSystemResource downloadedFile = mediaService.download("test-file.txt").block();

        assertNotNull(downloadedFile);
        assertEquals(content, new String(Files.readAllBytes(Path.of(downloadedFile.getPath()))));

        Files.delete(tempFile);
    }
}
