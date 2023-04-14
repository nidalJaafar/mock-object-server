package platform.media.mockobjectserver.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import reactor.core.publisher.Mono;

import java.util.Locale;

@ControllerAdvice
@RequiredArgsConstructor
public class MediaServiceExceptionHandler extends ResponseEntityExceptionHandler {
    private final MessageSource messageSource;

    @ExceptionHandler(MediaServiceException.class)
    public Mono<ApiError> mediaServiceExceptionHandler(MediaServiceException e, Locale locale) {
        String message = messageSource.getMessage(e.getMessage(), null, locale);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(e.getStatusCode()), message);
        ApiError apiError = new ApiError(HttpStatusCode.valueOf(e.getStatusCode()), problemDetail);
        return Mono.just(apiError);
    }
}
