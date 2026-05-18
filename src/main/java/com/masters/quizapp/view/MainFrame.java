package com.masters.quizapp.view;

import com.masters.quizapp.controller.QuizController;
import com.masters.quizapp.model.Question;
import com.masters.quizapp.service.QuizApiService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Main View container for the Educational Testing System.
 * Adheres strictly to MVC — delegates all logic to QuizController.
 */
public class MainFrame extends JFrame {
    
    private static final String PANEL_SETUP = "SETUP";
    private static final String PANEL_TESTING = "TESTING";
    private static final String PANEL_RESULT = "RESULT";

    private final QuizController controller;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    // UI Components
    private JComboBox<Integer> amountDropdown;
    private JComboBox<String> difficultyDropdown;
    private JLabel questionLabel;
    private ButtonGroup optionsGroup;
    private JRadioButton[] optionButtons;
    private JLabel scoreLabel;

    /**
     * Constructs the main application frame and initializes MVC wiring.
     */
    public MainFrame() {
        // MVC Initialization
        QuizApiService apiService = new QuizApiService();
        this.controller = new QuizController(apiService);

        // Frame Configuration
        setTitle("Educational Testing System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 450);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Build screens
        mainPanel.add(createSetupPanel(), PANEL_SETUP);
        mainPanel.add(createTestingPanel(), PANEL_TESTING);
        mainPanel.add(createResultPanel(), PANEL_RESULT);

        add(mainPanel);
        cardLayout.show(mainPanel, PANEL_SETUP);
    }

    private JPanel createSetupPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Educational Testing System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));

        amountDropdown = new JComboBox<>(new Integer[]{5, 10, 15, 20});
        difficultyDropdown = new JComboBox<>(new String[]{"easy", "medium", "hard"});
        
        JButton startButton = new JButton("Start Quiz");
        startButton.setFont(new Font("Arial", Font.BOLD, 14));
        startButton.addActionListener(e -> handleStartClick());

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; 
        panel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1; gbc.gridy = 1; 
        panel.add(new JLabel("Number of Questions:"), gbc);
        gbc.gridx = 1; 
        panel.add(amountDropdown, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; 
        panel.add(new JLabel("Difficulty:"), gbc);
        gbc.gridx = 1; 
        panel.add(difficultyDropdown, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; 
        panel.add(startButton, gbc);

        return panel;
    }

    private JPanel createTestingPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        questionLabel = new JLabel("Question text...");
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(questionLabel, BorderLayout.NORTH);

        JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        optionsGroup = new ButtonGroup();
        optionButtons = new JRadioButton[4];
        
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JRadioButton("Option " + (i + 1));
            optionButtons[i].setFont(new Font("Arial", Font.PLAIN, 14));
            optionsGroup.add(optionButtons[i]);
            optionsPanel.add(optionButtons[i]);
        }
        panel.add(optionsPanel, BorderLayout.CENTER);

        JButton nextButton = new JButton("Next");
        nextButton.setFont(new Font("Arial", Font.BOLD, 14));
        nextButton.addActionListener(e -> handleNextClick());
        panel.add(nextButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createResultPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 0; gbc.gridy = 0;

        JLabel titleLabel = new JLabel("Quiz Completed!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, gbc);

        scoreLabel = new JLabel("Final Score: 0/0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 18));
        scoreLabel.setForeground(new Color(34, 139, 34)); // Forest Green
        gbc.gridy = 1;
        panel.add(scoreLabel, gbc);

        JButton playAgainButton = new JButton("Play Again");
        playAgainButton.setFont(new Font("Arial", Font.BOLD, 14));
        playAgainButton.addActionListener(e -> cardLayout.show(mainPanel, PANEL_SETUP));
        gbc.gridy = 2;
        panel.add(playAgainButton, gbc);

        return panel;
    }

    private void handleStartClick() {
        int amount = (Integer) amountDropdown.getSelectedItem();
        String diff = (String) difficultyDropdown.getSelectedItem();
        
        // Pure delegation to controller
        controller.startNewQuiz(amount, diff);
        
        loadNextQuestion();
        cardLayout.show(mainPanel, PANEL_TESTING);
    }

    private void handleNextClick() {
        String selectedAnswer = getSelectedAnswer();
        if (selectedAnswer == null) {
            JOptionPane.showMessageDialog(this, "Please select an answer to continue.");
            return;
        }

        // Delegate scoring logic entirely to controller
        controller.submitAnswer(selectedAnswer);

        if (controller.isQuizFinished()) {
            showResults();
        } else {
            loadNextQuestion();
        }
    }

    private void loadNextQuestion() {
        Question currentQuestion = controller.getCurrentQuestion();
        if (currentQuestion == null) return;

        // Wrap in HTML so long questions word-wrap in the JLabel
        questionLabel.setText("<html>" + currentQuestion.getQuestionText() + "</html>");
        List<String> options = currentQuestion.getShuffledOptions();

        optionsGroup.clearSelection();
        for (int i = 0; i < optionButtons.length; i++) {
            if (i < options.size()) {
                optionButtons[i].setText(options.get(i));
                optionButtons[i].setActionCommand(options.get(i)); // Used to retrieve selection
                optionButtons[i].setVisible(true);
            } else {
                optionButtons[i].setVisible(false); // Hide unused buttons for True/False questions
            }
        }
    }

    private void showResults() {
        int finalScore = controller.getCurrentScore();
        int total = controller.getCurrentQuiz().getQuestions().size();
        scoreLabel.setText("Final Score: " + finalScore + " / " + total);
        cardLayout.show(mainPanel, PANEL_RESULT);
    }

    private String getSelectedAnswer() {
        ButtonModel selection = optionsGroup.getSelection();
        return selection != null ? selection.getActionCommand() : null;
    }

    /**
     * Application Entry Point.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
