package inputplay;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import main.Main;
import mainplay.GameRoom;

public class LevelSelectScreen extends JFrame {
    
    private static BufferedImage bgImage;
    private static ImageIcon lockIcon;

    static {
        try {
            bgImage = ImageIO.read(new File("main/menu_bg.png"));
            BufferedImage originalLock = ImageIO.read(new File("mainplay/lock.png"));
            lockIcon = new ImageIcon(originalLock.getScaledInstance(32, 32, Image.SCALE_SMOOTH));
        } catch (Exception e) {
            System.err.println("Preloading assets failed: " + e.getMessage());
        }
    }
    
    public LevelSelectScreen() {
        setTitle("Level Selection");
        setSize(800, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Background Panel
        JPanel bgPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bgImage != null) {
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(new Color(40, 30, 25));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        bgPanel.setBackground(new Color(40, 30, 25)); // Prevent white flash
        setContentPane(bgPanel);
        
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel gridPanel = new JPanel(new GridLayout(6, 5, 10, 10));
        gridPanel.setOpaque(false);
        
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
                    new GameRoom(levelNum, 0, 0).setVisible(true);
                    this.dispose();
                });
            } else {
                if (lockIcon != null) btn.setIcon(lockIcon);
                else btn.setText("🔒");
                btn.setBackground(new Color(80, 80, 80));
                btn.setEnabled(false);
            }
            gridPanel.add(btn);
        }
        
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        bgPanel.add(gridPanel, gbc);
        
        JButton backBtn = new JButton("BACK TO MENU") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(255, 50, 50), 0, getHeight(), new Color(139, 0, 0));
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
            new Main().start();
            this.dispose();
        });
        
        gbc.gridy = 1; gbc.weighty = 0.2;
        gbc.insets = new Insets(0, 0, 30, 0);
        bgPanel.add(backBtn, gbc);
    }
}
