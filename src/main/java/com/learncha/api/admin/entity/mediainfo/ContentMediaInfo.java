package com.learncha.api.admin.entity.mediainfo;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContentMediaInfo {

    @Enumerated(value = EnumType.STRING)
    private ContentMediaType contentMediaType;

    @Embedded
    private Youtube youtube;

    @Embedded
    private Book book;

    @RequiredArgsConstructor
    public enum ContentMediaType {
        YOUTUBE("YOUTUBE"),
        BOOK("BOOK")
        ;
        private final String description;
    }

    @Builder
    public ContentMediaInfo(ContentMediaType contentMediaType, Youtube youtube, Book book) {
        this.contentMediaType = contentMediaType;
        this.youtube = youtube;
        this.book = book;
    }

    // book 타입 메서드
    // youtube  타입 메서드
}
