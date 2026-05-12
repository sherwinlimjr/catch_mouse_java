package mainplay2;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class GameRoom extends JFrame {
    private JLabel turnLabel;
    private Timer gameTimer;
    private int catTime;
    private int mouseTime;
    private int catMoves;
    private int mouseMoves;
    private int catScore;
    private int mouseScore;
    private int diceValue = 0;
    private boolean hasRolled = false;
    private JLabel diceLabel;
    private JButton rollButton;
    private int remainingMoves = 0;
    private int[] currentBoardState;

    private String formatTime(int seconds) {
        int mins = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", mins, secs);
    }

    public GameRoom(int[] boardState, int catMoves, int mouseMoves, int catTime, int mouseTime, int catScore, int mouseScore) {
        this.catMoves = catMoves;
        this.mouseMoves = mouseMoves;
        this.catTime = catTime;
        this.mouseTime = mouseTime;
        this.catScore = catScore;
        this.mouseScore = mouseScore;
        this.currentBoardState = boardState != null ? boardState.clone() : new int[48];

        setTitle("Catch Mouse - Player 2 (Mouse)");
        setSize(800, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(new Color(45, 52, 54));
        setLayout(new BorderLayout());

        // Header Panel (Centered Turn Label)
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(45, 52, 54));
        headerPanel.setBorder(new EmptyBorder(20, 20, 0, 20));

        turnLabel = new JLabel("Player 2: Mouse", SwingConstants.CENTER);
        turnLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        turnLabel.setForeground(Color.WHITE);
        headerPanel.add(turnLabel);

        // Dice Section
        JPanel dicePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        dicePanel.setOpaque(false);
        
        diceLabel = new JLabel("DICE: -", SwingConstants.CENTER);
        diceLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        diceLabel.setForeground(Color.YELLOW);
        diceLabel.setPreferredSize(new Dimension(100, 40));
        
        rollButton = new JButton("ROLL DICE");
        rollButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        rollButton.setBackground(new Color(46, 125, 50));
        rollButton.setForeground(Color.WHITE);
        rollButton.setFocusPainted(false);
        rollButton.addActionListener(e -> {
            if (!hasRolled) {
                diceValue = (int)(Math.random() * 6) + 1;
                diceLabel.setText("DICE: " + diceValue + " (" + diceValue + " MOVES)");
                remainingMoves = diceValue;
                hasRolled = true;
                rollButton.setEnabled(false);
            }
        });

        dicePanel.add(diceLabel);
        dicePanel.add(rollButton);
        headerPanel.add(dicePanel);
        
        add(headerPanel, BorderLayout.NORTH);

        // Timer logic removed

        // Main Container
        JPanel mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setBackground(new Color(45, 52, 54));
        add(mainContainer, BorderLayout.CENTER);

        // Single Board Panel (Merged Tiles)
        int boardWidth = 8 * 85;
        int boardHeight = 6 * 85;
        int iconSize = 70;
        ImageIcon catIcon = loadTransparentIcon("mainplay/cat.png", iconSize);
        ImageIcon mouseIcon = loadTransparentIcon("mainplay/mouse.png", iconSize);

        // Check if placement phase for Mouse
        boolean tempPlacement = true;
        if (currentBoardState != null) {
            for (int val : currentBoardState) {
                if ((val & 2) != 0) {
                    tempPlacement = false;
                    break;
                }
            }
        }
        final boolean isPlacement = tempPlacement;

        if (isPlacement) {
            dicePanel.setVisible(false);
            hasRolled = true; // Allow clicking without rolling
        }

        JPanel boardPanel = new JPanel() {
            private final ImageIcon floorIcon = mainplay.GameConfig.getTileIcon(0, 85, 85);
            private final ImageIcon wallIcon = mainplay.GameConfig.getTileIcon(1, 85, 85);

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

                for (int i = 0; i < 48; i++) {
                    int r = i / 8;
                    int c = i % 8;
                    int x = c * 85;
                    int y = r * 85;

                    if (mainplay.GameConfig.isWall(i)) {
                        if (wallIcon != null) g2.drawImage(wallIcon.getImage(), x, y, 85, 85, this);
                    } else {
                        if (floorIcon != null) g2.drawImage(floorIcon.getImage(), x, y, 85, 85, this);
                    }
                }

                // Draw Icons
                if (currentBoardState != null) {
                    for (int i = 0; i < 48; i++) {
                        int r = i / 8;
                        int c = i % 8;
                        int x = c * 85 + (85 - iconSize) / 2;
                        int y = r * 85 + (85 - iconSize) / 2;

                        if ((currentBoardState[i] & 1) != 0) {
                            g2.drawImage(catIcon.getImage(), x, y, iconSize, iconSize, this);
                        }
                        if ((currentBoardState[i] & 2) != 0) {
                            g2.drawImage(mouseIcon.getImage(), x, y, iconSize, iconSize, this);
                        }
                    }
                }
            }
        };
        boardPanel.setPreferredSize(new Dimension(boardWidth, boardHeight));
        boardPanel.setBorder(new LineBorder(new Color(46, 125, 50), 5));

        java.util.function.IntConsumer attemptMove = index -> {
            if (index < 0 || index >= 48) return;

            if (mainplay.GameConfig.isWall(index)) {
                JOptionPane.showMessageDialog(GameRoom.this, "You cannot move into a wall!", "Blocked", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int prevIndex = -1;
            if (currentBoardState != null) {
                for (int i = 0; i < 48; i++) {
                    if ((currentBoardState[i] & 2) != 0) {
                        prevIndex = i;
                        break;
                    }
                }
            }

            if (prevIndex != -1) {
                if (!hasRolled) {
                    JOptionPane.showMessageDialog(GameRoom.this, "Roll the dice first!", "Roll Dice", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int prevRow = prevIndex / 8;
                int prevCol = prevIndex % 8;
                int r = index / 8;
                int c = index % 8;
                int dist = Math.abs(prevRow - r) + Math.abs(prevCol - c);
                
                if (dist > 1) {
                    JOptionPane.showMessageDialog(GameRoom.this, "You can only move 1 tile at a time!", "Invalid Move", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            for (int i = 0; i < 48; i++) {
                currentBoardState[i] &= 1;
            }
            currentBoardState[index] |= 2;

            if (isPlacement) {
                remainingMoves = 0;
            } else {
                remainingMoves--;
                diceLabel.setText("MOVES LEFT: " + remainingMoves);
            }

            boardPanel.repaint();

            boolean caught = (currentBoardState[index] & 1) != 0;

            if (caught) {
                JOptionPane.showMessageDialog(GameRoom.this, "The Mouse was caught by the Cat!", "Caught!", JOptionPane.INFORMATION_MESSAGE);
                mainplay.GameRoom resetRoom = new mainplay.GameRoom(new int[48], 0, 0, 600, 600, catScore + 1, mouseScore);
                resetRoom.setVisible(true);
                dispose();
                return;
            }

            if (remainingMoves <= 0) {
                mainplay.GameRoom nextRoom = new mainplay.GameRoom(currentBoardState, catMoves + 1, mouseMoves + 1, GameRoom.this.catTime, GameRoom.this.mouseTime, catScore, mouseScore);
                nextRoom.setVisible(true);
                dispose();
            }
        };

        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int c = e.getX() / 85;
                int r = e.getY() / 85;
                int index = r * 8 + c;
                attemptMove.accept(index);
            }
        });

        InputMap im = mainContainer.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = mainContainer.getActionMap();

        String[] keys = {"UP", "DOWN", "LEFT", "RIGHT"};
        int[] dRow = {-1, 1, 0, 0};
        int[] dCol = {0, 0, -1, 1};

        for (int k = 0; k < 4; k++) {
            final int dr = dRow[k];
            final int dc = dCol[k];
            im.put(KeyStroke.getKeyStroke(keys[k]), keys[k]);
            am.put(keys[k], new AbstractAction() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (isPlacement) return;

                    int prevIndex = -1;
                    if (currentBoardState != null) {
                        for (int i = 0; i < 48; i++) {
                            if ((currentBoardState[i] & 2) != 0) {
                                prevIndex = i;
                                break;
                            }
                        }
                    }

                    if (prevIndex != -1) {
                        int r = prevIndex / 8 + dr;
                        int c = prevIndex % 8 + dc;
                        if (r >= 0 && r < 6 && c >= 0 && c < 8) {
                            attemptMove.accept(r * 8 + c);
                        }
                    }
                }
            });
        }

        mainContainer.add(boardPanel);
    }



    private ImageIcon loadTransparentIcon(String path, int size) {
        try {
            BufferedImage original = ImageIO.read(new File(path));
            BufferedImage transparent = new BufferedImage(
                original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_ARGB);

            int bgColor = original.getRGB(0, 0);
            int bgR = (bgColor >> 16) & 0xFF;
            int bgG = (bgColor >> 8) & 0xFF;
            int bgB = bgColor & 0xFF;
            int tolerance = 40;

            for (int y = 0; y < original.getHeight(); y++) {
                for (int x = 0; x < original.getWidth(); x++) {
                    int pixel = original.getRGB(x, y);
                    int r = (pixel >> 16) & 0xFF;
                    int g = (pixel >> 8) & 0xFF;
                    int b = pixel & 0xFF;

                    if (Math.abs(r - bgR) <= tolerance &&
                        Math.abs(g - bgG) <= tolerance &&
                        Math.abs(b - bgB) <= tolerance) {
                        transparent.setRGB(x, y, 0x00FFFFFF);
                    } else {
                        transparent.setRGB(x, y, pixel);
                    }
                }
            }

            Image scaled = transparent.getScaledInstance(size, size, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (IOException e) {
            System.err.println("Could not load image: " + path);
            return new ImageIcon();
        }
    }
}
