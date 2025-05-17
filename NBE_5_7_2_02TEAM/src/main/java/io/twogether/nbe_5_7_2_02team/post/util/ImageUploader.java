package io.twogether.nbe_5_7_2_02team.post.util;

import io.twogether.nbe_5_7_2_02team.global.exception.ErrorException;
import io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ImageUploader {

    @Value("${file.upload-dir}")
    private String baseUploadDir;

    private static final int MAX_IMAGES_COUNT = 10;

    public List<String> saveImages(List<MultipartFile> images, Long postId) {

        if (images.size() > MAX_IMAGES_COUNT) {
            throw new ErrorException(ErrorCode.IMAGE_UPLOAD_LIMIT_EXCEEDED);
        }

        return images.stream().map(image -> saveImage(image, postId)).toList();
    }

    private String saveImage(MultipartFile file, Long postId) {
        try {
            Path uploadRootPath = Paths.get(baseUploadDir).toAbsolutePath().normalize();
            Path postDir = uploadRootPath.resolve("post").resolve(String.valueOf(postId));

            Files.createDirectories(postDir);

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = postDir.resolve(fileName);
            file.transferTo(filePath.toFile());

            return "/uploads/post/" + postId + "/" + fileName;

        } catch (IOException e) {
            throw new ErrorException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }

    public void deletePostImageByFolder(Long postId) {

        Path folderPath =
                Paths.get(baseUploadDir)
                        .toAbsolutePath()
                        .normalize()
                        .resolve("post")
                        .resolve(postId.toString());

        try {
            if (Files.exists(folderPath)) {
                Files.walk(folderPath)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        } catch (IOException e) {
            throw new ErrorException(ErrorCode.IMAGE_DELETE_FAILED);
        }
    }
}
