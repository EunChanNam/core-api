package com.learncha.api.healthcheck.web;

import com.learncha.api.common.security.jwt.model.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api/v1/hello")
@RestController
public class TestController {

    @GetMapping
    public void method_01(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        /**
         * principal은 Email인데 받고자 하는 값이 UserDetails라서 null
         */
        log.info("user details: ");
        System.out.println(userDetails);
        var data = SecurityContextHolder.getContext().getAuthentication();
    }

    @PostMapping
    public void method_o2(@AuthenticationPrincipal UserDetails user) {
        System.out.println(user);
    }
}
