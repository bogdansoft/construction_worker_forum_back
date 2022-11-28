package com.construction_worker_forum_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableCaching
public class ConstructionWorkerForumBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConstructionWorkerForumBackApplication.class, args);
    }
}
