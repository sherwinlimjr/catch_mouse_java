package mainplay;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class GameConfig {
    public static final int TILE_SIZE = 48;
    
    // Base size for Level 1 is 15x15
    // Base size for Level 1 is 11x11 (was 15x15) to make it less tight
    public static int getRows(int level) { return 9 + (level * 2); }
    public static int getCols(int level) { return 9 + (level * 2); }
    
    public static int[][] generateMaze(int level) {
        int rows = getRows(level);
        int cols = getCols(level);
        int[][] maze = new int[rows][cols];
        
        // Initialize with walls (1)
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                maze[r][c] = 1;
            }
        }
        
        // Recursive Backtracker
        recursiveBacktrack(maze, 1, 1, rows, cols);
        
        // Ensure entrance and exit
        maze[1][1] = 0; // Mouse start
        maze[rows-2][cols-2] = 0; // Exit target
        
        // Remove some walls to divide them and create more open paths
        int wallsToRemove = (rows * cols) / 5; // Remove ~20% of tiles for a more open feel
        java.util.Random rand = new java.util.Random();
        for (int i = 0; i < wallsToRemove; i++) {
            int r = 1 + rand.nextInt(rows - 2);
            int c = 1 + rand.nextInt(cols - 2);
            maze[r][c] = 0;
        }
        
        return maze;
    }
    
    private static void recursiveBacktrack(int[][] maze, int r, int c, int rows, int cols) {
        maze[r][c] = 0;
        
        Integer[] dirs = {0, 1, 2, 3}; // 0:U, 1:R, 2:D, 3:L
        java.util.Collections.shuffle(java.util.Arrays.asList(dirs));
        
        for (int dir : dirs) {
            int dr = 0, dc = 0;
            if (dir == 0) dr = -2;
            else if (dir == 1) dc = 2;
            else if (dir == 2) dr = 2;
            else if (dir == 3) dc = -2;
            
            int nr = r + dr, nc = c + dc;
            if (nr > 0 && nr < rows - 1 && nc > 0 && nc < cols - 1 && maze[nr][nc] == 1) {
                maze[r + dr/2][c + dc/2] = 0;
                recursiveBacktrack(maze, nr, nc, rows, cols);
            }
        }
    }

    public static boolean isWall(int[][] maze, int r, int c) {
        if (r < 0 || r >= maze.length || c < 0 || c >= maze[0].length) return true;
        return maze[r][c] == 1;
    }

    public static boolean isTrap(int[][] maze, int r, int c, int level) {
        // Procedural traps for complexity
        if (level < 2) return false;
        // Reduce trap density (e.g. modulo 35 instead of 17)
        return ((r * 31 + c) % 35 == 0) && maze[r][c] == 0 && (r != 1 || c != 1) && (r != maze.length - 2 || c != maze[0].length - 2);
    }

    public static boolean isMilk(int[][] maze, int r, int c, int level) {
        if (level < 3) return false;
        return ((r * 13 + c) % 43 == 0) && maze[r][c] == 0 && (r != 1 || c != 1) && (r != maze.length - 2 || c != maze[0].length - 2);
    }

    public static int getRandomFloorIndex(int[][] maze) {
        java.util.Random rand = new java.util.Random();
        int r, c;
        do {
            r = rand.nextInt(maze.length);
            c = rand.nextInt(maze[0].length);
        } while (maze[r][c] != 0);
        return r * maze[0].length + c;
    }
}
