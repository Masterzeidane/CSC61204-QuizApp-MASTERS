package com.masters.quizapp.strategy;

import com.masters.quizapp.model.Question;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link DifficultySelectionStrategy}.
 */
class DifficultySelectionStrategyTest {

    @Test
    void select_matchingDifficultyPreferred_returnsPreferredQuestionsFirst() {
        // Arrange
        Question q1 = new Question("Category1", "easy", "Q1", "A1", Arrays.asList("A2"));
        Question q2 = new Question("Category2", "hard", "Q2", "A1", Arrays.asList("A2"));
        Question q3 = new Question("Category3", "easy", "Q3", "A1", Arrays.asList("A2"));
        List<Question> bank = Arrays.asList(q1, q2, q3);

        DifficultySelectionStrategy strategy = new DifficultySelectionStrategy("easy");

        // Act
        List<Question> selected = strategy.select(bank, 2);

        // Assert
        assertEquals(2, selected.size());
        assertTrue(selected.stream().allMatch(q -> q.getDifficulty().equalsIgnoreCase("easy")), 
                "All selected questions should match target difficulty 'easy' when available");
    }

    @Test
    void select_insufficientMatchingDifficulty_topsUpWithOtherDifficulties() {
        // Arrange
        Question q1 = new Question("Category1", "easy", "Q1", "A1", Arrays.asList("A2"));
        Question q2 = new Question("Category2", "hard", "Q2", "A1", Arrays.asList("A2"));
        Question q3 = new Question("Category3", "medium", "Q3", "A1", Arrays.asList("A2"));
        List<Question> bank = Arrays.asList(q1, q2, q3);

        DifficultySelectionStrategy strategy = new DifficultySelectionStrategy("easy");

        // Act
        List<Question> selected = strategy.select(bank, 2);

        // Assert
        assertEquals(2, selected.size());
        boolean hasEasy = selected.stream().anyMatch(q -> q.getDifficulty().equalsIgnoreCase("easy"));
        assertTrue(hasEasy, "The matching difficulty question should be included");
    }

    @Test
    void select_amountExceedsBankSize_throwsIllegalArgumentException() {
        // Arrange
        Question q1 = new Question("Category1", "easy", "Q1", "A1", Arrays.asList("A2"));
        Question q2 = new Question("Category2", "hard", "Q2", "A1", Arrays.asList("A2"));
        List<Question> bank = Arrays.asList(q1, q2);

        DifficultySelectionStrategy strategy = new DifficultySelectionStrategy("easy");

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            strategy.select(bank, 3);
        });

        assertEquals("Requested amount exceeds the available questions in the bank.", exception.getMessage());
    }
}
