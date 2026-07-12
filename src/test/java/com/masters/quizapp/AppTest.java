package com.masters.quizapp;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

/**
 * Smoke test to verify the project compiles and JUnit 5 is wired correctly.
 */
class AppTest {

    @Test
    void main_applicationLaunch_doesNotThrowException() {
        assertDoesNotThrow(() -> App.main(new String[] {}));
    }
}
