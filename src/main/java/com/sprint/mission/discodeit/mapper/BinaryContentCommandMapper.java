package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.command.BinaryContentCommand;
import com.sprint.mission.discodeit.dto.request.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.exception.BadRequestException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class BinaryContentCommandMapper {

    public BinaryContentCommand toCommand(CreateBinaryContentRequest request) {
        if (request == null) {
            return null;
        }
        return new BinaryContentCommand(request.fileName(), request.contentType(), request.bytes());
    }

    public BinaryContentCommand toCommand(MultipartFile file, String defaultFileName) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            String fileName = resolveFileName(file, defaultFileName);
            String contentType = resolveContentType(file);
            return new BinaryContentCommand(fileName, contentType, file.getBytes());
        } catch (IOException e) {
            throw new BadRequestException("파일을 읽을 수 없습니다.", e);
        }
    }

    private String resolveFileName(MultipartFile file, String defaultFileName) {
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.hasText(originalFilename)) {
            return originalFilename;
        }
        return defaultFileName;
    }

    private String resolveContentType(MultipartFile file) {
        String contentType = file.getContentType();
        if (StringUtils.hasText(contentType)) {
            return contentType;
        }
        return MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }
}
