package com.masters.quizapp.service;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.masters.quizapp.model.Question;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Service for communicating with the Open Trivia Database API.
 */
public class QuizApiService {

    private static final String API_URL_FORMAT = "https://opentdb.com/api.php?amount=%d&type=multiple";
    private static final int HTTP_SUCCESS_MIN = 200;
    private static final int HTTP_SUCCESS_MAX = 299;

    private final HttpClient httpClient;
    private final Gson gson;

    /**
     * Constructs the QuizApiService and initializes the HTTP client and Gson
     * parser.
     */
    public QuizApiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    /**
     * Fetches questions from the Open Trivia Database API.
     * Uses graceful degradation: in case of network errors or non-2xx responses,
     * it returns a fallback local bank without printing stack traces.
     *
     * @param amount the number of questions to fetch
     * @return a list of parsed Question objects, or fallback questions on failure
     */
    public List<Question> fetchQuestions(int amount) {
        try {
            HttpRequest request = buildRequest(amount);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (isSuccessful(response.statusCode())) {
                return parseResponse(response.body());
            }
            return getFallbackBank();
        } catch (Exception e) {
            // Silently swallow exception and gracefully degrade
            return getFallbackBank();
        }
    }

    private HttpRequest buildRequest(int amount) {
        String url = String.format(API_URL_FORMAT, amount);
        return HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
    }

    private boolean isSuccessful(int statusCode) {
        return statusCode >= HTTP_SUCCESS_MIN && statusCode <= HTTP_SUCCESS_MAX;
    }

    private List<Question> parseResponse(String jsonBody) {
        OpenTdbResponse tdbResponse = gson.fromJson(jsonBody, OpenTdbResponse.class);
        List<Question> mappedQuestions = new ArrayList<>();

        if (tdbResponse != null && tdbResponse.results != null) {
            for (OpenTdbResult result : tdbResponse.results) {
                mappedQuestions.add(mapToDomainQuestion(result));
            }
        }
        return mappedQuestions;
    }

    private Question mapToDomainQuestion(OpenTdbResult result) {
        return new Question(
                result.category,
                result.difficulty,
                result.question,
                result.correctAnswer,
                result.incorrectAnswers);
    }

    private List<Question> getFallbackBank() {
        return Arrays.asList(
                new Question("Software Architecture", "Medium", 
                        "In the MVC architectural pattern, which component is responsible for handling user input and updating the model?",
                        "Controller", Arrays.asList("View", "Model", "Observer")),
                new Question("Software Design", "Medium", 
                        "Which SOLID principle states that a class should have only one reason to change?",
                        "Single Responsibility Principle", Arrays.asList("Open/Closed Principle", "Liskov Substitution Principle", "Dependency Inversion Principle")),
                new Question("Object-Oriented Programming", "Easy", 
                        "Which core OOP concept refers to the bundling of data and the methods that operate on that data into a single unit?",
                        "Encapsulation", Arrays.asList("Inheritance", "Polymorphism", "Abstraction")),
                new Question("Design Patterns", "Hard", 
                        "Which creational design pattern is best suited for constructing complex objects step by step?",
                        "Builder", Arrays.asList("Singleton", "Factory Method", "Adapter")),
                new Question("Software Design", "Hard", 
                        "The Open/Closed Principle dictates that software entities should be open for extension but closed for what?",
                        "Modification", Arrays.asList("Instantiation", "Inheritance", "Execution"))
        );
    }

    // --- Private Helper DTOs for Gson Mapping ---

    private static class OpenTdbResponse {
        @SerializedName("results")
        List<OpenTdbResult> results;
    }

    private static class OpenTdbResult {
        @SerializedName("category")
        String category;
        @SerializedName("difficulty")
        String difficulty;
        @SerializedName("question")
        String question;
        @SerializedName("correct_answer")
        String correctAnswer;
        @SerializedName("incorrect_answers")
        List<String> incorrectAnswers;
    }
}
