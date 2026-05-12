package mainplay;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class GameConfig {
    public static final int ROWS = 6;
    public static final int COLS = 8;
    
    // 0 = Path, 1 = Wall
    public static final int[][] MAZE = {
        {0, 0, 0, 0, 1, 0, 0, 0},
        {0, 1, 1, 0, 1, 0, 1, 0},
        {0, 0, 1, 0, 0, 0, 1, 0},
        {1, 0, 1, 1, 1, 1, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 1, 1, 1, 1, 1, 0}
    };

    public static ImageIcon getTileIcon(int type, int width, int height) {
        String path = (type == 1) ? "resources/tiles/wall.png" : "resources/tiles/floor.png";
        try {
            BufferedImage img = ImageIO.read(new File(path));
            Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (IOException e) {
            System.err.println("Could not load tile: " + path);
            return null;
        }
    }
    
    public static boolean isWall(int index) {
        int r = index / COLS;
        int c = index % COLS;
        if (r < 0 || r >= ROWS || c < 0 || c >= COLS) return true;
        return MAZE[r][c] == 1;
    }
}
