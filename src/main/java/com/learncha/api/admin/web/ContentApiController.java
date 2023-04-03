package com.learncha.api.admin.web;

import com.learncha.api.admin.service.ContentService;
import com.learncha.api.admin.web.ContentDto.ContentResponse;
import com.learncha.api.admin.web.ContentDto.ContentUpsertRequest;
import com.learncha.api.common.security.jwt.model.UserDetailsImpl;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ContentApiController {

    private final ContentService adminService;

    public ContentApiController(final ContentService adminService) {
        this.adminService = adminService;
    }

    @PutMapping("/api/v1/contents")
    public void upsert(
            @RequestBody @Valid ContentUpsertRequest dto,
            @AuthenticationPrincipal UserDetailsImpl user) {
        log.info("user: {}", user);
        adminService.upsert(dto, user.getMember().getMemberToken());
    }

    @GetMapping("/api/v1/contents")
    public ContentResponse getTemporaryContent(@AuthenticationPrincipal UserDetailsImpl user) {
        return adminService.getTemporaryContent(user.getMember().getId());
    }
}
