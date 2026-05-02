package main;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

/**
 * A custom component that draws a stylized, cartoonish "CATCH THE MOUSE" logo
 * in the style of classic Tom and Jerry title cards.
 */
public class CartoonLogo extends JComponent {

    public CartoonLogo() {
        setPreferredSize(new Dimension(600, 230));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int w = getWidth();
        int h = getHeight();
        int cx = w / 2;
        int cy = h / 2;

        // =========================================================
        // TITLE TEXT  (drawn first so characters render on top)
        // =========================================================
        Font titleFont = new Font("Arial Black", Font.BOLD, 46);
        g2.setFont(titleFont);
        FontMetrics fm = g2.getFontMetrics();
        String line1 = "CATCH THE";
        String line2 = "MOUSE";
        int l1w = fm.stringWidth(line1);
        int l2w = fm.stringWidth(line2);
        int l1x = cx - l1w / 2;
        int l1y = cy - 18;
        int l2x = cx - l2w / 2;
        int l2y = cy + fm.getHeight() - 10;

        // Drop shadow
        g2.setColor(new Color(20, 0, 0, 160));
        g2.drawString(line1, l1x + 4, l1y + 4);
        g2.drawString(line2, l2x + 4, l2y + 4);

        // Thick red outline
        g2.setColor(new Color(180, 10, 10));
        int out = 3;
        for (int dx = -out; dx <= out; dx++) {
            for (int dy = -out; dy <= out; dy++) {
                if (dx * dx + dy * dy <= out * out + 1) {
                    g2.drawString(line1, l1x + dx, l1y + dy);
                    g2.drawString(line2, l2x + dx, l2y + dy);
                }
            }
        }

        // Yellow-orange gradient fill
        GradientPaint gp1 = new GradientPaint(0, l1y - fm.getAscent(), new Color(255, 240, 0), 0, l1y, new Color(255, 150, 0));
        g2.setPaint(gp1);
        g2.drawString(line1, l1x, l1y);

        GradientPaint gp2 = new GradientPaint(0, l2y - fm.getAscent(), new Color(255, 240, 0), 0, l2y, new Color(255, 150, 0));
        g2.setPaint(gp2);
        g2.drawString(line2, l2x, l2y);

        // Speed lines to the left of the text
        g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(255, 255, 255, 180));
        int lx = l1x - 12;
        g2.drawLine(lx, l1y - 28, lx - 38, l1y - 28);
        g2.drawLine(lx + 4, l1y - 16, lx - 28, l1y - 16);
        g2.drawLine(lx + 8, l1y - 4,  lx - 18, l1y - 4);

        // =========================================================
        // CAT  (left side)
        // =========================================================
        int catX = 30;   // left edge of cat area
        int catY = cy - 60;

        drawCat(g2, catX, catY);

        // =========================================================
        // MOUSE (right side)
        // =========================================================
        int mouseX = w - 115;
        int mouseY = cy - 40;

        drawMouse(g2, mouseX, mouseY);

