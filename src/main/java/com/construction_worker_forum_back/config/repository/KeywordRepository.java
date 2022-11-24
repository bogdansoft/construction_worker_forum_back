package com.construction_worker_forum_back.config.repository;

import com.construction_worker_forum_back.model.entity.Keyword;
import org.springframework.data.repository.CrudRepository;

public interface KeywordRepository extends CrudRepository<Keyword, String> {
}
