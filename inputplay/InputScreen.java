package inputplay;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import main.ModernButton;
import mainplay.GameRoom;

public class InputScreen extends JFrame {
    public InputScreen() {
        setTitle("Catch Mouse - Player Input");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(new Color(33, 33, 33));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        ModernButton pvpButton = new ModernButton("PLAYER VS PLAYER", new Color(63, 81, 181));
        pvpButton.setPreferredSize(new Dimension(300, 80));
        pvpButton.addActionListener(e -> {
            mainplay.GameRoom game = new mainplay.GameRoom(0, 0);
            game.setVisible(true);
            this.dispose();
        });
        add(pvpButton, gbc);
    }
}
