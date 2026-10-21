
package pt.clutchmetrics.service;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class VideoProcessingServiceTest {

    @Test
    void shouldProcessVideoToVerticalFormat() throws IOException {

        // Pasta onde guardámos o vídeo no teste do Postman
        Path originalDirectory =
                Path.of("storage/videos/original");

        // Procurar o MP4 que enviámos anteriormente
        String originalFileName;

        try (var files = Files.list(originalDirectory)) {

            originalFileName = files
                    .filter(Files::isRegularFile)
                    .filter(path ->
                            path.toString().endsWith(".mp4"))
                    .findFirst()
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "No original MP4 found."
                            ))
                    .getFileName()
                    .toString();
        }

        // Criar os serviços necessários
        VideoStorageService storageService =
                new VideoStorageService("storage/videos");

        VideoProcessingService processingService =
                new VideoProcessingService(storageService);

        // Processar do segundo 2 até ao segundo 10
        String processedFileName =
                processingService.processVerticalVideo(
                        originalFileName,
                        2,
                        10
                );

        // Confirmar que foi criado um ficheiro real
        Path processedVideo = Path.of(
                "storage/videos/processed",
                processedFileName
        );

        assertTrue(Files.isRegularFile(processedVideo));

        assertTrue(Files.size(processedVideo) > 0);

        System.out.println(
                "Processed video created: " +
                        processedVideo.toAbsolutePath()
        );
    }
}
