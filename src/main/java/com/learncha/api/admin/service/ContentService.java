package com.learncha.api.admin.service;

import com.learncha.api.admin.entity.Content;
import com.learncha.api.admin.entity.chapter.Chapter;
import com.learncha.api.admin.repository.ContentRepository;
import com.learncha.api.admin.web.ConentDto.ChapterInfo;
import com.learncha.api.admin.web.ConentDto.ContentUpsertRequest;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContentService {

    private final ContentRepository contentRepository;

    public ContentService(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    @Transactional
    public void upsert(ContentUpsertRequest dto) {
        Content content = dto.toEntity();

        List<Chapter> chapters = dto.getChapterInfo().stream().map(ChapterInfo::toChapterEntity)
            .collect(Collectors.toList());

        content.addChapters(chapters);
        contentRepository.save(content);
    }
}
