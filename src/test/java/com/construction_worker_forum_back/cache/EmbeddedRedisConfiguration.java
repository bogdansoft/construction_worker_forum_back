package com.construction_worker_forum_back.cache;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import redis.embedded.RedisServer;

@TestConfiguration
class EmbeddedRedisConfiguration implements TestExecutionListener {

    private final RedisServer redisServer;

    public EmbeddedRedisConfiguration() {
        this.redisServer = new RedisServer();
    }

    @Override
    public void beforeTestExecution(TestContext testContext) {
        redisServer.start();
    }

    @Override
    public void afterTestExecution(TestContext testContext) {
       redisServer.stop();
    }
}
