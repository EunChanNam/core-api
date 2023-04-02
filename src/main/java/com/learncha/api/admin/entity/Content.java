package com.learncha.api.admin.entity;

import com.learncha.api.admin.entity.basicinfo.ContentBasicInfo;
import com.learncha.api.admin.entity.chapter.Chapter;
import com.learncha.api.admin.entity.mediainfo.ContentMediaInfo;
import com.learncha.api.common.abstractentity.TimeStamp;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
    name = "content",
    indexes = {
        @Index(name = "uq_article_token", columnList = "article_token", unique = true)
    }
)
public class Content extends TimeStamp {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(name = "article_token", nullable = true)
    private String articleToken;

    @Embedded
    private ContentBasicInfo basicInfo;

    @Embedded
    private ContentMediaInfo mediaInfo;

    @OneToMany(mappedBy = "content", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<Chapter> chapters = new LinkedList<>();

    @Builder
    public Content(ContentBasicInfo basicInfo, ContentMediaInfo mediaInfo) {
        this.basicInfo = basicInfo;
        this.mediaInfo = mediaInfo;
    }

    public void addChapters(List<Chapter> chapters) {
        for(Chapter chapter : chapters) {
            chapter.setContent(this);
        }
        this.chapters.addAll(chapters);
    }
}
