package io.twogether.nbe_5_7_2_02team.member.util.Uploader;

import io.twogether.nbe_5_7_2_02team.global.exception.ErrorException;
import io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ImageUpload {

    @Value("${file.upload-dir}")
    private String baseUploadDir;

    public String saveProfileImage(MultipartFile file, Long memberId) {
        if (file == null || file.isEmpty()) return null;
        try {
            Path uploadRootPath = Paths.get(baseUploadDir).toAbsolutePath().normalize();
            Path memberDir = uploadRootPath.resolve("member").resolve(String.valueOf(memberId));

            Files.createDirectories(memberDir);

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = memberDir.resolve(fileName);
            file.transferTo(filePath.toFile());

            return "/uploads/member/" + memberId + "/" + fileName;

        } catch (IOException e) {
            throw new ErrorException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }
}
