package com.groupname.quizapp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder class for creating {@link Quiz} instances.
 */
public class QuizBuilder {

    private static final int DEFAULT_TIME_LIMIT = 30;
    private static final int DEFAULT_PASSING_SCORE = 0;
    
    private static final String ERR_EMPTY_TITLE = "Quiz title cannot be empty.";
    private static final String ERR_EMPTY_QUESTIONS = "Quiz must contain at least one question.";

    private String title = "";
    private int timeLimit = DEFAULT_TIME_LIMIT;
    private int passingScore = DEFAULT_PASSING_SCORE;
    private List<Question> questions = new ArrayList<>();

    /**
     * Sets the title of the quiz.
     *
     * @param title the quiz title
     * @return the current builder instance
     */
    public QuizBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Sets the time limit of the quiz.
     *
     * @param timeLimit the time limit
     * @return the current builder instance
     */
    public QuizBuilder setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
        return this;
    }

    /**
     * Sets the passing score of the quiz.
     *
     * @param passingScore the passing score
     * @return the current builder instance
     */
    public QuizBuilder setPassingScore(int passingScore) {
        this.passingScore = passingScore;
        return this;
    }

    /**
     * Adds a single question to the quiz.
     *
     * @param question the question to add
     * @return the current builder instance
     */
    public QuizBuilder addQuestion(Question question) {
        this.questions.add(question);
        return this;
    }

    /**
     * Sets the entire list of questions for the quiz.
     *
     * @param questions the list of questions
     * @return the current builder instance
     */
    public QuizBuilder setQuestions(List<Question> questions) {
        this.questions = (questions == null) ? new ArrayList<>() : new ArrayList<>(questions);
        return this;
    }

    /**
     * Builds and validates the Quiz instance.
     *
     * @return a fully constructed Quiz
     * @throws IllegalArgumentException if the title is empty
     * @throws IllegalStateException if the questions list is empty
     */
    public Quiz build() {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException(ERR_EMPTY_TITLE);
        }
        if (questions == null || questions.isEmpty()) {
            throw new IllegalStateException(ERR_EMPTY_QUESTIONS);
        }
        return new Quiz(title, timeLimit, passingScore, questions);
    }
}
