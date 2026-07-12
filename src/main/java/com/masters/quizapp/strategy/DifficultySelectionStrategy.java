package com.masters.quizapp.strategy;

import com.masters.quizapp.model.Question;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Question selection strategy that prefers questions matching the target difficulty,
 * and tops up randomly with other questions from the bank if there are not enough.
 */
public class DifficultySelectionStrategy implements QuestionSelectionStrategy {

    private static final String ERR_NOT_ENOUGH_QUESTIONS =
            "Requested amount exceeds the available questions in the bank.";
    private final String targetDifficulty;

    /**
     * Constructs a DifficultySelectionStrategy with a target difficulty.
     *
     * @param targetDifficulty the difficulty level to prefer (e.g., "easy", "medium", "hard")
     */
    public DifficultySelectionStrategy(String targetDifficulty) {
        this.targetDifficulty = targetDifficulty;
    }

    /**
     * Selects questions from the bank. Prefers matching difficulty, topping up randomly from others.
     *
     * @param bank   the list of available questions
     * @param amount the number of questions to select
     * @return a list of selected questions
     * @throws IllegalArgumentException if the requested amount exceeds the size of the bank
     */
    @Override
    public List<Question> select(List<Question> bank, int amount) {
        if (amount > bank.size()) {
            throw new IllegalArgumentException(ERR_NOT_ENOUGH_QUESTIONS);
        }
        List<Question> preferred = new ArrayList<>();
        List<Question> others = new ArrayList<>();
        for (Question q : bank) {
            if (q.getDifficulty().equalsIgnoreCase(targetDifficulty)) {
                preferred.add(q);
            } else {
                others.add(q);
            }
        }
        Collections.shuffle(preferred);
        Collections.shuffle(others);
        preferred.addAll(others);
        return new ArrayList<>(preferred.subList(0, amount));
    }
}
