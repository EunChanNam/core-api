package com.learncha.api.admin.entity.basicinfo;

import javax.persistence.Column;
import javax.persistence.Embeddable;
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
public class ContentBasicInfo {
    @Column(name = "price_policy")
    @Enumerated(value = EnumType.STRING)
    private PricePolicy pricePolicy;

    @Column(name = "price")
    private Long price;

    @Column(name = "category")
    private String category;

    @Column(name = "reading_time")
    private String readingTime;

    @RequiredArgsConstructor
    public enum PricePolicy {
        FREE("FREE"),
        PAID("PAID")
        ;
        private final String description;
    }

    @Builder
    private ContentBasicInfo(PricePolicy pricePolicy, Long price, String category, String readingTime) {
        this.pricePolicy = pricePolicy;
        this.price = price;
        this.category = category;
        this.readingTime = readingTime;
    }

    public static ContentBasicInfo createFreeBasicInfo(Long price, String category, String readingTime) {
        return ContentBasicInfo.builder()
            .pricePolicy(PricePolicy.FREE)
            .price(price)
            .category(category)
            .readingTime(readingTime)
            .build();
    }
}
