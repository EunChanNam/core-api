package com.learncha.api.admin.entity.chapter;

import com.learncha.api.admin.entity.Content;
import com.learncha.api.admin.entity.chapter.cover.ChapterCover;
import com.learncha.api.common.abstractentity.TimeStamp;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Chapter extends TimeStamp {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    @Column(name = "ordering")
    private Integer ordering;

    @Column(name = "time_flag")
    private String timeFlag;

    @Embedded
    private ChapterCover chapterCover;

    @Column(columnDefinition = "TEXT")
    private String detail;

    @ManyToOne
    @JoinColumn(name = "content_id")
    private Content content;

    public Chapter(Integer ordering, String timeFlag, ChapterCover chapterCover, String detail) {
        this.ordering = ordering;
        this.timeFlag = timeFlag;
        this.chapterCover = chapterCover;
        this.detail = detail;
    }

    public void setContent(Content content) {
        this.content = content;
    }
}
