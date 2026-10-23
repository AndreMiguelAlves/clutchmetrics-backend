package pt.clutchmetrics.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class VideoProcessingService {

    private final VideoStorageService videoStorageService;

    public VideoProcessingService(
            VideoStorageService videoStorageService) {
        this.videoStorageService = videoStorageService;
    }


    public String processVerticalVideo(
            String originalFileName,
            int startSeconds,
            int endSeconds) {

        return processVerticalVideo(
                originalFileName,
                startSeconds,
                endSeconds,
                false
        );
    }

    public String processVerticalVideo(
            String originalFileName,
            int startSeconds,
            int endSeconds,
            boolean fullHd) {


        // Validar o intervalo solicitado
        if (startSeconds < 0 || endSeconds <= startSeconds) {
            throw new IllegalArgumentException(
                    "Invalid video interval."
            );
        }

        int duration = endSeconds - startSeconds;

        if (duration > 60) {
            throw new IllegalArgumentException(
                    "The processed clip cannot exceed 60 seconds."
            );
        }

        // Encontrar o original e preparar o destino
        Path input = videoStorageService
                .getOriginalVideoPath(originalFileName);

        double originalDuration = getVideoDuration(input);

        if (endSeconds > originalDuration) {
            throw new IllegalArgumentException(
                    "The selected interval exceeds the video duration."
            );
        }


        Path output = videoStorageService
                .createProcessedVideoPath();


        int targetWidth = fullHd ? 1080 : 720;
        int targetHeight = fullHd ? 1920 : 1280;

        String videoFilter =
                "[0:v]split=2[bg][fg];" +
                        "[bg]scale=" + targetWidth + ":" + targetHeight + ":" +
                        "force_original_aspect_ratio=increase," +
                        "crop=" + targetWidth + ":" + targetHeight +
                        ",boxblur=20:1[blur];" +
                        "[fg]scale=" + targetWidth + ":-2[front];" +
                        "[blur][front]overlay=(W-w)/2:(H-h)/2," +
                        "setsar=1[v]";


        // Cada elemento é um argumento separado.
        List<String> command = List.of(
                "ffmpeg",
                "-hide_banner",
                "-loglevel", "error",
                "-nostdin",
                "-y",
                "-ss", String.valueOf(startSeconds),
                "-i", input.toString(),
                "-t", String.valueOf(duration),
                "-filter_complex", videoFilter,
                "-map", "[v]",
                "-map", "0:a?",
                "-c:v", "libx264",
                "-preset", "veryfast",
                "-crf", "23",
                "-c:a", "aac",
                "-b:a", "128k",
                "-movflags", "+faststart",
                output.toString()
        );

        Path logFile;

        try {
            logFile = Files.createTempFile(
                    "clutchmetrics-ffmpeg-", ".log"
            );
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not prepare video processing.", e
            );
        }

        boolean success = false;

        try {
            ProcessBuilder builder = new ProcessBuilder(command);

            builder.redirectErrorStream(true);
            builder.redirectOutput(logFile.toFile());

            Process process = builder.start();

            // Impedir processos que ficam bloqueados
            boolean finished = process.waitFor(
                    180, TimeUnit.SECONDS
            );

            if (!finished) {
                process.destroyForcibly();
                process.waitFor();

                throw new IllegalStateException(
                        "Video processing timed out."
                );
            }

            if (process.exitValue() != 0) {
                throw new IllegalStateException(
                        "FFmpeg could not process this video."
                );
            }

            if (!Files.isRegularFile(output)
                    || Files.size(output) == 0) {
                throw new IllegalStateException(
                        "The processed video was not created."
                );
            }

            success = true;

            return output.getFileName().toString();

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Error while processing video.", e
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "Video processing was interrupted.", e
            );

        } finally {
            try {
                Files.deleteIfExists(logFile);

                if (!success) {
                    Files.deleteIfExists(output);
                }
            } catch (IOException ignored) {
                // Não impedir a limpeza ou propagação do erro principal.
            }
        }
    }


    public int getProcessedVideoDuration(String processedFileName) {

        // 1. Localizar o MP4 processado
        Path videoPath = videoStorageService
                .getProcessedVideoPath(processedFileName);

        // 2. Obter a duração real através do FFprobe
        double duration = getVideoDuration(videoPath);

        // 3. Converter para segundos inteiros
        return Math.max(1, (int) Math.round(duration));
    }


    private double getVideoDuration(Path videoPath) {

        Path logFile;

        try {
            logFile = Files.createTempFile(
                    "clutchmetrics-ffprobe-", ".log"
            );
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not prepare video validation.", e
            );
        }

        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "ffprobe",
                    "-v", "error",
                    "-show_entries", "format=duration",
                    "-of", "default=noprint_wrappers=1:nokey=1",
                    videoPath.toString()
            );

            builder.redirectErrorStream(true);
            builder.redirectOutput(logFile.toFile());

            Process process = builder.start();

            boolean finished = process.waitFor(
                    30, TimeUnit.SECONDS
            );

            if (!finished) {
                process.destroyForcibly();
                process.waitFor();

                throw new IllegalStateException(
                        "Video validation timed out."
                );
            }

            if (process.exitValue() != 0) {
                throw new IllegalArgumentException(
                        "Invalid or unreadable video file."
                );
            }

            String result = Files.readString(logFile).trim();

            double duration;

            try {
                duration = Double.parseDouble(result);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "Could not determine video duration.", e
                );
            }

            if (!Double.isFinite(duration) || duration <= 0) {
                throw new IllegalArgumentException(
                        "Invalid video duration."
                );
            }

            return duration;

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Error while checking video duration.", e
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "Video validation was interrupted.", e
            );

        } finally {
            try {
                Files.deleteIfExists(logFile);
            } catch (IOException ignored) {
                // Cleanup of the temporary validation log.
            }
        }
    }

}
