package mainplay;

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
        
        setTitle("Catch Mouse - Player 1 (Cat)");
        setSize(800, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(new Color(33, 33, 33));
        setLayout(new BorderLayout());

        // Header Panel (Centered Turn Label)
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(33, 33, 33));
        headerPanel.setBorder(new EmptyBorder(20, 20, 0, 20));

        turnLabel = new JLabel("Player 1: Cat", SwingConstants.CENTER);
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
        rollButton.setBackground(new Color(63, 81, 181));
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
        mainContainer.setBackground(new Color(33, 33, 33));
        add(mainContainer, BorderLayout.CENTER);

        // Game Board Panel (6x8 grid = 48 boxes)
        JPanel gameBoardPanel = new JPanel(new GridLayout(6, 8, 8, 8));
        gameBoardPanel.setBackground(new Color(45, 52, 54));
        gameBoardPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(63, 81, 181), 5),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        int iconSize = 70;
        ImageIcon catIcon = loadTransparentIcon("mainplay/cat.png", iconSize);
        ImageIcon mouseIcon = loadTransparentIcon("mainplay/mouse.png", iconSize);

        // Check if placement phase
        boolean tempPlacement = true;
        if (currentBoardState != null) {
            for (int val : currentBoardState) {
                if ((val & 1) != 0) {
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

        for (int i = 0; i < 48; i++) {
            final int index = i; // final index for use in listener
            JPanel box = new JPanel();
            box.setPreferredSize(new Dimension(85, 85));
            
            Color defaultBg = new Color(53, 59, 72);
            Color hoverBg = new Color(173, 216, 230);
            Color defaultBorder = new Color(63, 81, 181);
            Color hoverBorder = Color.CYAN;

            box.setBackground(defaultBg);
            box.setBorder(new LineBorder(defaultBorder, 2));
            box.setLayout(new GridBagLayout());

            JLabel iconLabel = new JLabel();
            // Restore ONLY Player 1's pieces (Cats) using bitmask
            if (currentBoardState != null && i < currentBoardState.length) {
                if ((currentBoardState[i] & 1) != 0 && (currentBoardState[i] & 2) != 0) {
                    iconLabel.setIcon(mouseIcon); 
                } else if ((currentBoardState[i] & 1) != 0) {
                    iconLabel.setIcon(catIcon);
                } else if ((currentBoardState[i] & 2) != 0) {
                    iconLabel.setIcon(mouseIcon);
                }
            }
            box.add(iconLabel);

            box.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    // Always show hover now that overlapping is allowed
                    if (iconLabel.getIcon() == null) {
                        box.setBackground(hoverBg);
                        box.setBorder(new LineBorder(hoverBorder, 2));
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    box.setBackground(defaultBg);
                    box.setBorder(new LineBorder(defaultBorder, 2));
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    // Check if move is valid (only 1 box away)
                    int prevIndex = -1;
                    if (currentBoardState != null) {
                        for (int i = 0; i < 48; i++) {
                            if ((currentBoardState[i] & 1) != 0) {
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
                        int currRow = index / 8;
                        int currCol = index % 8;

                        int dist = Math.max(Math.abs(prevRow - currRow), Math.abs(prevCol - currCol));
                        if (dist > 1) {
                            JOptionPane.showMessageDialog(GameRoom.this, "You can only move 1 box at a time!", "Invalid Move", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }

                    // Allow overlapping pieces as requested
                    // Bitmask state: 1 (Cat), 2 (Mouse), 3 (Both)
                    
                    // Immediately remove old cat icon from UI
                    for (Component comp : gameBoardPanel.getComponents()) {
                        if (comp instanceof JPanel) {
                            JPanel p = (JPanel) comp;
                            for (Component child : p.getComponents()) {
                                if (child instanceof JLabel) {
                                    JLabel label = (JLabel) child;
                                    if (label.getIcon() == catIcon) {
                                        label.setIcon(null);
                                    }
                                }
                            }
                        }
                    }

                    // Place the new Cat icon visually
                    iconLabel.setIcon(catIcon);

                    // Update internal state
                    for (int i = 0; i < 48; i++) {
                        currentBoardState[i] &= 2; // Keep only mouse
                    }
                    currentBoardState[index] |= 1; // Place cat

                    if (isPlacement) {
                        remainingMoves = 0; // End placement immediately
                    } else {
                        remainingMoves--;
                        diceLabel.setText("MOVES LEFT: " + remainingMoves);
                    }

                    // Check for capture
                    if ((currentBoardState[index] & 2) != 0) {
                        JOptionPane.showMessageDialog(GameRoom.this, "The Cat caught the Mouse!", "Caught!", JOptionPane.INFORMATION_MESSAGE);
                        mainplay.GameRoom resetRoom = new mainplay.GameRoom(new int[48], 0, 0, 600, 600, catScore + 1, mouseScore);
                        resetRoom.setVisible(true);
                        dispose();
                        return;
                    }

                    if (remainingMoves <= 0) {
                        // Disable further clicks
                        for (Component comp : gameBoardPanel.getComponents()) {
                            for (java.awt.event.MouseListener ml : comp.getMouseListeners()) {
                                comp.removeMouseListener(ml);
                            }
                        }

                        // Transition to Mouse
                        Timer transitionTimer = new Timer(500, event -> {
                            mainplay2.GameRoom nextRoom = new mainplay2.GameRoom(currentBoardState, catMoves + 1, mouseMoves, GameRoom.this.catTime, GameRoom.this.mouseTime, catScore, mouseScore);
                            nextRoom.setVisible(true);
                            dispose();
                        });
                        transitionTimer.setRepeats(false);
                        transitionTimer.start();
                    }
                }
            });

            gameBoardPanel.add(box);
        }

        mainContainer.add(gameBoardPanel);
        System.out.println("Grid initialized with board state.");
    }



    /**
     * Loads an image and returns a scaled ImageIcon.
     * - If the image already has a transparent corner (pre-transparent PNG), it is
     *   used as-is (only converted to ARGB for rendering).
     * - If the image has a solid background, the top-left pixel color is sampled and
     *   removed within a tolerance range, making those pixels transparent.
     */
    private ImageIcon loadTransparentIcon(String path, int size) {
        try {
            BufferedImage original = ImageIO.read(new File(path));
            BufferedImage transparent = new BufferedImage(
                original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_ARGB);

            // Check if top-left pixel is already transparent (alpha == 0)
            int topLeft = original.getRGB(0, 0);
            int topLeftAlpha = (topLeft >> 24) & 0xFF;
            boolean alreadyTransparent = (topLeftAlpha == 0);

            if (alreadyTransparent) {
                // Image already has transparency — just copy all pixels as-is
                Graphics2D g2d = transparent.createGraphics();
                g2d.drawImage(original, 0, 0, null);
                g2d.dispose();
            } else {
                // Sample top-left pixel RGB as background color to remove
                int bgR = (topLeft >> 16) & 0xFF;
                int bgG = (topLeft >> 8) & 0xFF;
                int bgB = topLeft & 0xFF;
                int tolerance = 40; // pixels within this distance become transparent

                for (int y = 0; y < original.getHeight(); y++) {
                    for (int x = 0; x < original.getWidth(); x++) {
                        int pixel = original.getRGB(x, y);
                        int a = (pixel >> 24) & 0xFF;
                        int r = (pixel >> 16) & 0xFF;
                        int g = (pixel >> 8) & 0xFF;
                        int b = pixel & 0xFF;

                        if (a == 0 ||
                            (Math.abs(r - bgR) <= tolerance &&
                             Math.abs(g - bgG) <= tolerance &&
                             Math.abs(b - bgB) <= tolerance)) {
                            transparent.setRGB(x, y, 0x00000000); // fully transparent
                        } else {
                            transparent.setRGB(x, y, pixel);
                        }
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
