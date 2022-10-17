package com.construction_worker_forum_back;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DummyTest {

    @Test
    void dummyTest() {
        int actual = 1;
        Assertions.assertEquals(1, actual);
    }
}
