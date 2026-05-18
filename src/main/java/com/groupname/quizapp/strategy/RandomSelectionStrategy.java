package com.groupname.quizapp.strategy;

import com.groupname.quizapp.model.Question;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A concrete strategy that randomly selects questions from the bank.
 */
public class RandomSelectionStrategy implements QuestionSelectionStrategy {

    private static final int START_INDEX = 0;
    private static final String ERR_NOT_ENOUGH_QUESTIONS = 
            "Requested amount exceeds the available questions in the bank.";

    /**
     * Selects a random subset of questions from the bank.
     *
     * @param bank the list of available questions
     * @param amount the number of questions to select
     * @return a randomly shuffled list of selected questions
     * @throws IllegalArgumentException if amount exceeds bank size
     */
    @Override
    public List<Question> select(List<Question> bank, int amount) {
        validateAmount(bank.size(), amount);
        
        List<Question> shuffledBank = new ArrayList<>(bank);
        Collections.shuffle(shuffledBank);
        
        return shuffledBank.subList(START_INDEX, amount);
    }

    /**
     * Validates that the requested amount does not exceed the bank size.
     *
     * @param bankSize the total number of questions available
     * @param amount the requested number of questions
     */
    private void validateAmount(int bankSize, int amount) {
        if (amount > bankSize) {
            throw new IllegalArgumentException(ERR_NOT_ENOUGH_QUESTIONS);
        }
    }
}
