package platform.media.mockobjectserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import platform.media.mockobjectserver.service.MediaService;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MediaController {

    private final MediaService service;

    @PostMapping("/upload")
    public Mono<String> upload(@RequestBody MultipartFile request) {
        return Mono.just(service.upload(request));
    }
}
