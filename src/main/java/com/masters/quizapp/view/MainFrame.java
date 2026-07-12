package com.masters.quizapp.view;

import com.masters.quizapp.controller.QuizController;
import com.masters.quizapp.model.Question;
import com.masters.quizapp.service.QuizApiService;
import com.masters.quizapp.strategy.DifficultySelectionStrategy;
import com.masters.quizapp.strategy.RandomSelectionStrategy;

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
    private JLabel offlineLabel;
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
        initMenuBar();

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

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        offlineLabel = new JLabel("Offline mode: using built-in question bank");
        offlineLabel.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 13));
        offlineLabel.setForeground(Color.RED);
        offlineLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        offlineLabel.setVisible(false);

        questionLabel = new JLabel("Question text...");
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        questionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        topPanel.add(offlineLabel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        topPanel.add(questionLabel);

        panel.add(topPanel, BorderLayout.NORTH);

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
        
        controller.setSelectionStrategy("any".equalsIgnoreCase(diff)
                ? new RandomSelectionStrategy()
                : new DifficultySelectionStrategy(diff));

        // Pure delegation to controller
        controller.startNewQuiz(amount, diff);
        
        // Update offline label visibility
        offlineLabel.setVisible(controller.isOfflineMode());
        
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

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void showAboutDialog() {
        String aboutMessage = "<html>" +
                "<h2>Educational Testing System</h2>" +
                "<p><b>Version:</b> 1.0-SNAPSHOT</p>" +
                "<p><b>Description:</b> An interactive educational testing application featuring dynamic quiz loading and offline fallback.</p>" +
                "<br/>" +
                "<p><b>Design Patterns & Architecture:</b></p>" +
                "<ul>" +
                "  <li>Model-View-Controller (MVC)</li>" +
                "  <li>Builder Pattern (for Quiz construction)</li>" +
                "  <li>Strategy Pattern (for Question selection)</li>" +
                "</ul>" +
                "<p><b>Dependencies:</b> Gson, JUnit 5, JaCoCo</p>" +
                "</html>";
        JOptionPane.showMessageDialog(this, aboutMessage, "About Educational Testing System", JOptionPane.INFORMATION_MESSAGE);
    }

}
