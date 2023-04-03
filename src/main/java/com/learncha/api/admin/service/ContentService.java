package com.learncha.api.admin.service;

import com.learncha.api.admin.entity.Content;
import com.learncha.api.admin.entity.chapter.Chapter;
import com.learncha.api.admin.repository.ContentRepository;
import com.learncha.api.admin.web.ContentDto.ChapterInfo;
import com.learncha.api.admin.web.ContentDto.ContentResponse;
import com.learncha.api.admin.web.ContentDto.ContentUpsertRequest;
import com.learncha.api.admin.web.ContentMapper;
import com.learncha.api.auth.domain.Member;
import com.learncha.api.auth.repository.MemberRepository;
import com.learncha.api.common.exception.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContentService {

    private final ContentRepository contentRepository;
    private final MemberRepository memberRepository;
    private final ContentMapper contentMapper;

    public ContentService(
            final ContentRepository contentRepository,
            final MemberRepository memberRepository,
            final ContentMapper contentMapper) {
        this.contentRepository = contentRepository;
        this.memberRepository = memberRepository;
        this.contentMapper = contentMapper;
    }

    @Transactional
    public void upsert(ContentUpsertRequest dto, String memberToken) {
        Member member = memberRepository.findByMemberToken(memberToken)
            .orElseThrow(EntityNotFoundException::new);

        Optional<Content> content = contentRepository.findByMemberId(member.getId());

        if(content.isEmpty()) {
            Content initContent = dto.toEntity();

            List<Chapter> chapters = dto.getChapterInfo().stream()
                .map(ChapterInfo::toChapterEntity)
                .collect(Collectors.toList());

            initContent.addChapters(chapters);
            initContent.setMemberId(member.getId());
            contentRepository.save(initContent);
        } else {
            contentRepository.delete(content.get());

            Content newContent = dto.toEntity();
            List<Chapter> newChapters = dto.getChapterInfo().stream()
                .map(ChapterInfo::toChapterEntity)
                .collect(Collectors.toList());

            newContent.addChapters(newChapters);
            newContent.setMemberId(member.getId());
            contentRepository.save(newContent);
        }
    }

    public ContentResponse getTemporaryContent(Long memberId) {
        return contentRepository.findByMemberId(memberId)
            .map(contentMapper::toResponse)
            .orElseGet(ContentResponse::none);
    }
}
