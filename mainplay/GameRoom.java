package mainplay;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.border.EmptyBorder;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import main.Main;

public class GameRoom extends JPanel {
    private Main parentFrame;
    private int catScore;
    private int mouseScore;
    private int level;
    private int lives = 3;
    
    private int mouseRow = 1, mouseCol = 1;
    private int mouseDirection = 0; // 0=Right, 1=Left, 2=Up, 3=Down
    private int cheeseRow, cheeseCol;
    private int exitRow, exitCol;
    private int[][] mazeMap;
    
    private int cheeseCollected = 0;
    private int cheeseRequired = 3;
    
    private JLabel statsLabel;
    private JLabel levelLabel;
    private JPanel heartPanel;
    private Timer catAiTimer;
    
    private BufferedImage mouseImg;
    private BufferedImage mouseRunImg;
    private BufferedImage catImg;
    private BufferedImage floorImg;
    private BufferedImage wallImg;
    private BufferedImage cheeseImg;
    private BufferedImage trapImg;
    private BufferedImage doorOpenImg;
    private BufferedImage doorClosedImg;
    
    private int mouseHitFlash = 0;
    
    // Directional mouse sprites: [direction][frame] — 0=Right, 1=Left, 2=Up, 3=Down
    private BufferedImage[][] mouseDirFrames = new BufferedImage[4][2];
    // Directional cat sprites: [direction][frame] — 0=Right, 1=Left, 2=Up, 3=Down
    private BufferedImage[][] catPatrolFrames = new BufferedImage[4][2];
    private BufferedImage[][] catChaseFrames  = new BufferedImage[4][2];
    
    private int animationFrame = 0;
    private Timer animationTimer;
    private Timer mouseMoveTimer;     // Cooldown timer for mouse movement
    private boolean canMouseMove = true; // Whether mouse may move this tick
    private static final int MOUSE_MOVE_DELAY = 150; // ms between mouse steps
    
    private List<CatNPC> cats = new ArrayList<>();
    private boolean mouseStunned = false;
    private boolean isGameOver = false;

