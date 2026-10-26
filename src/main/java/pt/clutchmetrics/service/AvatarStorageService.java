
package pt.clutchmetrics.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.UUID;

@Service
public class AvatarStorageService {

    private static final long MAX_FILE_SIZE = 3L * 1024 * 1024;

    private final Path avatarDirectory;

    public AvatarStorageService(
            @Value("${clutchmetrics.avatar.storage-path:storage/avatars}")
            String storagePath) {

        this.avatarDirectory = Path.of(storagePath)
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(avatarDirectory);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not initialize avatar storage.",
                    exception
            );
        }
    }

    public String saveAvatar(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Please select an image."
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Avatar must be smaller than 3 MB."
            );
        }

        String originalName = file.getOriginalFilename();
        int dotIndex = originalName == null
                ? -1
                : originalName.lastIndexOf('.');

        if (dotIndex < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid image format."
            );
        }

        String extension = originalName
                .substring(dotIndex + 1)
                .toLowerCase(Locale.ROOT);

        if (extension.equals("jpeg")) {
            extension = "jpg";
        }

        if (!extension.equals("jpg") &&
                !extension.equals("png") &&
                !extension.equals("webp")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only JPG, PNG and WebP images are allowed."
            );
        }

        try {
            byte[] header;

            try (InputStream input = file.getInputStream()) {
                header = input.readNBytes(12);
            }

            if (!hasValidSignature(header, extension)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "The file is not a valid image."
                );
            }

            String fileName = UUID.randomUUID() + "." + extension;

            Path destination = avatarDirectory.resolve(fileName);

            try (InputStream input = file.getInputStream()) {
                Files.copy(input, destination);
            }

            return fileName;

        } catch (IOException exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not save avatar.",
                    exception
            );
        }
    }

    public Path getAvatarPath(String fileName) {

        if (fileName == null ||
                !fileName.matches(
                        "[0-9a-f-]{36}\\.(jpg|png|webp)"
                )) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Avatar not found."
            );
        }

        Path path = avatarDirectory.resolve(fileName).normalize();

        if (!path.startsWith(avatarDirectory) ||
                !Files.isRegularFile(path)) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Avatar not found."
            );
        }

        return path;
    }

    private boolean hasValidSignature(
            byte[] header,
            String extension) {

        if (header.length < 12) {
            return false;
        }

        return switch (extension) {
            case "jpg" ->
                    (header[0] & 0xFF) == 0xFF &&
                            (header[1] & 0xFF) == 0xD8 &&
                            (header[2] & 0xFF) == 0xFF;

            case "png" ->
                    (header[0] & 0xFF) == 0x89 &&
                            header[1] == 0x50 &&
                            header[2] == 0x4E &&
                            header[3] == 0x47 &&
                            header[4] == 0x0D &&
                            header[5] == 0x0A &&
                            header[6] == 0x1A &&
                            header[7] == 0x0A;

            case "webp" ->
                    header[0] == 'R' &&
                            header[1] == 'I' &&
                            header[2] == 'F' &&
                            header[3] == 'F' &&
                            header[8] == 'W' &&
                            header[9] == 'E' &&
                            header[10] == 'B' &&
                            header[11] == 'P';

            default -> false;
        };
    }
}
