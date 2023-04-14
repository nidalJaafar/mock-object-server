package platform.media.mockobjectserver.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import reactor.core.publisher.Flux;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.Optional;

@Slf4j
public class LoggingRequestDecorator extends ServerHttpRequestDecorator {
    private Flux<DataBuffer> body;
    private final ServerHttpRequest delegate;

    public LoggingRequestDecorator(ServerHttpRequest delegate) {
        super(delegate);
        this.delegate = delegate;
        log();
    }

    @Override
    public Flux<DataBuffer> getBody() {
        return body;
    }

    private void log() {
        if (log.isDebugEnabled()) {
            String path = delegate.getURI().getPath();
            String method = Optional.of(delegate.getMethod()).orElse(HttpMethod.GET).name();
            String headers = delegate.getHeaders().toString();

            log.debug("{} {}", method, path);
            log.debug("{}", headers);
            body = super.getBody().doOnNext(buffer -> {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ByteBuffer byteBuffer = ByteBuffer.allocate(buffer.capacity());
                buffer.toByteBuffer(byteBuffer);
                try {
                    Channels.newChannel(byteArrayOutputStream).write(byteBuffer.asReadOnlyBuffer());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                log.debug("{}: {}", "request", byteArrayOutputStream);
            });
        } else body = super.getBody();
    }
}
