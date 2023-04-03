package com.learncha.api.admin.entity.mediainfo;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Book {
    @Column(name = "author")
    private String author;

    @Column(name = "title")
    private String title;

    @Column(name = "second_title")
    private String secondTitle;

    @Column(name = "book_image_url")
    private String bookImageUrl;

    @Builder
    public Book(String author, String title, String secondTitle, String bookImageUrl) {
        this.author = author;
        this.title = title;
        this.secondTitle = secondTitle;
        this.bookImageUrl = bookImageUrl;
    }

    public static Book of(String author, String title, String secondTitle, String bookImageUrl) {
        return new Book(author, title, secondTitle, bookImageUrl);
    }
}
