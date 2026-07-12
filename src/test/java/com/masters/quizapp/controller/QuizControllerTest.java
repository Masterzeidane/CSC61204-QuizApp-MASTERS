package com.masters.quizapp.controller;

import com.masters.quizapp.model.Question;
import com.masters.quizapp.model.Quiz;
import com.masters.quizapp.service.QuizApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class QuizControllerTest {

    private QuizController controller;
    private QuizApiService mockApiService;

    @BeforeEach
    public void setUp() {
        // Create a manual stub to avoid external Mockito dependencies
        // This overrides the fetchQuestions method to return deterministic data
        mockApiService = new QuizApiService() {
            @Override
            public List<Question> fetchQuestions(int amount, String difficulty) {
                return fetchQuestions(amount);
            }

            @Override
            public List<Question> fetchQuestions(int amount) {
                return Arrays.asList(
                        new Question("Science", "easy", "Is the Earth round?", "Yes", Arrays.asList("No", "Maybe", "Unsure")),
                        new Question("Math", "easy", "What is 2+2?", "4", Arrays.asList("1", "2", "3"))
                );
            }
        };
        controller = new QuizController(mockApiService);
        controller.setSelectionStrategy((bank, n) -> new ArrayList<>(bank).subList(0, n));
    }

    @Test
    public void constructor_validInitialization_setsInitialValues() {
        assertAll("Initial State Verification",
                () -> assertNull(controller.getCurrentQuiz(), "Quiz should be null before starting"),
                () -> assertEquals(0, controller.getCurrentScore(), "Initial score should be 0"),
                () -> assertTrue(controller.isQuizFinished(), "Quiz is considered finished/empty before starting"),
                () -> assertNull(controller.getCurrentQuestion(), "Current question should be null")
        );
    }

    @Test
    public void startNewQuiz_validInputs_initializesQuizState() {
        controller.startNewQuiz(2, "easy");

        Quiz quiz = controller.getCurrentQuiz();
        assertAll("Started State Verification",
                () -> assertNotNull(quiz, "Quiz model should be instantiated"),
                () -> assertEquals("Trivia Quiz - easy", quiz.getTitle(), "Quiz title should match input difficulty"),
                () -> assertEquals(2, quiz.getQuestions().size(), "Should have exactly 2 questions from the stub"),
                () -> assertEquals(0, controller.getCurrentScore(), "Score should reset to 0"),
                () -> assertFalse(controller.isQuizFinished(), "Quiz should be actively running"),
                () -> assertNotNull(controller.getCurrentQuestion(), "First question should be loaded"),
                () -> assertEquals("Is the Earth round?", controller.getCurrentQuestion().getQuestionText())
        );
    }

    @Test
    public void submitAnswer_correctAnswer_incrementsScoreAndAdvances() {
        controller.startNewQuiz(2, "easy");
        
        boolean isCorrect = controller.submitAnswer("Yes");

        assertAll("Correct Answer Processing",
                () -> assertTrue(isCorrect, "Answer should be evaluated as correct"),
                () -> assertEquals(1, controller.getCurrentScore(), "Score should increment by 1"),
                () -> assertFalse(controller.isQuizFinished(), "Quiz should not be finished yet"),
                () -> assertEquals("What is 2+2?", controller.getCurrentQuestion().getQuestionText(), "Should advance to the next question")
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"YES", "yes", "yEs", "Yes"})
    public void submitAnswer_correctAnswerDifferentCase_incrementsScore(String answer) {
        controller.startNewQuiz(2, "easy");
        
        boolean isCorrect = controller.submitAnswer(answer);

        assertAll("Case Insensitive Verification",
                () -> assertTrue(isCorrect, "Answer evaluation should be case-insensitive"),
                () -> assertEquals(1, controller.getCurrentScore(), "Score should increment by 1")
        );
    }

    @Test
    public void submitAnswer_incorrectAnswer_doesNotIncrementScoreAndAdvances() {
        controller.startNewQuiz(2, "easy");
        
        boolean isCorrect = controller.submitAnswer("No");

        assertAll("Incorrect Answer Processing",
                () -> assertFalse(isCorrect, "Answer should be evaluated as incorrect"),
                () -> assertEquals(0, controller.getCurrentScore(), "Score should remain 0"),
                () -> assertFalse(controller.isQuizFinished(), "Quiz should not be finished yet"),
                () -> assertEquals("What is 2+2?", controller.getCurrentQuestion().getQuestionText(), "Should still advance to the next question")
        );
    }

    @Test
    public void submitAnswer_allQuestionsAnswered_finishesQuiz() {
        controller.startNewQuiz(2, "easy");
        
        controller.submitAnswer("Yes"); // Answer Q1
        controller.submitAnswer("4");   // Answer Q2

        assertAll("Quiz Completion",
                () -> assertTrue(controller.isQuizFinished(), "Quiz should be marked as finished"),
                () -> assertEquals(2, controller.getCurrentScore(), "Score should be 2/2"),
                () -> assertNull(controller.getCurrentQuestion(), "Current question should be null when finished")
        );
    }

    @Test
    public void submitAnswer_quizAlreadyFinished_throwsIllegalStateException() {
        controller.startNewQuiz(2, "easy");
        
        controller.submitAnswer("Yes"); // Answer Q1
        controller.submitAnswer("4");   // Answer Q2

        // Now the quiz is finished. Submitting an answer again should throw.
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            controller.submitAnswer("Some Answer");
        });

        assertEquals("Quiz is already finished or not started.", exception.getMessage());
    }
    
    @Test
    public void submitAnswer_quizNotStarted_throwsIllegalStateException() {
        // Submitting an answer before starting a quiz should immediately throw
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            controller.submitAnswer("Some Answer");
        });

        assertEquals("Quiz is already finished or not started.", exception.getMessage());
    }

    @Test
    public void isOfflineMode_queryStatus_returnsTrueIfFallbackTriggered() {
        QuizApiService offlineApiService = new QuizApiService() {
            @Override
            public boolean isFallbackTriggered() {
                return true;
            }
        };
        QuizController offlineController = new QuizController(offlineApiService);
        assertTrue(offlineController.isOfflineMode(), "isOfflineMode should return true if API service fallback is triggered");
    }
}
