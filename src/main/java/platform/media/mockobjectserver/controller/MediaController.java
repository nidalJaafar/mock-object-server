package platform.media.mockobjectserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import platform.media.mockobjectserver.service.MediaService;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MediaController {

    private final MediaService service;

    @PostMapping("/upload")
    public Mono<String> upload(@RequestPart("file") FilePart request) {
        return service.upload(request);
    }

    @GetMapping("/download/{name}")
    public Mono<FileSystemResource> download(@PathVariable String name) {
        return service.download(name);
    }
}
