package com.masters.quizapp.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the {@link Quiz} model class.
 */
class QuizTest {

    private Quiz quiz;

    @BeforeEach
    void setUp() {
        List<Question> emptyQuestions = Collections.emptyList();
        
        // Quiz constructor is protected, so we can access it within the same package.
        // We bypass the Builder for pure model testing.
        quiz = new Quiz("Midterm Exam", 45, 60, emptyQuestions);
    }

    @Test
    void getTitle_validInitialization_returnsCorrectTitle() {
        assertEquals("Midterm Exam", quiz.getTitle(), "Title should match the initialized value");
    }

    @Test
    void getTimeLimit_validInitialization_returnsCorrectTimeLimit() {
        assertEquals(45, quiz.getTimeLimit(), "Time limit should match the initialized value");
    }

    @Test
    void getPassingScore_validInitialization_returnsCorrectPassingScore() {
        assertEquals(60, quiz.getPassingScore(), "Passing score should match the initialized value");
    }

    @Test
    void getQuestions_validInitialization_returnsCorrectQuestionsList() {
        assertEquals(0, quiz.getQuestions().size(), "Questions list should be exactly as initialized");
    }
}
