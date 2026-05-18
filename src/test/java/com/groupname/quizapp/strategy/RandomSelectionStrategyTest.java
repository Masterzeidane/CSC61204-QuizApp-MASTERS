package com.groupname.quizapp.strategy;

import com.groupname.quizapp.model.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link RandomSelectionStrategy}.
 */
class RandomSelectionStrategyTest {

    private RandomSelectionStrategy strategy;
    private List<Question> questionBank;

    @BeforeEach
    void setUp() {
        strategy = new RandomSelectionStrategy();
        Question q1 = new Question("Cat", "Easy", "Q1", "A", Arrays.asList("B"));
        Question q2 = new Question("Cat", "Easy", "Q2", "A", Arrays.asList("B"));
        Question q3 = new Question("Cat", "Easy", "Q3", "A", Arrays.asList("B"));
        questionBank = Arrays.asList(q1, q2, q3);
    }

    @Test
    void select_validAmount_returnsSubset() {
        List<Question> selected = strategy.select(questionBank, 2);

        assertAll("Verify selected questions",
                () -> assertEquals(2, selected.size(), "Should return exactly 2 questions"),
                () -> assertTrue(questionBank.containsAll(selected), "Selected questions must be from the bank")
        );
    }

    @Test
    void select_exactBankSize_returnsAllQuestionsShuffled() {
        List<Question> selected = strategy.select(questionBank, 3);
        
        assertAll("Verify full bank selection",
                () -> assertEquals(3, selected.size(), "Should return all 3 questions"),
                () -> assertTrue(selected.containsAll(questionBank), "Selected must contain all bank questions")
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {4, 10, 100})
    void select_amountExceedsBankSize_throwsIllegalArgumentException(int invalidAmount) {
        assertThrows(IllegalArgumentException.class, 
                () -> strategy.select(questionBank, invalidAmount),
                "Selecting more questions than available should throw exception");
    }
}
