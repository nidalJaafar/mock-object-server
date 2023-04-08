package platform.media.mockobjectserver.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import platform.media.mockobjectserver.service.MediaService;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class MediaControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private MediaService mediaService;

    @Test
    void testUploadFile() throws IOException {
        File file = new File("src/test/resources/sample-file.txt");
        Files.createFile(file.toPath());

        when(mediaService.upload(any())).thenReturn(Mono.just(file.getName()));

        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("file", new FileSystemResource(file));

        webTestClient.post()
                .uri("/api/v1/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo(file.getName());
        Files.delete(file.toPath());
    }

    @Test
    void testDownloadFile() throws IOException {
        File file = new File("src/test/resources/sample-file.txt");
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
