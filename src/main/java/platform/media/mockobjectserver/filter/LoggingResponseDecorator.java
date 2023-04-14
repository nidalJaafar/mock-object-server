package platform.media.mockobjectserver.filter;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;

@Slf4j
public class LoggingResponseDecorator extends ServerHttpResponseDecorator {
    private ServerHttpResponse delegate;

    public LoggingResponseDecorator(ServerHttpResponse delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        return super.writeWith(Flux.from(body).doOnNext(buffer -> {
            if (log.isDebugEnabled()) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ByteBuffer byteBuffer = ByteBuffer.allocate(buffer.capacity());
                buffer.toByteBuffer(byteBuffer);
                try {
                    Channels.newChannel(byteArrayOutputStream).write(byteBuffer.asReadOnlyBuffer());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                log.debug("{}: {} - {} : {}", "response", byteArrayOutputStream.toString(), "header", delegate.getHeaders().toString());
            }
        }));
    }
}
