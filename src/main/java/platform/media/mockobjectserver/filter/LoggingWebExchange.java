package platform.media.mockobjectserver.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;

public class LoggingWebExchange extends ServerWebExchangeDecorator {
    private final LoggingRequestDecorator loggingRequestDecorator;
    private final LoggingResponseDecorator loggingResponseDecorator;

    protected LoggingWebExchange(ServerWebExchange delegate) {
        super(delegate);
        if (delegate.getRequest().getURI().getPath().equals("/api/v1/upload"))
            loggingResponseDecorator = new LoggingResponseDecorator(delegate.getResponse());
        else loggingResponseDecorator = null;
        loggingRequestDecorator = new LoggingRequestDecorator(delegate.getRequest());
    }

    @Override
    public ServerHttpRequest getRequest() {
        return loggingRequestDecorator;
    }

    @Override
    public ServerHttpResponse getResponse() {
        return loggingResponseDecorator == null ? getDelegate().getResponse() : loggingResponseDecorator;
    }
}
