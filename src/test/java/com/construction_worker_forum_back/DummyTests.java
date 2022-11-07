package com.construction_worker_forum_back;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("dev")
@SpringBootTest
class DummyTests {

    @Test
    void dummy1() {
        int actual = 1;
        assertEquals(1, actual);
    }
}
