package com.learncha.api.admin.entity.mediainfo;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Youtube {
    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "channel_name")
    private String channelName;

    @Column(name = "video_title")
    private String videoTitle;

    @Column(name = "thumbnail_image_url")
    private String thumbnailImageUrl;

    public Youtube(
            final String videoUrl,
            final String channelName,
            final String videoTitle,
            final String thumbnailImageUrl) {
        this.videoUrl = videoUrl;
        this.channelName = channelName;
        this.videoTitle = videoTitle;
        this.thumbnailImageUrl = thumbnailImageUrl;
    }

    public static Youtube of(String videoUrl, String channelName, String videoTitle, String thumbnailImageUrl) {
        return new Youtube(videoUrl, channelName, videoTitle, thumbnailImageUrl);
    }
}
