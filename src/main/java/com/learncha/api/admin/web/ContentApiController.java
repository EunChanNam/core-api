package com.learncha.api.admin.web;

import com.learncha.api.admin.service.ContentService;
import com.learncha.api.admin.web.ConentDto.ContentUpsertRequest;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContentApiController {

    private final ContentService adminService;

    public ContentApiController(final ContentService adminService) {
        this.adminService = adminService;
    }

    @PutMapping("/api/v1/contents")
    public void upsert(@RequestBody ContentUpsertRequest dto) {
        adminService.upsert(dto);
    }
}
