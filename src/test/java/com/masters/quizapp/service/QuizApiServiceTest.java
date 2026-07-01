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

    @Test
    void unescapeHtml_variousHtmlEntities_returnsDecodedStrings() {
        assertAll("HTML decoding verification",
                () -> assertEquals("Hello \"World\"", apiService.unescapeHtml("Hello &quot;World&quot;"), "Should decode double quotes"),
                () -> assertEquals("It's a test", apiService.unescapeHtml("It&#039;s a test"), "Should decode decimal single quotes"),
                () -> assertEquals("It's a test", apiService.unescapeHtml("It&#x27;s a test"), "Should decode hex single quotes"),
                () -> assertEquals("A & B", apiService.unescapeHtml("A &amp; B"), "Should decode ampersands"),
                () -> assertEquals("1 < 2", apiService.unescapeHtml("1 &lt; 2"), "Should decode less than"),
                () -> assertEquals("2 > 1", apiService.unescapeHtml("2 &gt; 1"), "Should decode greater than"),
                () -> assertEquals("Unicode: é, °, —", apiService.unescapeHtml("Unicode: &#233;, &deg;, &mdash;"), "Should decode various unicode/named entities")
        );
    }

    @Test
    void fetchQuestions_withDifficultyAndNetworkFailure_returnsFilteredFallbackQuestions() {
        // Arrange
        int amountToFetch = 3;
        
        // Act
        // Interrupt the current thread to simulate network failure
        Thread.currentThread().interrupt();
        List<Question> questions = apiService.fetchQuestions(amountToFetch, "easy");
        
        // Clear the interrupted status
        Thread.interrupted();

        // Assert
        assertAll("Verify filtered fallback questions",
                () -> assertNotNull(questions, "Returned fallback list should not be null"),
                () -> assertEquals(1, questions.size(), "Should return exactly 1 easy fallback question"),
                () -> assertEquals("Object-Oriented Programming", questions.get(0).getCategory(), "Category should be Object-Oriented Programming"),
                () -> assertEquals("Easy", questions.get(0).getDifficulty(), "Difficulty should be Easy")
        );
    }
}
