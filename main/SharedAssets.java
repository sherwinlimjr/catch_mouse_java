package main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class SharedAssets {
    private static final Map<String, BufferedImage> images = new HashMap<>();
    private static final Map<String, ImageIcon> icons = new HashMap<>();

    public static void preload() {
        // Run preloading in a separate thread to not block the initial window
        new Thread(() -> {
            getImage("main/menu_bg.png");
            getIcon("mainplay/lock.png", 32, 32);
        }).start();
    }

    public static BufferedImage getImage(String path) {
        if (!images.containsKey(path)) {
            try {
                File file = new File(path);
                if (file.exists()) {
                    images.put(path, ImageIO.read(file));
                }
            } catch (Exception e) {
                System.err.println("Failed to load image: " + path);
            }
        }
        return images.get(path);
    }

    public static ImageIcon getIcon(String path, int w, int h) {
        String key = path + "_" + w + "x" + h;
        if (!icons.containsKey(key)) {
            BufferedImage img = getImage(path);
            if (img != null) {
                icons.put(key, new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH)));
            }
        }
        return icons.get(key);
    }
}
