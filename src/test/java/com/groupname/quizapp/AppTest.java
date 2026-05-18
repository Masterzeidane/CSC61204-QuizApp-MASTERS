package com.groupname.quizapp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Smoke test to verify the project compiles and JUnit 5 is wired correctly.
 */
class AppTest {

    @Test
    void applicationStartsWithoutException() {
        assertDoesNotThrow(() -> App.main(new String[]{}));
    }
}
