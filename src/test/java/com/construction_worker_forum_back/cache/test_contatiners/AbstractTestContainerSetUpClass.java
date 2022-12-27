package com.construction_worker_forum_back.cache.test_contatiners;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

@ExtendWith(SpringExtension.class)
@ImportAutoConfiguration(classes = CacheAutoConfiguration.class)
@EnableCaching
public abstract class AbstractTestContainerSetUpClass {

    @Container
    static final GenericContainer REDIS_CONTAINER;

    static {
        REDIS_CONTAINER = new GenericContainer(DockerImageName.parse("redis:5.0.3-alpine"))
                .withExposedPorts(6379);

        REDIS_CONTAINER.start();
    }

    @DynamicPropertySource
    public static void dynamicPropertySource(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.redis.port", REDIS_CONTAINER::getFirstMappedPort);
    }
}
