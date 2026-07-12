package com.masters.quizapp.controller;

import com.masters.quizapp.model.Question;
import com.masters.quizapp.model.Quiz;
import com.masters.quizapp.builder.QuizBuilder;
import com.masters.quizapp.service.QuizApiService;

import java.util.List;

/**
 * Acts as the central mediator between the View and the Services/Models.
 * Controls the flow of the application.
 */
public class QuizController {

    private static final String ERR_QUIZ_FINISHED = "Quiz is already finished or not started.";

    private final QuizApiService apiService;
    private Quiz currentQuiz;
    private int currentQuestionIndex;
    private int currentScore;

    /**
     * Constructs a new QuizController.
     *
     * @param apiService the service used to fetch questions
     */
    public QuizController(QuizApiService apiService) {
        this.apiService = apiService;
        this.currentQuestionIndex = 0;
        this.currentScore = 0;
    }

    /**
     * Starts a new quiz by fetching questions of the requested difficulty and initializing the model.
     *
     * @param amount the number of questions to fetch
     * @param difficulty the requested difficulty level
     */
    public void startNewQuiz(int amount, String difficulty) {
        List<Question> questions = apiService.fetchQuestions(amount, difficulty);
        
        currentQuiz = new QuizBuilder()
                .setTitle("Trivia Quiz - " + difficulty)
                .setQuestions(questions)
                .build();
                
        currentQuestionIndex = 0;
        currentScore = 0;
    }

    /**
     * Submits an answer for the current question and updates the score.
     * Moves the internal pointer to the next question.
     *
     * @param selectedAnswer the answer chosen by the user
     * @return true if the answer was correct, false otherwise
     * @throws IllegalStateException if the quiz is already finished
     */
    public boolean submitAnswer(String selectedAnswer) {
        if (isQuizFinished()) {
            throw new IllegalStateException(ERR_QUIZ_FINISHED);
        }

        Question currentQuestion = currentQuiz.getQuestions().get(currentQuestionIndex);
        boolean isCorrect = currentQuestion.getCorrectAnswer().equalsIgnoreCase(selectedAnswer);
        
        if (isCorrect) {
            currentScore++;
        }
        
        currentQuestionIndex++;
        return isCorrect;
    }

    /**
     * Retrieves the current active question.
     *
     * @return the current question, or null if finished
     */
    public Question getCurrentQuestion() {
        if (isQuizFinished()) {
            return null;
        }
        return currentQuiz.getQuestions().get(currentQuestionIndex);
    }

    /**
     * Checks if the quiz has been completely answered.
     *
     * @return true if all questions are answered or quiz is not started
     */
    public boolean isQuizFinished() {
        return currentQuiz == null || currentQuestionIndex >= currentQuiz.getQuestions().size();
    }

    /**
     * Retrieves the current score of the user.
     *
     * @return the current score
     */
    public int getCurrentScore() {
        return currentScore;
    }

    /**
     * Retrieves the current active quiz model.
     *
     * @return the current quiz
     */
    public Quiz getCurrentQuiz() {
        return currentQuiz;
    }

    /**
     * Checks if the quiz is running in offline mode (using built-in question bank).
     *
     * @return true if running in offline mode, false otherwise
     */
    public boolean isOfflineMode() {
        return apiService.isFallbackTriggered();
    }
}
