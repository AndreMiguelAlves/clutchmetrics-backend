
package pt.clutchmetrics.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class VideoStorageService {

    private static final long MAX_FILE_SIZE = 100L * 1024 * 1024;

    private final Path originalDirectory;
    private final Path processedDirectory;

    public VideoStorageService(
            @Value("${clutchmetrics.video.storage-path:storage/videos}")
            String storagePath) {

        Path rootDirectory = Path.of(storagePath)
                .toAbsolutePath()
                .normalize();

        this.originalDirectory = rootDirectory.resolve("original");
        this.processedDirectory = rootDirectory.resolve("processed");

        try {
            Files.createDirectories(originalDirectory);
            Files.createDirectories(processedDirectory);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not initialize video storage.", e
            );
        }
    }

    public String saveOriginalVideo(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "The video file cannot be empty."
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    "The video must not exceed 100 MB."
            );
        }

        String originalName = file.getOriginalFilename();

        if (originalName == null ||
                !originalName.toLowerCase(java.util.Locale.ROOT)
                        .endsWith(".mp4")) {
            throw new IllegalArgumentException(
                    "Only MP4 files are supported."
            );
        }

        try (InputStream input = file.getInputStream()) {
            byte[] header = input.readNBytes(8);

            if (header.length < 8 ||
                    !new String(
                            header, 4, 4, StandardCharsets.US_ASCII
                    ).equals("ftyp")) {
                throw new IllegalArgumentException(
                        "The file is not a valid MP4 container."
                );
            }
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not validate the video.", e
            );
        }

        String fileName = UUID.randomUUID() + ".mp4";
        Path destination = originalDirectory.resolve(fileName);

        try (InputStream input = file.getInputStream()) {
            Files.copy(input, destination);
            return fileName;
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not save the video.", e
            );
        }
    }


    public Path getOriginalVideoPath(String fileName) {

        if (fileName == null ||
                !fileName.matches(
                        "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.mp4$"
                )) {

            throw new IllegalArgumentException(
                    "Invalid video file name."
            );
        }

        Path videoPath = originalDirectory
                .resolve(fileName)
                .normalize();

        if (!videoPath.startsWith(originalDirectory) ||
                !Files.isRegularFile(videoPath)) {

            throw new IllegalArgumentException(
                    "Original video not found."
            );
        }

        return videoPath;
    }

    public Path createProcessedVideoPath() {

        String fileName = UUID.randomUUID() + ".mp4";

        return processedDirectory.resolve(fileName);
    }


    public void deleteProcessedVideo(String fileName)
            throws IOException {

        // Validar o nome do ficheiro
        if (fileName == null ||
                !fileName.matches(
                        "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.mp4$"
                )) {

            throw new IllegalArgumentException(
                    "Invalid processed video file name."
            );
        }

        // Localizar o ficheiro na pasta processed
        Path videoPath = processedDirectory
                .resolve(fileName)
                .normalize();

        // Garantir que permanece dentro da pasta correta
        if (!videoPath.startsWith(processedDirectory)) {
            throw new IllegalArgumentException(
                    "Invalid processed video path."
            );
        }

        // Eliminar apenas se o ficheiro existir
        Files.deleteIfExists(videoPath);
    }

    public Path getProcessedVideoPath(String fileName) {

        if (fileName == null ||
                !fileName.matches(
                        "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.mp4$"
                )) {

            throw new IllegalArgumentException(
                    "Invalid processed video file name."
            );
        }

        Path videoPath = processedDirectory
                .resolve(fileName)
                .normalize();

        if (!videoPath.startsWith(processedDirectory) ||
                !Files.isRegularFile(videoPath)) {

            throw new IllegalArgumentException(
                    "Processed video not found."
            );
        }

        return videoPath;
    }

}
