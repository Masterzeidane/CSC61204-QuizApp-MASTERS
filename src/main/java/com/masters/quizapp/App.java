package com.masters.quizapp;

import com.masters.quizapp.view.MainFrame;
import javax.swing.SwingUtilities;

/**
 * Application entry point for the Educational Testing System.
 */
public class App {

    public static void main(String[] args) {
        System.out.println("=== Educational Testing System ===");
        
        // Launch the GUI on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
