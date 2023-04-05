package platform.media.mockobjectserver.service;

import org.springframework.web.multipart.MultipartFile;

public interface MediaService {

    String upload(MultipartFile request);
}
