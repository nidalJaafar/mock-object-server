package platform.media.mockobjectserver.service;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface MediaService {

    Mono<String> upload(FilePart request);

    Mono<FileSystemResource> download(String name);
}