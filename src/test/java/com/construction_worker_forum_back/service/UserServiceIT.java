package com.construction_worker_forum_back.service;

import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class UserServiceIT {

    @Container
    public static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:latest");

}