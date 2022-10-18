package com.construction_worker_forum_back;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class DummyTests {

    @Test
    void dummy1() {
        int actual = 1;
        assertEquals(1, actual);
    }
}
