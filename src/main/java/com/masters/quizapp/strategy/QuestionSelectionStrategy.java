package com.masters.quizapp.strategy;

import java.util.List;

import com.masters.quizapp.model.Question;

/**
 * Interface representing the Strategy Pattern for selecting questions.
 */
public interface QuestionSelectionStrategy {

    /**
     * Selects a specific amount of questions from the provided bank.
     *
     * @param bank   the list of available questions
     * @param amount the number of questions to select
     * @return a list of selected questions
     */
    List<Question> select(List<Question> bank, int amount);
}
