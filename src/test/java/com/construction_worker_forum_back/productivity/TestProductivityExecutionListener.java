package com.construction_worker_forum_back.productivity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import java.util.concurrent.TimeUnit;

@Slf4j
public class TestProductivityExecutionListener implements TestExecutionListener {
    private long startTime;

    @Override
    public void beforeTestExecution(TestContext testContext) {
        startTime = System.currentTimeMillis();
        log.info("LOGGER => test method started");
    }

    @Override
    public void afterTestExecution(TestContext testContext) {
        long endTime = System.currentTimeMillis();
        log.info("LOGGER => test method completed execution at {} seconds", TimeUnit.MILLISECONDS.toSeconds(endTime - startTime));
    }
}
