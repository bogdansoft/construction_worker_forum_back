package com.construction_worker_forum_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class ConstructionWorkerForumBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConstructionWorkerForumBackApplication.class, args);
    }
}
