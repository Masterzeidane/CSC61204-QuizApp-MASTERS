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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for communicating with the Open Trivia Database API.
 */
public class QuizApiService {

    private static final Logger LOGGER = Logger.getLogger(QuizApiService.class.getName());

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
        return fetchQuestions(amount, null);
    }

    /**
     * Fetches questions from the Open Trivia Database API with a specified difficulty.
     * Uses graceful degradation: in case of network errors or non-2xx responses,
     * it returns a fallback local bank while logging the failure details.
     *
     * @param amount the number of questions to fetch
     * @param difficulty the requested difficulty level (easy, medium, hard)
     * @return a list of parsed Question objects, or fallback questions on failure
     */
    public List<Question> fetchQuestions(int amount, String difficulty) {
        try {
            HttpRequest request = buildRequest(amount, difficulty);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (isSuccessful(response.statusCode())) {
                return parseResponse(response.body());
            }
            LOGGER.log(Level.WARNING, "API request failed with status code: {0}. Falling back to local question bank.", response.statusCode());
            return getFallbackBank(difficulty);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception occurred while fetching questions: {0}. Falling back to local question bank.", e.getMessage());
            return getFallbackBank(difficulty);
        }
    }

    private HttpRequest buildRequest(int amount, String difficulty) {
        String url = String.format(API_URL_FORMAT, amount);
        if (difficulty != null && !difficulty.trim().isEmpty()) {
            url += "&difficulty=" + difficulty.trim().toLowerCase();
        }
        return HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
    }

    private List<Question> getFallbackBank(String difficulty) {
        List<Question> allFallback = getFallbackBank();
        if (difficulty == null || difficulty.trim().isEmpty()) {
            return allFallback;
        }
        List<Question> filtered = new ArrayList<>();
        for (Question q : allFallback) {
            if (q.getDifficulty().equalsIgnoreCase(difficulty)) {
                filtered.add(q);
            }
        }
        return filtered.isEmpty() ? allFallback : filtered;
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
                unescapeHtml(result.category),
                unescapeHtml(result.difficulty),
                unescapeHtml(result.question),
                unescapeHtml(result.correctAnswer),
                unescapeHtmlList(result.incorrectAnswers));
    }

    String unescapeHtml(String input) {
        if (input == null || !input.contains("&")) {
            return input;
        }

        java.util.Map<String, String> commonEntities = new java.util.HashMap<>();
        commonEntities.put("&quot;", "\"");
        commonEntities.put("&amp;", "&");
        commonEntities.put("&lt;", "<");
        commonEntities.put("&gt;", ">");
        commonEntities.put("&apos;", "'");
        commonEntities.put("&ldquo;", "\"");
        commonEntities.put("&rdquo;", "\"");
        commonEntities.put("&lsquo;", "'");
        commonEntities.put("&rsquo;", "'");
        commonEntities.put("&ndash;", "-");
        commonEntities.put("&mdash;", "—");
        commonEntities.put("&deg;", "°");

        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("&[a-zA-Z0-9#]+;");
        java.util.regex.Matcher matcher = pattern.matcher(input);
        StringBuilder sb = new StringBuilder();
        int lastEnd = 0;

        while (matcher.find()) {
            sb.append(input, lastEnd, matcher.start());
            String entity = matcher.group();
            if (commonEntities.containsKey(entity)) {
                sb.append(commonEntities.get(entity));
            } else if (entity.startsWith("&#x") || entity.startsWith("&#X")) {
                try {
                    int code = Integer.parseInt(entity.substring(3, entity.length() - 1), 16);
                    sb.append((char) code);
                } catch (NumberFormatException e) {
                    sb.append(entity);
                }
            } else if (entity.startsWith("&#")) {
                try {
                    int code = Integer.parseInt(entity.substring(2, entity.length() - 1));
                    sb.append((char) code);
                } catch (NumberFormatException e) {
                    sb.append(entity);
                }
            } else {
                sb.append(entity);
            }
            lastEnd = matcher.end();
        }
        sb.append(input.substring(lastEnd));
        return sb.toString();
    }

    private List<String> unescapeHtmlList(List<String> list) {
        if (list == null) return null;
        List<String> unescaped = new ArrayList<>();
        for (String s : list) {
            unescaped.add(unescapeHtml(s));
        }
        return unescaped;
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
