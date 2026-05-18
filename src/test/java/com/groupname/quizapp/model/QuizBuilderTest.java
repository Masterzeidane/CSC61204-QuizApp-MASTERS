package com.groupname.quizapp.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link QuizBuilder}.
 */
class QuizBuilderTest {

    private Question dummyQuestion;

    @BeforeEach
    void setUp() {
        dummyQuestion = new Question("Math", "Easy", "1+1?", "2", Arrays.asList("1", "3", "4"));
    }

    @Test
    void build_validInputs_returnsConstructedQuiz() {
        Quiz quiz = new QuizBuilder()
                .setTitle("Math Quiz")
                .setTimeLimit(60)
                .setPassingScore(50)
                .addQuestion(dummyQuestion)
                .build();

        assertAll("Verify Quiz Properties",
                () -> assertEquals("Math Quiz", quiz.getTitle(), "Title should match"),
                () -> assertEquals(60, quiz.getTimeLimit(), "Time limit should match"),
                () -> assertEquals(50, quiz.getPassingScore(), "Passing score should match"),
                () -> assertEquals(1, quiz.getQuestions().size(), "Should contain exactly 1 question")
        );
    }

    @Test
    void build_setQuestionsList_returnsQuizWithAllQuestions() {
        List<Question> questions = Arrays.asList(dummyQuestion, dummyQuestion);
        Quiz quiz = new QuizBuilder()
                .setTitle("Science Quiz")
                .setQuestions(questions)
                .build();

        assertEquals(2, quiz.getQuestions().size(), "Should contain exactly 2 questions");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   ", "\t", "\n"})
    void build_invalidTitle_throwsIllegalArgumentException(String invalidTitle) {
        QuizBuilder builder = new QuizBuilder()
                .setTitle(invalidTitle)
                .addQuestion(dummyQuestion);

        assertThrows(IllegalArgumentException.class, builder::build, 
                "Building with invalid title should throw exception");
    }

    @Test
    void build_emptyQuestions_throwsIllegalStateException() {
        QuizBuilder builder = new QuizBuilder()
                .setTitle("Valid Title"); // No questions added

        assertThrows(IllegalStateException.class, builder::build, 
                "Building without questions should throw exception");
    }

    @Test
    void build_nullQuestionsList_throwsIllegalStateException() {
        QuizBuilder builder = new QuizBuilder()
                .setTitle("Valid Title")
                .setQuestions(null); // Explicitly null

        assertThrows(IllegalStateException.class, builder::build, 
                "Building with null questions list should throw exception");
    }
}