    public GameRoom(Main parentFrame, int level, int catScore, int mouseScore) {
        this.parentFrame = parentFrame;
        this.level = level;
        this.catScore = catScore;
        this.mouseScore = mouseScore;
        
        // 1. Initial Data
        this.mazeMap = GameConfig.generateMaze(level);
        int rows = mazeMap.length;
        int cols = mazeMap[0].length;
        
        loadGameAssets(); 
        spawnCheese();
        
        this.exitRow = rows - 2;
        this.exitCol = cols - 2;
        
        // 2. Level Rules & Cat Spawning
        cheeseRequired = 3 + (level * 2);
        int catCount = 1 + (level / 2);
        for (int i = 0; i < catCount; i++) {
            int r = 1 + (int)(Math.random() * (rows - 2));
            int c = 1 + (int)(Math.random() * (cols - 2));
            boolean onDoor = (r == exitRow && c == exitCol);
            if (mazeMap[r][c] == 0 && (r != 1 || c != 1) && !onDoor) {
                cats.add(new CatNPC(r, c, Math.max(1, c - 3), Math.min(cols - 2, c + 3)));
            } else {
                i--; // Retry
            }
        }

        // 3. UI Setup
        setLayout(new BorderLayout());
        setFocusable(true); // Needed to receive key events when acting as main panel
        requestFocusInWindow();

        // Header
        JPanel headerPanel = new JPanel(new GridLayout(1, 3));
        headerPanel.setBackground(new Color(60, 40, 30)); // Dark Woody Brown
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        // 1. Title
        JLabel titleLabel = new JLabel("CHEESE CAPER: THE CATCH");
        titleLabel.setFont(new Font("Arial Black", Font.BOLD, 22));
        titleLabel.setForeground(new Color(255, 213, 79)); // Gold/Yellow
        headerPanel.add(titleLabel);

        // 2. Level Center
        levelLabel = new JLabel("LEVEL " + level, SwingConstants.CENTER);
        levelLabel.setFont(new Font("Arial Black", Font.BOLD, 18));
        levelLabel.setForeground(Color.WHITE);
        headerPanel.add(levelLabel);

        // 3. Stats Right
        JPanel rightHeader = new JPanel(new BorderLayout());
        rightHeader.setOpaque(false);
        statsLabel = new JLabel("CHEESE: 0/" + cheeseRequired, SwingConstants.RIGHT);
        statsLabel.setFont(new Font("Arial Black", Font.BOLD, 16));
        statsLabel.setForeground(Color.WHITE);
        rightHeader.add(statsLabel, BorderLayout.NORTH);

        heartPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
        heartPanel.setOpaque(false);
        updateHearts();
        rightHeader.add(heartPanel, BorderLayout.SOUTH);
        headerPanel.add(rightHeader);

        add(headerPanel, BorderLayout.NORTH);

        // Game Board
        JPanel mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setBackground(new Color(215, 204, 200));
        add(mainContainer, BorderLayout.CENTER);

        JPanel boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int rows = mazeMap.length;
                int cols = mazeMap[0].length;
                
                // Calculate dynamic tile size to fit window
                int ts = Math.min(760 / cols, 560 / rows);
                int mapW = cols * ts;
                int mapH = rows * ts;
                int offsetX = (getWidth() - mapW) / 2;
                int offsetY = (getHeight() - mapH) / 2;
                
                g2.translate(offsetX, offsetY);

                // Draw Wooden Frame Border
                g2.setColor(new Color(80, 50, 40));
                g2.setStroke(new BasicStroke(10));
                g2.drawRect(-5, -5, mapW + 10, mapH + 10);
                g2.setColor(new Color(110, 70, 50));
                g2.setStroke(new BasicStroke(4));
                g2.drawRect(-7, -7, mapW + 14, mapH + 14);

                // Layer 1: Floor & Walls
                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < cols; c++) {
                        int x = c * ts, y = r * ts;
                        if (mazeMap[r][c] == 1) {
                             if (wallImg != null) g2.drawImage(wallImg, x, y, ts, ts, null);
                             else { g2.setColor(new Color(34, 100, 34)); g2.fillRect(x, y, ts, ts); }
                        } else {
                             if (floorImg != null) g2.drawImage(floorImg, x, y, ts, ts, null);
                             else { g2.setColor(new Color(200, 200, 190)); g2.fillRect(x, y, ts, ts); }
                             
                             if (GameConfig.isTrap(mazeMap, r, c, level)) {
                                 if (trapImg != null) g2.drawImage(trapImg, x + ts/8, y + ts/8, ts - ts/4, ts - ts/4, null);
                             }
                        }
                        
                        if (r == exitRow && c == exitCol) {
                            BufferedImage door = (cheeseCollected >= cheeseRequired) ? doorOpenImg : doorClosedImg;
                            if (door != null) g2.drawImage(door, x, y, ts, ts, null);
                        }
                    }
                }

                // Layer 3: Items
                if (cheeseRow != -1) {
                    int itemSize = (int)(ts * 0.7);
                    int x = cheeseCol * ts + (ts - itemSize)/2, y = cheeseRow * ts + (ts - itemSize)/2;
                    if (cheeseImg != null) g2.drawImage(cheeseImg, x, y, itemSize, itemSize, null);
                }
                
                // Layer 4: Characters
                int charSize = (int)(ts * 0.85);
                int mx = mouseCol * ts + (ts - charSize)/2, my = mouseRow * ts + (ts - charSize)/2;

                // Pick correct directional sprite — no rotation needed!
                BufferedImage mouseSprite = mouseDirFrames[mouseDirection][animationFrame % 2];
                
                if (mouseSprite != null) {
                    Graphics2D g2dSprite = (Graphics2D) g2.create();
                    if (mouseHitFlash > 0) {
                        mouseHitFlash--;
                        // Shake effect
                        int shakeX = (mouseHitFlash % 2 == 0) ? 4 : -4;
                        int shakeY = (mouseHitFlash % 3 == 0) ? 4 : -4;
                        
                        g2dSprite.drawImage(mouseSprite, mx + shakeX, my + shakeY, charSize, charSize, null);
                        
                        // Flashing red tint
                        if (mouseHitFlash % 4 < 2) {
                            g2dSprite.setColor(new Color(255, 0, 0, 120));
                            g2dSprite.fillOval(mx + shakeX + 4, my + shakeY + 4, charSize - 8, charSize - 8);
                        }
                    } else {
                        g2dSprite.drawImage(mouseSprite, mx, my, charSize, charSize, null);
                    }
                    g2dSprite.dispose();
                }

                for (CatNPC cat : cats) {
                    int cx = cat.col * ts + (ts - charSize)/2, cy = cat.row * ts + (ts - charSize)/2;
                    // Pick directional cat sprite based on cat.direction
                    BufferedImage[][] catFrameSet = (cat.state == CatNPC.State.CHASE) ? catChaseFrames : catPatrolFrames;
                    BufferedImage cs = catFrameSet[cat.direction][animationFrame % 2];
                    if (cs != null) {
                        g2.drawImage(cs, cx, cy, charSize, charSize, null);
                    }
                }
            }
        };

        boardPanel.setPreferredSize(new Dimension(8 * 85, 6 * 85));
        boardPanel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        mainContainer.add(boardPanel, gbc);

        // Controls
        InputMap im = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = this.getActionMap();
        im.put(KeyStroke.getKeyStroke("UP"), "UP");
        im.put(KeyStroke.getKeyStroke("DOWN"), "DOWN");
        im.put(KeyStroke.getKeyStroke("LEFT"), "LEFT");
        im.put(KeyStroke.getKeyStroke("RIGHT"), "RIGHT");
        am.put("UP", createMoveAction(-1, 0, boardPanel));
        am.put("DOWN", createMoveAction(1, 0, boardPanel));
        am.put("LEFT", createMoveAction(0, -1, boardPanel));
        am.put("RIGHT", createMoveAction(0, 1, boardPanel));

        initCatAI(boardPanel);
        initAnimation(boardPanel);
    }
    
    private void spawnCheese() {
        int index = GameConfig.getRandomFloorIndex(mazeMap);
        cheeseRow = index / mazeMap[0].length;
        cheeseCol = index % mazeMap[0].length;
    }

    private AbstractAction createMoveAction(int dRow, int dCol, JPanel board) {
        return new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (isGameOver || mouseStunned || !canMouseMove) return;
                
                if (dCol == 1) mouseDirection = 0;
                else if (dCol == -1) mouseDirection = 1;
                else if (dRow == -1) mouseDirection = 2;
                else if (dRow == 1) mouseDirection = 3;
                
                int nr = mouseRow + dRow, nc = mouseCol + dCol;
                if (!GameConfig.isWall(mazeMap, nr, nc)) {
                    mouseRow = nr; mouseCol = nc;
                    if (GameConfig.isTrap(mazeMap, mouseRow, mouseCol, level)) takeDamage("Trap!", board);
                    if (mouseRow == cheeseRow && mouseCol == cheeseCol) {
                        cheeseCollected++;
                        statsLabel.setText("CHEESE: " + cheeseCollected + "/" + cheeseRequired);
                        if (cheeseCollected < cheeseRequired) spawnCheese();
                        else { cheeseRow = -1; cheeseCol = -1; }
                    }
                    if (mouseRow == exitRow && mouseCol == exitCol && cheeseCollected >= cheeseRequired) {
                        catAiTimer.stop();
                        animationTimer.stop();
                        if (level == Main.maxUnlockedLevel) Main.maxUnlockedLevel++;
                        JOptionPane.showMessageDialog(GameRoom.this, "You Win!");
                        parentFrame.showLevelSelection();
                    }
                    checkCollisions(board);
                    board.repaint();
                    // Start cooldown
                    canMouseMove = false;
                    if (mouseMoveTimer != null) mouseMoveTimer.stop();
                    mouseMoveTimer = new Timer(MOUSE_MOVE_DELAY, ev -> { canMouseMove = true; });
                    mouseMoveTimer.setRepeats(false);
                    mouseMoveTimer.start();
                }
            }
        };
    }

    private void updateHearts() {
        heartPanel.removeAll();
        try {
            BufferedImage fullImg = ImageIO.read(new File("mainplay/heart_full.png"));
            BufferedImage emptyImg = ImageIO.read(new File("mainplay/heart_empty.png"));
            
            ImageIcon fullIcon = new ImageIcon(fullImg.getScaledInstance(20, 20, Image.SCALE_SMOOTH));
            ImageIcon emptyIcon = new ImageIcon(emptyImg.getScaledInstance(20, 20, Image.SCALE_SMOOTH));
            
            for (int i = 0; i < 4; i++) {
                if (i < lives) heartPanel.add(new JLabel(fullIcon));
                else heartPanel.add(new JLabel(emptyIcon));
            }
        } catch (Exception e) {
            heartPanel.add(new JLabel("LIVES: " + lives));
        }
        heartPanel.revalidate();
    }

    private void initCatAI(JPanel board) {
        // Patrol speed: 500ms. Chase speed: 220ms, reduced by 10ms per level (min 120ms)
        int chaseDelay  = Math.max(120, 220 - (level * 10));
        int patrolDelay = 500;

        // We use a single timer but track state to switch delays dynamically
        catAiTimer = new Timer(patrolDelay, null);
        catAiTimer.addActionListener(e -> {
            if (isGameOver) return;
            for (CatNPC c : cats) c.update(mouseRow, mouseCol, mazeMap);
            checkCollisions(board);
            board.repaint();

            // Adjust timer delay based on any cat's current state
            boolean anyChasing = cats.stream().anyMatch(c -> c.state == CatNPC.State.CHASE);
            int desiredDelay = anyChasing ? chaseDelay : patrolDelay;
            if (catAiTimer.getDelay() != desiredDelay) {
                catAiTimer.setDelay(desiredDelay);
            }
        });
        catAiTimer.start();
    }

    private void checkCollisions(JPanel board) {
        for (CatNPC c : cats) if (c.row == mouseRow && c.col == mouseCol) takeDamage("Caught!", board);
    }

    private void takeDamage(String reason, JPanel board) {
        if (mouseHitFlash > 0) return; // Invulnerability period
        lives--; updateHearts();
        mouseHitFlash = 30; // Longer flicker animation for invulnerability
        if (lives <= 0) {
            catAiTimer.stop();
            animationTimer.stop();
            JOptionPane.showMessageDialog(this, "Game Over!");
            parentFrame.showMainMenu();
        } else {
            // Keep mouse position. Cats also stay in their current position.
            // The mouse has invulnerability frames (mouseHitFlash > 0) to escape.
        }
    }

    private void loadGameAssets() {
        try {
            mouseImg = makeTransparent(ImageIO.read(new File("mainplay/mouse.png")));
            mouseRunImg = makeTransparent(ImageIO.read(new File("mainplay/mouse_run.png")));
            catImg = makeTransparent(ImageIO.read(new File("mainplay/cat.png")));
            cheeseImg = makeTransparent(ImageIO.read(new File("mainplay/cheese.png")));
            trapImg = makeTransparent(ImageIO.read(new File("mainplay/trap.png")));
            doorOpenImg = makeTransparent(ImageIO.read(new File("mainplay/door_open.png")));
            doorClosedImg = makeTransparent(ImageIO.read(new File("mainplay/door_closed.png")));

            // Procedural Tiles matching the screenshot
            java.util.Random rand = new java.util.Random();
            
            // 1. Stone Floor Tile
            floorImg = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
            Graphics2D gFloor = floorImg.createGraphics();
            gFloor.setColor(new Color(190, 195, 185)); // Stone beige
            gFloor.fillRect(0, 0, 64, 64);
            gFloor.setColor(new Color(160, 165, 155));
            gFloor.drawRect(0, 0, 63, 63); // Tile borders
            // Add some cracks
            gFloor.setColor(new Color(140, 145, 135));
            for(int i=0; i<5; i++) {
                int x1 = rand.nextInt(64), y1 = rand.nextInt(64);
                gFloor.drawLine(x1, y1, x1 + rand.nextInt(10), y1 + rand.nextInt(10));
            }
            gFloor.dispose();

            // 2. Hedge Wall Tile
            wallImg = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
            Graphics2D gWall = wallImg.createGraphics();
            gWall.setColor(new Color(20, 60, 20)); // Deep green
            gWall.fillRect(0, 0, 64, 64);
            for(int i=0; i<200; i++) {
                int gr = 50 + rand.nextInt(100);
                gWall.setColor(new Color(gr/4, gr, gr/5));
                gWall.fillOval(rand.nextInt(60), rand.nextInt(60), 5, 5);
            }
            gWall.dispose();

            // Load directional mouse sprites
            String[] dirs = {"right", "left", "up", "down"};
            for (int d = 0; d < 4; d++) {
                for (int f = 1; f <= 2; f++) {
                    File spriteFile = new File("mainplay/sprites/mouse_" + dirs[d] + "_" + f + ".png");
                    if (spriteFile.exists()) {
                        mouseDirFrames[d][f - 1] = ImageIO.read(spriteFile);
                    } else {
                        // Fallback: use old mouse image if new sprites missing
                        mouseDirFrames[d][f - 1] = mouseImg;
                    }
                }
            }

            // Load directional cat sprites (patrol and chase)
            for (int d = 0; d < 4; d++) {
                for (int f = 1; f <= 2; f++) {
                    File patrolFile = new File("mainplay/sprites/cat_" + dirs[d] + "_" + f + ".png");
                    File chaseFile  = new File("mainplay/sprites/cat_chase_" + dirs[d] + "_" + f + ".png");
                    catPatrolFrames[d][f - 1] = patrolFile.exists() ? ImageIO.read(patrolFile) : catImg;
                    catChaseFrames[d][f - 1]  = chaseFile.exists()  ? ImageIO.read(chaseFile)  : catImg;
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading assets: " + e.getMessage());
        }
    }

    private BufferedImage makeTransparent(BufferedImage img) {
        if (img == null) return null;
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage res = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        // Use the top-left pixel as the transparent key
        int keyRGB = img.getRGB(0, 0);
        int kr = (keyRGB >> 16) & 0xFF;
        int kg = (keyRGB >> 8) & 0xFF;
        int kb = keyRGB & 0xFF;
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                
                int diff = Math.abs(r - kr) + Math.abs(g - kg) + Math.abs(b - kb);
                if (diff < 60) {
                    res.setRGB(x, y, 0x00FFFFFF);
                } else {
                    res.setRGB(x, y, rgb);
                }
            }
        }
        return res;
    }

    private void initAnimation(JPanel board) {
        animationTimer = new Timer(150, e -> { animationFrame++; board.repaint(); });
        animationTimer.start();
    }
}
