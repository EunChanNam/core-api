package com.learncha.api.admin.entity.chapter.cover;

import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class ChapterCover {
    private String title;
    private String coverImageUrl;
    private String overview;

    @Builder
    public ChapterCover(String title, String coverImageUrl, String overview) {
        this.title = title;
        this.coverImageUrl = coverImageUrl;
        this.overview = overview;
    }
}
