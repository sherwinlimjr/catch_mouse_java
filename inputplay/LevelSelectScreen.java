package inputplay;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import main.Main;
import main.SharedAssets;

public class LevelSelectScreen extends JPanel {

    public LevelSelectScreen(Main parentFrame) {
        setLayout(new BorderLayout());
        setBackground(new Color(40, 30, 25)); // Prevent white flash

        // Background Panel
        JPanel bgPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                BufferedImage bgImage = SharedAssets.getImage("main/menu_bg.png");
                if (bgImage != null) {
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(new Color(40, 30, 25));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        bgPanel.setOpaque(false);
        add(bgPanel, BorderLayout.CENTER);

        // Grid Container
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel gridPanel = new JPanel(new GridLayout(6, 5, 10, 10));
        gridPanel.setOpaque(false);

        ImageIcon lockIcon = SharedAssets.getIcon("mainplay/lock.png", 32, 32);

        for (int i = 1; i <= 30; i++) {
            final int levelNum = i;
            boolean isUnlocked = (levelNum <= Main.maxUnlockedLevel);

            JButton btn = new JButton();
            btn.setPreferredSize(new Dimension(80, 60));
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(new Color(255, 213, 79), 2));
            btn.setFont(new Font("Arial Black", Font.BOLD, 16));

            if (isUnlocked) {
                btn.setText(String.valueOf(levelNum));
                btn.setBackground(new Color(63, 81, 181));
                btn.setForeground(Color.WHITE);
                btn.addActionListener(e -> {
                    parentFrame.showGameRoom(levelNum, 0, 0);
                });
            } else {
                if (lockIcon != null)
                    btn.setIcon(lockIcon);
                else
                    btn.setText("🔒");
                btn.setBackground(new Color(80, 80, 80));
                btn.setEnabled(false);
            }
            gridPanel.add(btn);
        }

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        bgPanel.add(gridPanel, gbc);

        // Back Button
        JButton backBtn = new JButton("BACK TO MENU") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(255, 50, 50), 0, getHeight(),
                        new Color(139, 0, 0));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
            }
        };
        backBtn.setPreferredSize(new Dimension(200, 50));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(new Font("Arial Black", Font.BOLD, 16));
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.addActionListener(e -> {
            parentFrame.showMainMenu();
        });

        // Bottom Panel for Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        bottomPanel.setOpaque(false);

        // Sound Toggle Button
        JButton soundBtn = new JButton(main.SoundManager.isMuted() ? "SOUND: OFF" : "SOUND: ON") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c1 = main.SoundManager.isMuted() ? new Color(100, 100, 100) : new Color(100, 80, 40);
                GradientPaint gp = new GradientPaint(0, 0, c1, 0, getHeight(), c1.darker());
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
            }
        };
        soundBtn.setPreferredSize(new Dimension(150, 50));
        soundBtn.setForeground(Color.WHITE);
        soundBtn.setFont(new Font("Arial Black", Font.BOLD, 14));
        soundBtn.setContentAreaFilled(false);
        soundBtn.setBorderPainted(false);
        soundBtn.addActionListener(e -> {
            main.SoundManager.toggleMute();
            soundBtn.setText(main.SoundManager.isMuted() ? "SOUND: OFF" : "SOUND: ON");
            soundBtn.repaint();
        });

        bottomPanel.add(soundBtn);
        bottomPanel.add(backBtn);

        gbc.gridy = 1;
        gbc.weighty = 0.2;
        gbc.insets = new Insets(0, 0, 30, 0);
        bgPanel.add(bottomPanel, gbc);
    }
}
