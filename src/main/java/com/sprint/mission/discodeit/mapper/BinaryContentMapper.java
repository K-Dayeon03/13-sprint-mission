package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Component;

@Component
public class BinaryContentMapper {

    public BinaryContentDto toDto(BinaryContent binaryContent) {
        if (binaryContent == null) {
            return null;
        }
        return new BinaryContentDto(
                binaryContent.getId(),
                binaryContent.getCreatedAt(),
                binaryContent.getFileName(),
                binaryContent.getContentType(),
                binaryContent.getSize(),
                "/api/binary-contents/" + binaryContent.getId()
        );
    }
}
