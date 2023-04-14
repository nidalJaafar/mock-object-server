package platform.media.mockobjectserver.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import platform.media.mockobjectserver.response.UploadResponse;
import platform.media.mockobjectserver.service.MediaService;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class MediaControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private MediaService mediaService;

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
    void testUploadFile() throws IOException {
        File file = new File("uploads/files/sample-file.txt");
        Files.createFile(file.toPath());

        when(mediaService.upload(any())).thenReturn(Mono.just(new UploadResponse().setFileName(file.getName())));

        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("file", new FileSystemResource(file));

        String fileName = webTestClient.post()
                .uri("/api/v1/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UploadResponse.class)
                .returnResult()
                .getResponseBody()
                .getFileName();
        assertNotNull(fileName);
        assertEquals("sample-file.txt", fileName);
        Files.delete(file.toPath());
    }

    @Test
    void testDownloadFile() throws IOException {
        File file = new File("uploads/files/sample-file.txt");
        Files.createFile(file.toPath());
        Files.write(file.toPath(), "test".getBytes());

        when(mediaService.download(anyString())).thenReturn(Mono.just(new FileSystemResource(file)));

        webTestClient.get()
                .uri("/api/v1/download/{name}", file.getName())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo(Files.readString(file.toPath(), StandardCharsets.UTF_8));
        Files.delete(file.toPath());
    }
}
