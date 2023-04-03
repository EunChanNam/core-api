package com.learncha.api.admin.web;

import com.learncha.api.admin.entity.Content;
import com.learncha.api.admin.web.ContentDto.BasicInfo;
import com.learncha.api.admin.web.ContentDto.ChapterInfo;
import com.learncha.api.admin.web.ContentDto.ContentResponse;
import com.learncha.api.admin.web.ContentDto.MediaInfo;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ContentMapper {

    public ContentResponse toResponse(Content content) {
        BasicInfo basicInfo = new BasicInfo(content.getBasicInfo());
        List<ChapterInfo> chapterInfos = content.getChapters().stream().map(ChapterInfo::new)
            .collect(Collectors.toList());

        if(content.getMediaInfo().getYoutube() != null)
            return new ContentResponse("TRUE", basicInfo, MediaInfo.youtube(content.getMediaInfo().getYoutube()), chapterInfos);

        return new ContentResponse("TRUE", basicInfo, MediaInfo.book(content.getMediaInfo().getBook()), chapterInfos);
    }
}
