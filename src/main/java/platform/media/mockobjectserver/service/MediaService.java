package platform.media.mockobjectserver.service;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.codec.multipart.FilePart;
import platform.media.mockobjectserver.response.UploadResponse;
import reactor.core.publisher.Mono;

public interface MediaService {

    Mono<UploadResponse> upload(FilePart request);

    Mono<FileSystemResource> download(String name);
}