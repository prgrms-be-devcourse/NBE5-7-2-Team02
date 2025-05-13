package io.twogether.nbe_5_7_2_02team.post.util;

import io.twogether.nbe_5_7_2_02team.global.exception.ErrorException;
import io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageUploader {

    @Value("${file.upload-dir}")
    private String baseUploadDir;

    public List<String> saveImages(List<MultipartFile> images, Long postId) {

        if (images.size() > 10) {
            throw new ErrorException(ErrorCode.IMAGE_UPLOAD_LIMIT_EXCEEDED);
        }

        return images.stream()
            .map(image -> saveImage(image, postId))
            .toList();
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
}
