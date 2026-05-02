package main;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ModernButton extends JButton {
    private Color baseColor;
    private Color hoverColor;
    private Color pressedColor;

    public ModernButton(String text, Color base) {
        super(text);
        this.baseColor = base;
        this.hoverColor = base.brighter();
        this.pressedColor = base.darker();

        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, 20));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (!isEnabled()) {
            g2.setColor(new Color(100, 100, 100));
        } else if (getModel().isPressed()) {
            g2.setColor(pressedColor);
        } else if (getModel().isRollover()) {
            g2.setColor(hoverColor);
        } else {
            g2.setColor(baseColor);
        }

        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 40, 40));
        g2.setColor(new Color(255, 255, 255, 50));
        g2.setStroke(new BasicStroke(2));
        g2.draw(new RoundRectangle2D.Double(1, 1, getWidth() - 2, getHeight() - 2, 40, 40));

        g2.dispose();
        super.paintComponent(g);
    }
}
