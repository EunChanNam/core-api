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
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
public class ContentDto {

    @NoArgsConstructor
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

    @NoArgsConstructor
    @ToString
    @Getter
    public static class BasicInfo {
        private String pricePolicy;
        private Long price;
        private String category;
        private String readingTime;

        public BasicInfo(ContentBasicInfo basicInfo1) {
            this.pricePolicy = basicInfo1.getPricePolicy().name();
            this.price = basicInfo1.getPrice();
            this.category = basicInfo1.getCategory();
            this.readingTime = basicInfo1.getReadingTime();
        }

        public ContentBasicInfo toBasicInfo() {
            return ContentBasicInfo.createFreeBasicInfo(
                price,
                category,
                readingTime
            );
        }
    }

    @NoArgsConstructor
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

        // 제거
        public MediaInfo(ContentMediaInfo mediaInfo1) {
            this.mediaType = mediaInfo1.getContentMediaType().name();
            this.videoUrl = mediaInfo1.getYoutube().getVideoUrl();
            this.channelName = mediaInfo1.getYoutube().getChannelName();
            this.videoTitle = mediaInfo1.getYoutube().getVideoTitle();
            this.bookTitle = mediaInfo1.getBook().getTitle();
            this.bookImageUrl = mediaInfo1.getBook().getBookImageUrl();
            this.author = mediaInfo1.getBook().getAuthor();
            this.secondTitle = mediaInfo1.getBook().getSecondTitle();
            this.thumbnailImageUrl = mediaInfo1.getYoutube().getThumbnailImageUrl();
        }

        @Builder(builderMethodName = "youtubeTypeBuilder")
        private MediaInfo(String mediaType, String videoUrl, String channelName, String videoTitle, String thumbnailImageUrl) {
            this.mediaType = mediaType;
            this.videoUrl = videoUrl;
            this.videoTitle = videoTitle;
            this.channelName = channelName;
            this.thumbnailImageUrl = thumbnailImageUrl;
        }

        @Builder(builderMethodName = "bookTypeBuilder")
        private MediaInfo(String bookTitle, String bookImageUrl, String author, String secondTitle) {
            this.author = author;
            this.bookTitle = bookTitle;
            this.bookImageUrl = bookImageUrl;
            this.secondTitle = secondTitle;
        }

        public static MediaInfo book(Book book) {
            return MediaInfo.bookTypeBuilder()
                .author(book.getAuthor())
                .bookTitle(book.getTitle())
                .bookImageUrl(book.getBookImageUrl())
                .secondTitle(book.getSecondTitle())
                .build();
        }

        public ContentMediaInfo toMediaInfo() {
            Youtube youtube = Youtube.of(videoUrl, channelName, videoTitle, thumbnailImageUrl);
            Book book = Book.of(author, bookTitle, secondTitle, bookImageUrl);

            ContentMediaType contentMediaType = ContentMediaType.YOUTUBE;

            if(this.mediaType.equals(ContentMediaType.BOOK.name()))
                contentMediaType = ContentMediaType.BOOK;

            return new ContentMediaInfo(contentMediaType, youtube, book);
        }

        public static MediaInfo youtube(Youtube youtube) {
            return MediaInfo.youtubeTypeBuilder()
                .mediaType(ContentMediaType.YOUTUBE.name())
                .videoUrl(youtube.getVideoUrl())
                .channelName(youtube.getChannelName())
                .videoTitle(youtube.getVideoTitle())
                .channelName(youtube.getChannelName())
                .thumbnailImageUrl(youtube.getThumbnailImageUrl())
                .build();
        }
    }

    @NoArgsConstructor
    @ToString
    @Getter
    public static class ChapterInfo {
        private int ordering;
        private String timeFlag;
        private String coverImageUrl;
        private String title;
        private String overview;
        private String detail;

        public ChapterInfo(Chapter chapter) {
            this.ordering = chapter.getOrdering();
            this.timeFlag = chapter.getTimeFlag();
            this.coverImageUrl = chapter.getChapterCover().getCoverImageUrl();
            this.title = chapter.getChapterCover().getTitle();
            this.overview = chapter.getChapterCover().getOverview();
            this.detail = chapter.getDetail();
        }

        public Chapter toChapterEntity() {
            ChapterCover chapterCover = new ChapterCover(title, coverImageUrl, overview);
            return new Chapter(ordering, timeFlag, chapterCover, detail);
        }
    }

    @Getter
    public static class ContentResponse {
        private String result;
        private BasicInfo basicInfo;
        private MediaInfo mediaInfo;
        private List<ChapterInfo> chapterInfos;

        public ContentResponse(String result) {
            this.result = result;
        }

        public ContentResponse(
            String result,
            BasicInfo basicInfo,
            MediaInfo mediaInfo,
            List<ChapterInfo> chapterInfos) {
            this.basicInfo = basicInfo;
            this.mediaInfo = mediaInfo;
            this.chapterInfos = chapterInfos;
            this.result = result;
        }

        public static ContentResponse none() {
            return new ContentResponse("NONE");
        }
    }
}
