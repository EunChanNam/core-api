package com.learncha.api.healthcheck.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api/health-check")
@RestController
public class HealthCheckApiController {

    @GetMapping
    public ResponseEntity<?> healthCheck() {
        log.info("health check start");
        return ResponseEntity.ok().build();
    }

}
