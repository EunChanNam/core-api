package com.learncha.api.admin.repository;

import com.learncha.api.admin.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, Long> { }
