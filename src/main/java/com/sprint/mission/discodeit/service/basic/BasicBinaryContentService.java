package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.command.BinaryContentCommand;
import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Override
    @Transactional
    public BinaryContentDto create(BinaryContentCommand command) {
        BinaryContent binaryContent = new BinaryContent(
                null,
                null,
                command.fileName(),
                command.contentType(),
                (long) command.bytes().length
        );

        BinaryContent saved = binaryContentRepository.save(binaryContent);
        binaryContentStorage.put(saved.getId(), command.bytes());
        return binaryContentMapper.toDto(saved);
    }

    @Override
    public BinaryContentDto findById(UUID id) {
        return binaryContentMapper.toDto(findEntityOrThrow(id));
    }

    @Override
    public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        // 리포지토리에 대량 조회를 위임합니다.
        return binaryContentRepository.findAllById(ids).stream()
                .map(binaryContentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        findEntityOrThrow(id);
        binaryContentRepository.deleteById(id);
    }

    private BinaryContent findEntityOrThrow(UUID id) {
        return binaryContentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 파일입니다."));
    }
}
