package com.masters.quizapp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a single question in the Educational Testing System.
 */
public class Question {

    private final String category;
    private final String difficulty;
    private final String questionText;
    private final String correctAnswer;
    private final List<String> incorrectAnswers;

    /**
     * Constructs a new Question.
     *
     * @param category         the category of the question
     * @param difficulty       the difficulty level
     * @param questionText     the actual text of the question
     * @param correctAnswer    the correct answer string
     * @param incorrectAnswers a list of incorrect answer strings
     */
    public Question(String category, String difficulty, String questionText,
            String correctAnswer, List<String> incorrectAnswers) {
        this.category = category;
        this.difficulty = difficulty;
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
        this.incorrectAnswers = Collections.unmodifiableList(new ArrayList<>(incorrectAnswers));
    }

    /**
     * Retrieves the category.
     *
     * @return the category string
     */
    public String getCategory() {
        return category;
    }

    /**
     * Retrieves the difficulty level.
     *
     * @return the difficulty string
     */
    public String getDifficulty() {
        return difficulty;
    }

    /**
     * Retrieves the question text.
     *
     * @return the question text
     */
    public String getQuestionText() {
        return questionText;
    }

    /**
     * Retrieves the correct answer.
     *
     * @return the correct answer string
     */
    public String getCorrectAnswer() {
        return correctAnswer;
    }

    /**
     * Retrieves the unmodifiable list of incorrect answers.
     *
     * @return the list of incorrect answers
     */
    public List<String> getIncorrectAnswers() {
        return incorrectAnswers;
    }

    /**
     * Returns a list of all possible answers (correct and incorrect), shuffled
     * randomly.
     *
     * @return a shuffled list of all options
     */
    public List<String> getShuffledOptions() {
        List<String> allOptions = new ArrayList<>(this.incorrectAnswers);
        allOptions.add(this.correctAnswer);
        Collections.shuffle(allOptions);
        return allOptions;
    }
}
