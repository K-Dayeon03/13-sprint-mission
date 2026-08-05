package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BinaryContentMapper {

    @Mapping(
            target = "downloadUrl",
            expression = "java(\"/api/binaryContents/\" + binaryContent.getId() + \"/download\")"
    )
    BinaryContentDto toDto(BinaryContent binaryContent);
}