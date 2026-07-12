package com.masters.quizapp.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Question} model class.
 */
class QuestionTest {

    private Question question;

    @BeforeEach
    void setUp() {
        question = new Question("Science", "Hard", "What is the speed of light?",
                "299,792,458 m/s", Arrays.asList("100 m/s", "1000 m/s", "Light speed"));
    }

    @Test
    void getCategory_validInitialization_returnsCorrectCategory() {
        assertEquals("Science", question.getCategory(), "Category should match constructor input");
    }

    @Test
    void getDifficulty_validInitialization_returnsCorrectDifficulty() {
        assertEquals("Hard", question.getDifficulty(), "Difficulty should match constructor input");
    }

    @Test
    void getQuestionText_validInitialization_returnsCorrectQuestionText() {
        assertEquals("What is the speed of light?", question.getQuestionText(),
                "Question text should match constructor input");
    }

    @Test
    void getCorrectAnswer_validInitialization_returnsCorrectAnswer() {
        assertEquals("299,792,458 m/s", question.getCorrectAnswer(),
                "Correct answer should match constructor input");
    }

    @Test
    void getIncorrectAnswers_validInitialization_returnsCorrectList() {
        List<String> incorrectAnswers = question.getIncorrectAnswers();
        assertAll("Verify incorrect answers list",
                () -> assertEquals(3, incorrectAnswers.size(), "Should contain exactly 3 incorrect answers"),
                () -> assertTrue(incorrectAnswers.contains("100 m/s"), "Should contain provided element"));
    }

    @Test
    void getShuffledOptions_validInitialization_returnsCombinedList() {
        List<String> allOptions = question.getShuffledOptions();
        assertAll("Verify all options combined",
                () -> assertEquals(4, allOptions.size(), "Should contain 1 correct + 3 incorrect = 4 options"),
                () -> assertTrue(allOptions.contains("299,792,458 m/s"), "Should contain the correct answer"),
                () -> assertTrue(allOptions.contains("1000 m/s"), "Should contain an incorrect answer"));
    }
}
