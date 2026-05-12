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
    private int catScore;
    private int mouseScore;
    
    private int catIndex = 0;
    private int mouseIndex = 47;

    public GameRoom(int catScore, int mouseScore) {
        this.catScore = catScore;
        this.mouseScore = mouseScore;
        
        setTitle("Catch Mouse - Real-Time Action!");
        setSize(800, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(new Color(33, 33, 33));
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(33, 33, 33));
        headerPanel.setBorder(new EmptyBorder(20, 20, 0, 20));

        JLabel titleLabel = new JLabel("Cat Score: " + catScore + "  |  Mouse Score: " + mouseScore, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        
        add(headerPanel, BorderLayout.NORTH);

        // Main Container
        JPanel mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setBackground(new Color(33, 33, 33));
        add(mainContainer, BorderLayout.CENTER);

        // Single Board Panel (Merged Tiles)
        int boardWidth = 8 * 85;
        int boardHeight = 6 * 85;
        
        int iconSize = 70;
        ImageIcon catIcon = loadTransparentIcon("mainplay/cat.png", iconSize);
        ImageIcon mouseIcon = loadTransparentIcon("mainplay/mouse.png", iconSize);

        JPanel boardPanel = new JPanel() {
            private final ImageIcon floorIcon = GameConfig.getTileIcon(0, 85, 85);
            private final ImageIcon wallIcon = GameConfig.getTileIcon(1, 85, 85);

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
                    
                    if (GameConfig.isWall(i)) {
                        if (wallIcon != null) g2.drawImage(wallIcon.getImage(), x, y, 85, 85, this);
                    } else {
                        if (floorIcon != null) g2.drawImage(floorIcon.getImage(), x, y, 85, 85, this);
                    }
                }

                // Draw Mouse
                int mr = mouseIndex / 8;
                int mc = mouseIndex % 8;
                int mx = mc * 85 + (85 - iconSize) / 2;
                int my = mr * 85 + (85 - iconSize) / 2;
                g2.drawImage(mouseIcon.getImage(), mx, my, iconSize, iconSize, this);
                
                // Draw Cat
                int cr = catIndex / 8;
                int cc = catIndex % 8;
                int cx = cc * 85 + (85 - iconSize) / 2;
                int cy = cr * 85 + (85 - iconSize) / 2;
                g2.drawImage(catIcon.getImage(), cx, cy, iconSize, iconSize, this);
            }
        };
        boardPanel.setPreferredSize(new Dimension(boardWidth, boardHeight));
        boardPanel.setBorder(new LineBorder(new Color(63, 81, 181), 5));

        java.util.function.Consumer<Boolean> checkWinCondition = (isCatMove) -> {
            if (catIndex == mouseIndex) {
                JOptionPane.showMessageDialog(GameRoom.this, "The Cat caught the Mouse!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                mainplay.GameRoom resetRoom = new mainplay.GameRoom(catScore + 1, mouseScore);
                resetRoom.setVisible(true);
                dispose();
            }
        };

        InputMap im = mainContainer.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = mainContainer.getActionMap();

        // Cat Controls (WASD)
        im.put(KeyStroke.getKeyStroke("W"), "CAT_UP");
        im.put(KeyStroke.getKeyStroke("S"), "CAT_DOWN");
        im.put(KeyStroke.getKeyStroke("A"), "CAT_LEFT");
        im.put(KeyStroke.getKeyStroke("D"), "CAT_RIGHT");

        // Mouse Controls (Arrow Keys)
        im.put(KeyStroke.getKeyStroke("UP"), "MOUSE_UP");
        im.put(KeyStroke.getKeyStroke("DOWN"), "MOUSE_DOWN");
        im.put(KeyStroke.getKeyStroke("LEFT"), "MOUSE_LEFT");
        im.put(KeyStroke.getKeyStroke("RIGHT"), "MOUSE_RIGHT");

        // Cat Actions
        am.put("CAT_UP", createMoveAction(true, -1, 0, boardPanel, checkWinCondition));
        am.put("CAT_DOWN", createMoveAction(true, 1, 0, boardPanel, checkWinCondition));
        am.put("CAT_LEFT", createMoveAction(true, 0, -1, boardPanel, checkWinCondition));
        am.put("CAT_RIGHT", createMoveAction(true, 0, 1, boardPanel, checkWinCondition));

        // Mouse Actions
        am.put("MOUSE_UP", createMoveAction(false, -1, 0, boardPanel, checkWinCondition));
        am.put("MOUSE_DOWN", createMoveAction(false, 1, 0, boardPanel, checkWinCondition));
        am.put("MOUSE_LEFT", createMoveAction(false, 0, -1, boardPanel, checkWinCondition));
        am.put("MOUSE_RIGHT", createMoveAction(false, 0, 1, boardPanel, checkWinCondition));

        mainContainer.add(boardPanel);
        System.out.println("Real-time GameRoom initialized.");
    }
    
    private AbstractAction createMoveAction(boolean isCat, int dRow, int dCol, JPanel boardPanel, java.util.function.Consumer<Boolean> checkWinCondition) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                int currentIndex = isCat ? catIndex : mouseIndex;
                int r = currentIndex / 8 + dRow;
                int c = currentIndex % 8 + dCol;
                
                if (r >= 0 && r < 6 && c >= 0 && c < 8) {
                    int targetIndex = r * 8 + c;
                    if (!GameConfig.isWall(targetIndex)) {
                        if (isCat) {
                            catIndex = targetIndex;
                        } else {
                            mouseIndex = targetIndex;
                        }
                        boardPanel.repaint();
                        checkWinCondition.accept(isCat);
                    }
                }
            }
        };
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
