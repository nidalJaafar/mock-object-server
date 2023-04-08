package platform.media.mockobjectserver.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

@RequiredArgsConstructor
public class ApiError implements ErrorResponse {
    private final HttpStatusCode httpStatusCode;
    private final ProblemDetail problemDetail;

    @Override
    public HttpStatusCode getStatusCode() {
        return httpStatusCode;
    }

    @Override
    public ProblemDetail getBody() {
        return problemDetail;
    }
}