        g2.dispose();
    }

    // ------------------------------------------------------------------
    //  Draw a cartoon cat head facing right
    // ------------------------------------------------------------------
    private void drawCat(Graphics2D g2, int x, int y) {
        // ---- Body ----
        g2.setColor(new Color(70, 80, 100));
        g2.fillOval(x + 10, y + 55, 70, 55);  // torso

        // ---- Tail ----
        Path2D tail = new Path2D.Double();
        tail.moveTo(x + 15, y + 95);
        tail.curveTo(x - 20, y + 110, x - 30, y + 70, x - 5, y + 50);
        tail.curveTo(x + 5,  y + 45,  x + 10, y + 50, x + 5, y + 55);
        g2.setColor(new Color(70, 80, 100));
        g2.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(tail);
        // tail tip (cream)
        g2.setColor(new Color(240, 220, 180));
        g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(tail);

        // ---- Head ----
        Color catColor = new Color(75, 85, 105);
        Color catLight = new Color(100, 110, 130);

        // Head base
        g2.setStroke(new BasicStroke(1));
        g2.setColor(catColor);
        g2.fillOval(x + 5, y + 5, 82, 75);

        // ---- Ears ----
        // Left ear outer
        Polygon leftEar = new Polygon(
            new int[]{x + 12, x + 5,  x + 32},
            new int[]{y + 20, y - 15, y + 10}, 3);
        g2.setColor(catColor);
        g2.fillPolygon(leftEar);
        // Left ear inner
        Polygon leftEarIn = new Polygon(
            new int[]{x + 14, x + 10, x + 28},
            new int[]{y + 18, y - 7,  y + 13}, 3);
        g2.setColor(new Color(200, 100, 120));
        g2.fillPolygon(leftEarIn);

        // Right ear outer
        Polygon rightEar = new Polygon(
            new int[]{x + 58, x + 80, x + 68},
            new int[]{y + 10, y - 15, y + 20}, 3);
        g2.setColor(catColor);
        g2.fillPolygon(rightEar);
        // Right ear inner
        Polygon rightEarIn = new Polygon(
            new int[]{x + 60, x + 75, x + 67},
            new int[]{y + 13, y - 7,  y + 18}, 3);
        g2.setColor(new Color(200, 100, 120));
        g2.fillPolygon(rightEarIn);

        // ---- Muzzle ----
        g2.setColor(new Color(210, 190, 165));
        g2.fillOval(x + 28, y + 40, 42, 28);

        // ---- Eyes ----
        // White sclera
        g2.setColor(Color.WHITE);
        g2.fillOval(x + 18, y + 22, 22, 18);
        g2.fillOval(x + 50, y + 22, 22, 18);
        // Pupils (angry slit)
        g2.setColor(new Color(30, 60, 30));
        g2.fillOval(x + 25, y + 24, 10, 14);
        g2.fillOval(x + 57, y + 24, 10, 14);
        // Shine
        g2.setColor(Color.WHITE);
        g2.fillOval(x + 27, y + 26, 4, 4);
        g2.fillOval(x + 59, y + 26, 4, 4);
        // Angry eyebrows
        g2.setColor(catColor);
        g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x + 17, y + 18, x + 39, y + 23);
        g2.drawLine(x + 51, y + 23, x + 73, y + 18);

        // ---- Nose ----
        int[] noseX = {x + 44, x + 48, x + 40};
        int[] noseY = {y + 44, y + 50, y + 50};
        g2.setColor(new Color(220, 100, 130));
        g2.setStroke(new BasicStroke(1));
        g2.fillPolygon(noseX, noseY, 3);

        // ---- Whiskers ----
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(1.5f));
        // Left whiskers
        g2.drawLine(x + 38, y + 50, x + 5,  y + 45);
        g2.drawLine(x + 38, y + 53, x + 5,  y + 55);
        g2.drawLine(x + 38, y + 56, x + 5,  y + 65);
        // Right whiskers
        g2.drawLine(x + 52, y + 50, x + 88, y + 45);
        g2.drawLine(x + 52, y + 53, x + 88, y + 55);
        g2.drawLine(x + 52, y + 56, x + 88, y + 65);

        // ---- Mouth ----
        g2.setColor(new Color(160, 80, 90));
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawArc(x + 32, y + 52, 12, 8, 200, -160);
        g2.drawArc(x + 46, y + 52, 12, 8, 200, -160);
    }

    // ------------------------------------------------------------------
    //  Draw a cartoon mouse head facing left (scared/running look)
    // ------------------------------------------------------------------
    private void drawMouse(Graphics2D g2, int x, int y) {
        Color mouseColor = new Color(160, 120, 100);
        Color mouseLight = new Color(200, 170, 150);
        Color earInner  = new Color(210, 140, 150);

        // ---- Body ----
        g2.setColor(mouseColor);
        g2.setStroke(new BasicStroke(1));
        g2.fillOval(x + 10, y + 55, 60, 45); // torso

        // ---- Tail ----
        Path2D tail = new Path2D.Double();
        tail.moveTo(x + 65, y + 90);
        tail.curveTo(x + 90, y + 100, x + 100, y + 75, x + 85, y + 60);
        g2.setColor(new Color(180, 140, 120));
        g2.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(tail);

        // ---- Big Round Ears ----
        // Left ear (further)
        g2.setStroke(new BasicStroke(1));
        g2.setColor(mouseColor);
        g2.fillOval(x + 2, y - 5, 38, 38);
        g2.setColor(earInner);
        g2.fillOval(x + 9, y + 2, 24, 24);

        // Right ear (near)
        g2.setColor(mouseColor);
        g2.fillOval(x + 40, y - 10, 42, 42);
        g2.setColor(earInner);
        g2.fillOval(x + 48, y - 3, 27, 27);

        // ---- Head ----
        g2.setColor(mouseColor);
        g2.fillOval(x + 8, y + 12, 75, 68);

        // ---- Muzzle ----
        g2.setColor(mouseLight);
        g2.fillOval(x + 18, y + 44, 48, 30);

        // ---- Eyes (wide, scared) ----
        g2.setColor(Color.WHITE);
        g2.fillOval(x + 22, y + 22, 22, 20);
        g2.fillOval(x + 50, y + 18, 22, 20);
        // Pupils
        g2.setColor(new Color(20, 20, 60));
        g2.fillOval(x + 29, y + 25, 10, 14);
        g2.fillOval(x + 57, y + 21, 10, 14);
        // Shine
        g2.setColor(Color.WHITE);
        g2.fillOval(x + 31, y + 27, 4, 4);
        g2.fillOval(x + 59, y + 23, 4, 4);
        // Raised eyebrows (scared)
        g2.setColor(new Color(120, 80, 60));
        g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawArc(x + 20, y + 14, 24, 12, 20, 140);
        g2.drawArc(x + 48, y + 10, 24, 12, 20, 140);

        // ---- Nose ----
        g2.setColor(new Color(220, 100, 130));
        g2.setStroke(new BasicStroke(1));
        g2.fillOval(x + 37, y + 46, 14, 10);
        // Nostrils
        g2.setColor(new Color(180, 60, 80));
        g2.fillOval(x + 39, y + 48, 4, 4);
        g2.fillOval(x + 46, y + 48, 4, 4);

        // ---- Whiskers ----
        g2.setColor(new Color(220, 200, 180));
        g2.setStroke(new BasicStroke(1.2f));
        // Left whiskers
        g2.drawLine(x + 36, y + 52, x + 5,  y + 48);
        g2.drawLine(x + 36, y + 55, x + 5,  y + 57);
        g2.drawLine(x + 36, y + 58, x + 5,  y + 66);
        // Right whiskers
        g2.drawLine(x + 56, y + 52, x + 88, y + 48);
        g2.drawLine(x + 56, y + 55, x + 88, y + 57);
        g2.drawLine(x + 56, y + 58, x + 88, y + 66);

        // ---- Open mouth (scared grin) ----
        g2.setColor(new Color(160, 80, 90));
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawArc(x + 28, y + 58, 36, 16, 200, -220);
        // Teeth
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(1));
        g2.fillRect(x + 36, y + 62, 8, 7);
        g2.fillRect(x + 47, y + 62, 8, 7);
        g2.setColor(new Color(200, 200, 200));
        g2.drawLine(x + 40, y + 62, x + 40, y + 69);
        g2.drawLine(x + 51, y + 62, x + 51, y + 69);

        // ---- Sweat drop (scared detail) ----
        g2.setColor(new Color(100, 200, 255, 200));
        g2.setStroke(new BasicStroke(1));
        int sdx = x + 82, sdy = y + 15;
        Path2D drop = new Path2D.Double();
        drop.moveTo(sdx, sdy - 12);
        drop.curveTo(sdx - 8, sdy - 4, sdx - 8, sdy + 6, sdx, sdy + 8);
        drop.curveTo(sdx + 8, sdy + 6, sdx + 8, sdy - 4, sdx, sdy - 12);
        g2.fill(drop);
    }
}
