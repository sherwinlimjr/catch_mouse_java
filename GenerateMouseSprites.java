import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.*;
import java.io.File;
import javax.imageio.ImageIO;

public class GenerateMouseSprites {

    static final int SIZE = 64;
    static final Color FUR       = new Color(190, 155, 130);
    static final Color FUR_DARK  = new Color(150, 110,  90);
    static final Color EAR_INNER = new Color(225, 145, 160);
    static final Color NOSE      = new Color(230, 80, 100);
    static final Color EYE       = new Color(20, 20, 60);
    static final Color WHITE     = Color.WHITE;
    static final Color TAIL      = new Color(210, 170, 150);
    static final Color FOOT      = new Color(220, 160, 145);
    static final int TRANSPARENT = 0x00000000;

    public static void main(String[] args) throws Exception {
        new File("mainplay/sprites").mkdirs();

        saveFrame("mainplay/sprites/mouse_right_1.png", drawRight(false));
        saveFrame("mainplay/sprites/mouse_right_2.png", drawRight(true));
        saveFrame("mainplay/sprites/mouse_left_1.png",  drawLeft(false));
        saveFrame("mainplay/sprites/mouse_left_2.png",  drawLeft(true));
        saveFrame("mainplay/sprites/mouse_up_1.png",    drawUp(false));
        saveFrame("mainplay/sprites/mouse_up_2.png",    drawUp(true));
        saveFrame("mainplay/sprites/mouse_down_1.png",  drawDown(false));
        saveFrame("mainplay/sprites/mouse_down_2.png",  drawDown(true));

        // Jump sprites
        saveFrame("mainplay/sprites/mouse_jump_right.png", drawJumpRight());
        saveFrame("mainplay/sprites/mouse_jump_left.png",  drawJumpLeft());
        saveFrame("mainplay/sprites/mouse_jump_up.png",    drawJumpUp());
        saveFrame("mainplay/sprites/mouse_jump_down.png",  drawJumpDown());

        System.out.println("All 12 mouse sprites generated successfully!");
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

    // ─── RIGHT FACING (base direction) ───────────────────────────────────────
    static BufferedImage drawRight(boolean frame2) {
        BufferedImage img = newCanvas();
        Graphics2D g = makeG(img);

        // Tail (left side)
        g.setColor(TAIL);
        g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D tail = new Path2D.Float();
        tail.moveTo(8, 38);
        tail.curveTo(4, 30, 2, 20, 8, 14);
        g.draw(tail);

        // Body
        g.setColor(FUR);
        g.fillOval(10, 24, 34, 26);

        // Head
        g.setColor(FUR);
        g.fillOval(36, 20, 24, 22);

        // Ears (top of head)
        g.setColor(FUR);
        g.fillOval(42, 10, 14, 14);
        g.setColor(EAR_INNER);
        g.fillOval(45, 13, 8, 9);

        // Snout
        g.setColor(new Color(230, 200, 185));
        g.fillOval(55, 26, 9, 7);

        // Nose
        g.setColor(NOSE);
        g.fillOval(60, 27, 5, 4);

        // Eye
        g.setColor(EYE);
        g.fillOval(49, 22, 5, 5);
        g.setColor(WHITE);
        g.fillOval(51, 22, 2, 2);

        // Legs / feet
        int legY = frame2 ? 46 : 44;
        g.setColor(FOOT);
        g.fillRoundRect(18, legY, 9, 10, 5, 5);
        g.fillRoundRect(30, legY - (frame2 ? 0 : 2), 9, 10, 5, 5);

        // Dark belly shading
        g.setColor(FUR_DARK);
        g.setStroke(new BasicStroke(1));
        g.drawOval(14, 30, 24, 16);

        g.dispose();
        return img;
    }

    // ─── LEFT FACING (mirror of right) ───────────────────────────────────────
    static BufferedImage drawLeft(boolean frame2) {
        BufferedImage right = drawRight(frame2);
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
        g.fillOval(16, 28, 32, 28);

        // Ears (two big round ears at top)
        g.setColor(FUR);
        g.fillOval(14, 8, 16, 16);
        g.fillOval(34, 8, 16, 16);
        g.setColor(EAR_INNER);
        g.fillOval(17, 11, 10, 10);
        g.fillOval(37, 11, 10, 10);

        // Head
        g.setColor(FUR);
        g.fillOval(18, 16, 28, 26);

        // Snout
        g.setColor(new Color(230, 200, 185));
        g.fillOval(24, 30, 16, 10);

        // Nose
        g.setColor(NOSE);
        g.fillOval(29, 32, 6, 5);

        // Whiskers
        g.setColor(new Color(220, 200, 180));
        g.setStroke(new BasicStroke(1));
        g.drawLine(20, 36, 8,  34);
        g.drawLine(20, 38, 8,  40);
        g.drawLine(44, 36, 56, 34);
        g.drawLine(44, 38, 56, 40);

        // Eyes
        g.setColor(EYE);
        g.fillOval(23, 22, 6, 6);
        g.fillOval(35, 22, 6, 6);
        g.setColor(WHITE);
        g.fillOval(25, 22, 2, 2);
        g.fillOval(37, 22, 2, 2);

        // Belly
        g.setColor(new Color(220, 195, 175));
        g.fillOval(22, 38, 20, 14);

        // Feet (walking animation)
        int leftLegX  = frame2 ? 16 : 18;
        int rightLegX = frame2 ? 38 : 36;
        g.setColor(FOOT);
        g.fillRoundRect(leftLegX, 52, 10, 8, 5, 5);
        g.fillRoundRect(rightLegX, 52, 10, 8, 5, 5);

        // Tail (peek from behind body)
        g.setColor(TAIL);
        g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawArc(20, 48, 24, 14, 0, -180);

        g.dispose();
        return img;
    }

    // ─── UP FACING (back view) ───────────────────────────────────────────────
    static BufferedImage drawUp(boolean frame2) {
        BufferedImage img = newCanvas();
        Graphics2D g = makeG(img);

        // Tail sticking up from top
        g.setColor(TAIL);
        g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D tail = new Path2D.Float();
        tail.moveTo(32, 16);
        tail.curveTo(28, 6, 36, 2, 38, 8);
        g.draw(tail);

        // Body
        g.setColor(FUR_DARK);
        g.fillOval(16, 28, 32, 28);

        // Back of head
        g.setColor(FUR_DARK);
        g.fillOval(18, 14, 28, 24);

        // Ears (back view — smaller, facing away)
        g.setColor(FUR_DARK);
        g.fillOval(13, 8, 15, 15);
        g.fillOval(36, 8, 15, 15);
        g.setColor(new Color(140, 90, 75));
        g.fillOval(16, 11, 9, 9);
        g.fillOval(39, 11, 9, 9);

        // Dark fur back shading
        g.setColor(new Color(130, 95, 75));
        g.fillOval(22, 28, 20, 16);

        // Feet (walking animation)
        int leftLegX  = frame2 ? 16 : 18;
        int rightLegX = frame2 ? 38 : 36;
        g.setColor(FOOT);
        g.fillRoundRect(leftLegX, 52, 10, 8, 5, 5);
        g.fillRoundRect(rightLegX, 52, 10, 8, 5, 5);

        g.dispose();
        return img;
    }
    // ─── JUMP SPRITES ────────────────────────────────────────────────────────
    static BufferedImage applyJumpEffect(BufferedImage baseImg) {
        BufferedImage img = newCanvas();
        Graphics2D g = makeG(img);
        
        // Draw shadow at the bottom
        g.setColor(new Color(0, 0, 0, 80));
        g.fillOval(16, 50, 32, 10);
        
        // Draw the mouse shifted up to simulate jumping
        g.drawImage(baseImg, 0, -12, null);
        
        g.dispose();
        return img;
    }

    static BufferedImage drawJumpRight() {
        return applyJumpEffect(drawRight(false));
    }

    static BufferedImage drawJumpLeft() {
        return applyJumpEffect(drawLeft(false));
    }

    static BufferedImage drawJumpUp() {
        return applyJumpEffect(drawUp(false));
    }

    static BufferedImage drawJumpDown() {
        return applyJumpEffect(drawDown(false));
    }
}
