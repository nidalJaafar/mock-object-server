package platform.media.mockobjectserver.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MediaServiceExceptionHandlerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldHandleMediaServiceException() {
        webTestClient.get()
                .uri("/api/v1/download/non-existent-file")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ApiError.class);
    }
}
