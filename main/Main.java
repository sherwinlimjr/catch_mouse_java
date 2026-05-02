package main;

import javax.swing.*;
import inputplay.InputScreen;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

public class Main extends JFrame {
    private boolean isRunning;

    public Main() {
        this.isRunning = false;
        setupWindow();
    }

    private void setupWindow() {
        setTitle("Catch Mouse Game - Menu");
        setSize(800, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(new Color(33, 33, 33));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;

        // --- Logo (Custom Cartoon Style) ---
        CartoonLogo cartoonLogo = new CartoonLogo();
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 40, 0); // Spacing above buttons
        add(cartoonLogo, gbc);

        // --- Buttons ---
        ModernButton playButton = new ModernButton("PLAY GAME", new Color(63, 81, 181));
        playButton.setPreferredSize(new Dimension(250, 70));
        playButton.addActionListener(e -> {
            InputScreen inputScreen = new InputScreen();
            inputScreen.setVisible(true);
            this.dispose();
        });

        ModernButton exitButton = new ModernButton("EXIT", new Color(198, 40, 40));
        exitButton.setPreferredSize(new Dimension(250, 70));
        exitButton.addActionListener(e -> {
            System.exit(0);
        });

        gbc.gridy = 1;
        add(playButton, gbc);

        gbc.gridy = 2;
        add(exitButton, gbc);
    }

    public void start() {
        isRunning = true;
        SwingUtilities.invokeLater(() -> {
            setVisible(true);
            System.out.println("Menu Window launched successfully.");
        });
    }



    public static void main(String[] args) {
        Main game = new Main();
        game.start();
    }
}
