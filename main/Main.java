package main;

import javax.swing.*;
import inputplay.InputScreen;
import mainplay.GameRoom;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.*;

public class Main extends JFrame {
    private boolean isRunning;
    public static int maxUnlockedLevel = 1;

    public Main() {
        this.isRunning = false;
        setTitle("Cheese Caper: The Catch");
        setSize(800, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        showMainMenu();
    }

    public void showMainMenu() {
        JPanel bgPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                BufferedImage bgImage = SharedAssets.getImage("main/menu_bg.png");
                if (bgImage != null) {
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;

        // --- Logo (Custom Cartoon Style) ---
        CartoonLogo cartoonLogo = new CartoonLogo();
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 40, 0); // Spacing above buttons
        bgPanel.add(cartoonLogo, gbc);

        // --- Buttons ---
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 0, 15));
        buttonPanel.setOpaque(false);

        ModernButton playButton = new ModernButton("PLAY CAMPAIGN", new Color(46, 125, 50));
        playButton.setPreferredSize(new Dimension(250, 60));
        playButton.addActionListener(e -> {
            showLevelSelection();
        });

        ModernButton endlessButton = new ModernButton("ENDLESS MODE", new Color(194, 24, 91));
        endlessButton.setPreferredSize(new Dimension(250, 60));
        endlessButton.addActionListener(e -> {
            showGameRoom(10, 0, 0);
        });

        ModernButton exitButton = new ModernButton("EXIT GAME", new Color(66, 66, 66));
        exitButton.setPreferredSize(new Dimension(250, 60));
        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(playButton);
        buttonPanel.add(endlessButton);
        buttonPanel.add(exitButton);

        gbc.gridy = 1;
        bgPanel.add(buttonPanel, gbc);

        setContentPane(bgPanel);
        revalidate();
        repaint();
    }

    public void showLevelSelection() {
        setContentPane(new inputplay.LevelSelectScreen(this));
        revalidate();
        repaint();
    }

    public void showGameRoom(int level, int catScore, int mouseScore) {
        setContentPane(new mainplay.GameRoom(this, level, catScore, mouseScore));
        revalidate();
        repaint();
    }

    public void start() {
        isRunning = true;
        SwingUtilities.invokeLater(() -> {
            setVisible(true);
            System.out.println("Menu Window launched successfully.");
        });
    }

    public static void main(String[] args) {
        SharedAssets.preload();
        Main game = new Main();
        game.start();
    }
}