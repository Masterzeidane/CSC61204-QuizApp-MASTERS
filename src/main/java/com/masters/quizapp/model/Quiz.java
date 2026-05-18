package com.masters.quizapp.model;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a quiz containing a list of questions and configuration rules.
 */
public class Quiz {

    private final String title;
    private final int timeLimit;
    private final int passingScore;
    private final List<Question> questions;

    /**
     * Protected constructor to enforce the use of the Builder pattern.
     *
     * @param title        the title of the quiz
     * @param timeLimit    the time limit in minutes (or seconds)
     * @param passingScore the required score to pass
     * @param questions    the list of questions in this quiz
     */
    protected Quiz(String title, int timeLimit, int passingScore, List<Question> questions) {
        this.title = title;
        this.timeLimit = timeLimit;
        this.passingScore = passingScore;
        this.questions = Collections.unmodifiableList(new ArrayList<>(questions));
    }

    /**
     * Retrieves the quiz title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Retrieves the time limit.
     *
     * @return the time limit
     */
    public int getTimeLimit() {
        return timeLimit;
    }

    /**
     * Retrieves the passing score.
     *
     * @return the passing score
     */
    public int getPassingScore() {
        return passingScore;
    }

    /**
     * Retrieves the list of questions in the quiz.
     *
     * @return an unmodifiable list of questions
     */
    public List<Question> getQuestions() {
        return questions;
    }
}
