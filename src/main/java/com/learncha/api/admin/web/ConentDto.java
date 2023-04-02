package com.learncha.api.admin.web;

import com.learncha.api.admin.entity.Content;
import com.learncha.api.admin.entity.basicinfo.ContentBasicInfo;
import com.learncha.api.admin.entity.chapter.Chapter;
import com.learncha.api.admin.entity.chapter.cover.ChapterCover;
import com.learncha.api.admin.entity.mediainfo.Book;
import com.learncha.api.admin.entity.mediainfo.ContentMediaInfo;
import com.learncha.api.admin.entity.mediainfo.ContentMediaInfo.ContentMediaType;
import com.learncha.api.admin.entity.mediainfo.Youtube;
import java.util.List;
import lombok.Getter;
import lombok.ToString;

@Getter
public class ConentDto {

    @ToString
    @Getter
    public static class ContentUpsertRequest {
        private BasicInfo basicInfo;
        private MediaInfo mediaInfo;
        private List<ChapterInfo> chapterInfo;

        public Content toEntity() {
            ContentBasicInfo basicInfoValue = basicInfo.toBasicInfo();
            ContentMediaInfo mediaInfoValue = mediaInfo.toMediaInfo();

            return new Content(basicInfoValue, mediaInfoValue);
        }
    }

    @ToString
    @Getter
    public static class BasicInfo {
        private String pricePolicy;
        private Long price;
        private String category;
        private String readingTime;

        public ContentBasicInfo toBasicInfo() {
            return ContentBasicInfo.createFreeBasicInfo(
                price,
                category,
                readingTime
            );
        }
    }

    @ToString
    @Getter
    public static class MediaInfo {
        private String mediaType;
        private String videoUrl;
        private String channelName;
        private String videoTitle;
        private String bookTitle;
        private String bookImageUrl;
        private String author;
        private String secondTitle;
        private String thumbnailImageUrl;

        public ContentMediaInfo toMediaInfo() {
            Youtube youtube = Youtube.of(videoUrl, channelName, videoTitle, thumbnailImageUrl);
            Book book = Book.of(author, secondTitle, bookImageUrl);

            ContentMediaType contentMediaType = ContentMediaType.YOUTUBE;

            if(this.mediaType.equals(ContentMediaType.BOOK.name()))
                contentMediaType = ContentMediaType.BOOK;

            return new ContentMediaInfo(contentMediaType, youtube,book);
        }
    }

    @ToString
    @Getter
    public static class ChapterInfo {
        private int ordering;
        private String timeFlag;
        private String coverImageUrl;
        private String title;
        private String overview;
        private String detail;

        public Chapter toChapterEntity() {
            ChapterCover chapterCover = new ChapterCover(title, coverImageUrl, overview);
            return new Chapter(ordering, timeFlag, chapterCover, detail);
        }
    }

}
