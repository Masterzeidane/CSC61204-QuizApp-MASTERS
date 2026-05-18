package com.masters.quizapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.masters.quizapp.model.Question;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link QuizApiService}.
 */
class QuizApiServiceTest {

    private QuizApiService apiService;

    @BeforeEach
    void setUp() {
        apiService = new QuizApiService();
    }

    @Test
    void fetchQuestions_validApiCall_returnsListOfQuestions() {
        // Arrange
        int amountToFetch = 3;

        // Act
        // This makes a real HTTP request to the OpenTDB API.
        List<Question> questions = apiService.fetchQuestions(amountToFetch);

        // Assert
        assertAll("Verify fetched questions",
                () -> assertNotNull(questions, "Returned question list should not be null"),
                () -> assertEquals(amountToFetch, questions.size(), "Should fetch exactly 3 questions"),
                () -> assertNotNull(questions.get(0).getQuestionText(), "First question text should not be null"),
                () -> assertFalse(questions.get(0).getQuestionText().trim().isEmpty(),
                        "First question text should not be empty"));
    }

    @Test
    void fetchQuestions_simulatedNetworkFailure_returnsFallbackQuestions() {
        // Arrange
        int amountToFetch = 3;
        
        // Act
        // Interrupt the current thread to simulate an InterruptedException when httpClient.send is called
        Thread.currentThread().interrupt();
        List<Question> questions = apiService.fetchQuestions(amountToFetch);
        
        // Clear the interrupted status so subsequent tests are not affected
        Thread.interrupted();

        // Assert
        assertAll("Verify fallback questions on failure",
                () -> assertNotNull(questions, "Returned fallback list should not be null"),
                () -> assertEquals(5, questions.size(), "Should return exactly 5 fallback questions"),
                () -> assertEquals("Software Architecture", questions.get(0).getCategory(), "First fallback should be Software Architecture"),
                () -> assertEquals("Controller", questions.get(0).getCorrectAnswer(), "First fallback correct answer should be Controller")
        );
    }
}
