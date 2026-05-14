import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.Random;

public class AssetGenerator {
    public static void main(String[] args) throws Exception {
        Random rand = new Random();
        
        // 1. Stone Floor Tile (64x64)
        BufferedImage floor = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = floor.createGraphics();
        g.setColor(new Color(180, 185, 170)); // Slightly greenish beige stone
        g.fillRect(0, 0, 64, 64);
        
        // Add subtle noise/texture
        for (int i = 0; i < 200; i++) {
            int c = 160 + rand.nextInt(40);
            g.setColor(new Color(c, c - 5, c - 10));
            g.fillRect(rand.nextInt(64), rand.nextInt(64), 2, 2);
        }
        
        // Add stone borders/cracks
        g.setColor(new Color(140, 145, 130));
        g.drawRect(0, 0, 63, 63);
        g.drawLine(0, 32, 64, 32);
        g.drawLine(32, 0, 32, 64);
        
        g.dispose();
        ImageIO.write(floor, "png", new File("resources/tiles/floor.png"));
        System.out.println("Generated floor.png");

        // 2. Hedge Wall Tile (64x64)
        BufferedImage wall = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
        g = wall.createGraphics();
        g.setColor(new Color(20, 80, 20)); // Deep forest green
        g.fillRect(0, 0, 64, 64);
        
        // Add leaves texture
        for (int i = 0; i < 300; i++) {
            int gr = 60 + rand.nextInt(100);
            g.setColor(new Color(gr / 2, gr, gr / 3));
            g.fillOval(rand.nextInt(60), rand.nextInt(60), 4 + rand.nextInt(4), 4 + rand.nextInt(4));
        }
        
        // Add darker shadows for depth
        g.setColor(new Color(10, 40, 10, 100));
        for (int i = 0; i < 50; i++) {
            g.fillOval(rand.nextInt(60), rand.nextInt(60), 8, 8);
        }
        
        g.dispose();
        ImageIO.write(wall, "png", new File("resources/tiles/wall.png"));
        System.out.println("Generated wall.png");

        // 3. Hearts (32x32)
        // Full Heart
        BufferedImage heartFull = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        g = heartFull.createGraphics();
        g.setColor(new Color(200, 30, 30));
        int[] hx = {16, 28, 28, 16, 4, 4};
        int[] hy = {28, 12, 8, 12, 8, 12};
        g.fillOval(4, 4, 14, 14);
        g.fillOval(14, 4, 14, 14);
        int[] triX = {4, 16, 28};
        int[] triY = {14, 28, 14};
        g.fillPolygon(triX, triY, 3);
        g.dispose();
        ImageIO.write(heartFull, "png", new File("mainplay/heart_full.png"));
        
        // Empty Heart
        BufferedImage heartEmpty = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        g = heartEmpty.createGraphics();
        g.setStroke(new BasicStroke(3));
        g.setColor(new Color(60, 30, 20));
        g.drawOval(4, 4, 14, 14);
        g.drawOval(14, 4, 14, 14);
        g.drawPolygon(triX, triY, 3);
        g.dispose();
        ImageIO.write(heartEmpty, "png", new File("mainplay/heart_empty.png"));
        System.out.println("Generated hearts");
    }
}
