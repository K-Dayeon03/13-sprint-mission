package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContent create(CreateBinaryContentRequest request) {
        BinaryContent binaryContent = new BinaryContent(
                null,                  // userId — 호출하는 쪽에서 맥락에 맞게 설정
                null,                  // messageId — 호출하는 쪽에서 맥락에 맞게 설정
                request.fileName(),
                request.contentType(),
                request.bytes()
        );
        return binaryContentRepository.save(binaryContent);
    }

    @Override
    public BinaryContent findById(UUID id) {
        BinaryContent binaryContent = binaryContentRepository.findById(id);
        if (binaryContent == null) {
            throw new NotFoundException("존재하지 않는 파일입니다.");
        }
        return binaryContent;
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        // 리포지토리에 대량 조회를 위임합니다.
        return binaryContentRepository.findAllByIdIn(ids);
    }

    @Override
    public void deleteById(UUID id) {
        BinaryContent binaryContent = binaryContentRepository.findById(id);
        if (binaryContent == null) {
            throw new NotFoundException("존재하지 않는 파일입니다.");
        }
        binaryContentRepository.deleteById(id);
    }
}
