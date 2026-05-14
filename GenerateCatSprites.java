import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class GenerateCatSprites {

    static final int SIZE = 64;
    // Cat colour palette
    static final Color FUR        = new Color(90, 100, 120);   // Blue-grey cat
    static final Color FUR_DARK   = new Color(55,  65,  80);
    static final Color FUR_LIGHT  = new Color(120, 130, 150);
    static final Color EAR_INNER  = new Color(200, 110, 130);
    static final Color BELLY      = new Color(220, 210, 195);
    static final Color NOSE       = new Color(220, 90, 110);
    static final Color EYE_GREEN  = new Color(30, 160,  60);
    static final Color PUPIL      = new Color(10,  10,  10);
    static final Color WHITE      = Color.WHITE;
    static final Color WHISKER    = new Color(230, 230, 220);
    static final Color TAIL       = new Color(80, 90, 110);

    public static void main(String[] args) throws Exception {
        new File("mainplay/sprites").mkdirs();

        // Patrol (normal walk) frames
        saveFrame("mainplay/sprites/cat_right_1.png", drawRight(false, false));
        saveFrame("mainplay/sprites/cat_right_2.png", drawRight(true,  false));
        saveFrame("mainplay/sprites/cat_left_1.png",  drawLeft(false, false));
        saveFrame("mainplay/sprites/cat_left_2.png",  drawLeft(true,  false));
        saveFrame("mainplay/sprites/cat_up_1.png",    drawUp(false));
        saveFrame("mainplay/sprites/cat_up_2.png",    drawUp(true));
        saveFrame("mainplay/sprites/cat_down_1.png",  drawDown(false));
        saveFrame("mainplay/sprites/cat_down_2.png",  drawDown(true));

        // Chase (angry run) frames — same directions but with different expression
        saveFrame("mainplay/sprites/cat_chase_right_1.png", drawRight(false, true));
        saveFrame("mainplay/sprites/cat_chase_right_2.png", drawRight(true,  true));
        saveFrame("mainplay/sprites/cat_chase_left_1.png",  drawLeft(false,  true));
        saveFrame("mainplay/sprites/cat_chase_left_2.png",  drawLeft(true,   true));
        saveFrame("mainplay/sprites/cat_chase_up_1.png",    drawUp(false));
        saveFrame("mainplay/sprites/cat_chase_up_2.png",    drawUp(true));
        saveFrame("mainplay/sprites/cat_chase_down_1.png",  drawDown(false));
        saveFrame("mainplay/sprites/cat_chase_down_2.png",  drawDown(true));

        System.out.println("All 16 cat sprites generated successfully!");
    }

    static void saveFrame(String path, BufferedImage img) throws Exception {
        ImageIO.write(img, "png", new File(path));
        System.out.println("Saved: " + path);
    }

    static BufferedImage newCanvas() {
        BufferedImage img = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(0, 0, 0, 0));
        g.fillRect(0, 0, SIZE, SIZE);
        g.dispose();
        return img;
    }

    static Graphics2D makeG(BufferedImage img) {
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        return g;
    }

    // ─── RIGHT FACING ────────────────────────────────────────────────────────
    static BufferedImage drawRight(boolean frame2, boolean chasing) {
        BufferedImage img = newCanvas();
        Graphics2D g = makeG(img);

        // Tail curling up/behind
        g.setColor(TAIL);
        g.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D tail = new Path2D.Float();
        if (frame2) {
            tail.moveTo(10, 42); tail.curveTo(2, 32, 2, 20, 10, 12);
        } else {
            tail.moveTo(10, 44); tail.curveTo(0, 30, 4, 16, 12, 10);
        }
        g.draw(tail);

        // Body
        g.setColor(FUR);
        g.fillOval(10, 26, 36, 26);

        // Belly patch
        g.setColor(BELLY);
        g.fillOval(18, 34, 18, 14);

        // Head
        g.setColor(FUR);
        g.fillOval(36, 14, 26, 24);

        // Ears (pointed)
        // Left ear
        int[] lEx = {38, 34, 44}, lEy = {20, 6, 8};
        g.setColor(FUR);
        g.fillPolygon(lEx, lEy, 3);
        g.setColor(EAR_INNER);
        int[] lEiX = {39, 36, 43}, lEiY = {19, 10, 11};
        g.fillPolygon(lEiX, lEiY, 3);

        // Right ear
        int[] rEx = {54, 58, 62}, rEy = {17, 4, 16};
        g.setColor(FUR);
        g.fillPolygon(rEx, rEy, 3);
        g.setColor(EAR_INNER);
        int[] rEiX = {55, 58, 61}, rEiY = {16, 8, 15};
        g.fillPolygon(rEiX, rEiY, 3);

        // Muzzle / snout
        g.setColor(BELLY);
        g.fillOval(52, 26, 12, 9);

        // Nose
        g.setColor(NOSE);
        g.fillOval(58, 28, 5, 4);

        // Eyes — green with black slit pupil
        g.setColor(EYE_GREEN);
        g.fillOval(42, 18, 8, 7);
        g.fillOval(52, 17, 8, 7);
        g.setColor(PUPIL);
        g.fillOval(45, 19, 3, 5);
        g.fillOval(55, 18, 3, 5);
        g.setColor(WHITE);
        g.fillOval(44, 19, 2, 2);
        g.fillOval(54, 18, 2, 2);

        // Angry eyebrows if chasing
        if (chasing) {
            g.setColor(FUR_DARK);
            g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(42, 17, 49, 20);
            g.drawLine(52, 16, 59, 19);
        }

        // Whiskers
        g.setColor(WHISKER);
        g.setStroke(new BasicStroke(1));
        g.drawLine(52, 30, 38, 28); g.drawLine(52, 32, 38, 34);
        g.drawLine(62, 30, 76, 28); g.drawLine(62, 32, 76, 34);

        // Legs (walking animation)
        g.setColor(FUR_DARK);
        int frontLegY = frame2 ? 48 : 46;
        int backLegY  = frame2 ? 46 : 48;
        g.fillRoundRect(40, frontLegY, 8, 10, 4, 4);
        g.fillRoundRect(18, backLegY,  8, 10, 4, 4);

        g.dispose();
        return img;
    }

    // ─── LEFT FACING (mirror of right) ───────────────────────────────────────
    static BufferedImage drawLeft(boolean frame2, boolean chasing) {
        BufferedImage right = drawRight(frame2, chasing);
        BufferedImage img = newCanvas();
        Graphics2D g = makeG(img);
        g.drawImage(right, SIZE, 0, -SIZE, SIZE, null);
        g.dispose();
        return img;
    }

    // ─── DOWN FACING (front view) ────────────────────────────────────────────
    static BufferedImage drawDown(boolean frame2) {
        BufferedImage img = newCanvas();
        Graphics2D g = makeG(img);

        // Body
        g.setColor(FUR);
        g.fillOval(14, 26, 36, 30);
        g.setColor(BELLY);
        g.fillOval(21, 34, 22, 18);

        // Ears — pointy triangles at top corners
        // Left ear
        int[] lEx = {14, 10, 24}, lEy = {22, 4, 12};
        g.setColor(FUR);
        g.fillPolygon(lEx, lEy, 3);
        g.setColor(EAR_INNER);
        int[] lEiX = {15, 12, 22}, lEiY = {20, 9, 14};
        g.fillPolygon(lEiX, lEiY, 3);

        // Right ear
        int[] rEx = {50, 54, 40}, rEy = {22, 4, 12};
        g.setColor(FUR);
        g.fillPolygon(rEx, rEy, 3);
        g.setColor(EAR_INNER);
        int[] rEiX = {49, 52, 42}, rEiY = {20, 9, 14};
        g.fillPolygon(rEiX, rEiY, 3);

        // Head
        g.setColor(FUR);
        g.fillOval(16, 10, 32, 28);

        // Muzzle
        g.setColor(BELLY);
        g.fillOval(22, 26, 20, 12);

        // Nose
        g.setColor(NOSE);
        g.fillOval(29, 28, 6, 5);

        // Eyes
        g.setColor(EYE_GREEN);
        g.fillOval(20, 17, 9, 8);
        g.fillOval(35, 17, 9, 8);
        g.setColor(PUPIL);
        g.fillOval(23, 18, 3, 6);
        g.fillOval(38, 18, 3, 6);
        g.setColor(WHITE);
        g.fillOval(23, 18, 2, 2);
        g.fillOval(38, 18, 2, 2);

        // Whiskers
        g.setColor(WHISKER);
        g.setStroke(new BasicStroke(1));
        g.drawLine(22, 31, 6,  28); g.drawLine(22, 33, 6,  36);
        g.drawLine(42, 31, 58, 28); g.drawLine(42, 33, 58, 36);

        // Feet
        int leftX  = frame2 ? 16 : 18;
        int rightX = frame2 ? 40 : 38;
        g.setColor(FUR_DARK);
        g.fillRoundRect(leftX,  52, 10, 9, 5, 5);
        g.fillRoundRect(rightX, 52, 10, 9, 5, 5);

        // Tail peek
        g.setColor(TAIL);
        g.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawArc(19, 48, 26, 12, 0, -180);

        g.dispose();
        return img;
    }

    // ─── UP FACING (back view) ───────────────────────────────────────────────
    static BufferedImage drawUp(boolean frame2) {
        BufferedImage img = newCanvas();
        Graphics2D g = makeG(img);

        // Tail raised high
        g.setColor(TAIL);
        g.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D tail = new Path2D.Float();
        tail.moveTo(32, 20);
        if (frame2) {
            tail.curveTo(26, 8, 38, 2, 40, 10);
        } else {
            tail.curveTo(38, 6, 48, 4, 44, 14);
        }
        g.draw(tail);

        // Body
        g.setColor(FUR_DARK);
        g.fillOval(14, 28, 36, 28);

        // Dark stripe down back
        g.setColor(new Color(40, 50, 65));
        g.fillOval(24, 30, 16, 22);

        // Back of head
        g.setColor(FUR_DARK);
        g.fillOval(16, 10, 32, 26);

        // Ears pointing upward
        int[] lEx = {16, 12, 26}, lEy = {18, 2, 10};
        g.setColor(FUR_DARK);
        g.fillPolygon(lEx, lEy, 3);
        g.setColor(new Color(120, 75, 90));
        int[] lEiX = {17, 14, 24}, lEiY = {17, 7, 12};
        g.fillPolygon(lEiX, lEiY, 3);

        int[] rEx = {48, 52, 38}, rEy = {18, 2, 10};
        g.setColor(FUR_DARK);
        g.fillPolygon(rEx, rEy, 3);
        g.setColor(new Color(120, 75, 90));
        int[] rEiX = {47, 50, 40}, rEiY = {17, 7, 12};
        g.fillPolygon(rEiX, rEiY, 3);

        // Feet peeking below body
        int leftX  = frame2 ? 16 : 18;
        int rightX = frame2 ? 40 : 38;
        g.setColor(FUR_DARK);
        g.fillRoundRect(leftX,  52, 10, 9, 5, 5);
        g.fillRoundRect(rightX, 52, 10, 9, 5, 5);

        g.dispose();
        return img;
    }
}
