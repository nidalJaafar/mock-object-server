package platform.media.mockobjectserver.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class MediaServiceException extends RuntimeException {
    private final HttpStatusCode httpStatusCode;

    public MediaServiceException(String message, Throwable throwable, HttpStatusCode httpStatusCode) {
        super(message, throwable);
        this.httpStatusCode = httpStatusCode;
    }
}
