package platform.media.mockobjectserver.exception;

import lombok.Getter;

@Getter
public class MediaServiceException extends RuntimeException {
    private final Integer statusCode;

    public MediaServiceException(String message, Throwable cause, Integer statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }
}
