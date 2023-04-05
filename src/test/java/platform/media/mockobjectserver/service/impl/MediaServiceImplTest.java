package platform.media.mockobjectserver.service.impl;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class MediaServiceImplTest {

    @InjectMocks
    private MediaServiceImpl mediaService;

    private final Path fileStorageLocation = Paths.get("./uploads/files");


    @Test
    public void testUpload() {

        MockMultipartFile file = new MockMultipartFile("temp-file", "temp-file.txt", "text/plain", "Hello World!".getBytes());
        String result = mediaService.upload(file);
        boolean match = Arrays.stream(Objects.requireNonNull(new File(fileStorageLocation.toUri())
                        .listFiles()))
                .map(File::getName)
                .anyMatch(name -> name.equals(result));
        assertTrue(match);
    }
}
