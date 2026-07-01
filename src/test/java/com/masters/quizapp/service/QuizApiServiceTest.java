package com.masters.quizapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.masters.quizapp.model.Question;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
        QuizApiService stubApiService = new QuizApiService() {
            @Override
            HttpResponse<String> sendRequest(HttpRequest request) throws Exception {
                return new HttpResponse<String>() {
                    @Override
                    public int statusCode() {
                        return 200;
                    }

                    @Override
                    public HttpRequest request() {
                        return null;
                    }

                    @Override
                    public java.util.Optional<HttpResponse<String>> previousResponse() {
                        return java.util.Optional.empty();
                    }

                    @Override
                    public java.net.http.HttpHeaders headers() {
                        return null;
                    }

                    @Override
                    public String body() {
                        return "{\"response_code\":0,\"results\":["
                                + "{\"category\":\"General\",\"type\":\"multiple\",\"difficulty\":\"easy\",\"question\":\"Q1\",\"correct_answer\":\"A1\",\"incorrect_answers\":[\"A2\",\"A3\",\"A4\"]},"
                                + "{\"category\":\"General\",\"type\":\"multiple\",\"difficulty\":\"easy\",\"question\":\"Q2\",\"correct_answer\":\"A1\",\"incorrect_answers\":[\"A2\",\"A3\",\"A4\"]},"
                                + "{\"category\":\"General\",\"type\":\"multiple\",\"difficulty\":\"easy\",\"question\":\"Q3\",\"correct_answer\":\"A1\",\"incorrect_answers\":[\"A2\",\"A3\",\"A4\"]}"
                                + "]}";
                    }

                    @Override
                    public java.util.Optional<javax.net.ssl.SSLSession> sslSession() {
                        return java.util.Optional.empty();
                    }

                    @Override
                    public java.net.URI uri() {
                        return null;
                    }

                    @Override
                    public java.net.http.HttpClient.Version version() {
                        return null;
                    }
                };
            }
        };

        // Act
        List<Question> questions = stubApiService.fetchQuestions(amountToFetch);

        // Assert
        assertAll("Verify fetched questions",
                () -> assertNotNull(questions, "Returned question list should not be null"),
                () -> assertEquals(amountToFetch, questions.size(), "Should fetch exactly 3 questions"),
                () -> assertEquals("Q1", questions.get(0).getQuestionText(), "First question text should match"),
                () -> assertEquals("A1", questions.get(0).getCorrectAnswer(), "First correct answer should match"));
    }

    @Test
    void fetchQuestions_simulatedNetworkFailure_returnsFallbackQuestions() {
        // Arrange
        int amountToFetch = 3;

        // Act
        // Interrupt the current thread to simulate an InterruptedException when
        // httpClient.send is called
        Thread.currentThread().interrupt();
        List<Question> questions = apiService.fetchQuestions(amountToFetch);

        // Clear the interrupted status so subsequent tests are not affected
        Thread.interrupted();

        // Assert
        assertAll("Verify fallback questions on failure",
                () -> assertNotNull(questions, "Returned fallback list should not be null"),
                () -> assertEquals(5, questions.size(), "Should return exactly 5 fallback questions"),
                () -> assertEquals("Software Architecture", questions.get(0).getCategory(),
                        "First fallback should be Software Architecture"),
                () -> assertEquals("Controller", questions.get(0).getCorrectAnswer(),
                        "First fallback correct answer should be Controller"));
    }

    @Test
    void unescapeHtml_variousHtmlEntities_returnsDecodedStrings() {
        assertAll("HTML decoding verification",
                () -> assertEquals("Hello \"World\"", apiService.unescapeHtml("Hello &quot;World&quot;"),
                        "Should decode double quotes"),
                () -> assertEquals("It's a test", apiService.unescapeHtml("It&#039;s a test"),
                        "Should decode decimal single quotes"),
                () -> assertEquals("It's a test", apiService.unescapeHtml("It&#x27;s a test"),
                        "Should decode hex single quotes"),
                () -> assertEquals("A & B", apiService.unescapeHtml("A &amp; B"), "Should decode ampersands"),
                () -> assertEquals("1 < 2", apiService.unescapeHtml("1 &lt; 2"), "Should decode less than"),
                () -> assertEquals("2 > 1", apiService.unescapeHtml("2 &gt; 1"), "Should decode greater than"),
                () -> assertEquals("Unicode: é, °, —", apiService.unescapeHtml("Unicode: &#233;, &deg;, &mdash;"),
                        "Should decode various unicode/named entities"));
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
                () -> assertEquals("Object-Oriented Programming", questions.get(0).getCategory(),
                        "Category should be Object-Oriented Programming"),
                () -> assertEquals("Easy", questions.get(0).getDifficulty(), "Difficulty should be Easy"));
    }
}
