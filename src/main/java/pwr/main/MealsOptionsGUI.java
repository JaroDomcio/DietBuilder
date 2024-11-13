package pwr.main;

import javax.swing.*;
import java.awt.*;

public class MealsOptionsGUI {
    private JPanel mealsPanel;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MealsOptionsGUI(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;

        // Tworzenie panelu dla opcji posiłków
        mealsPanel = new JPanel(new FlowLayout());

        JLabel label = new JLabel("Tutaj będą opcje dla posiłków.");
        JButton backButton = new JButton("Powrót");

        mealsPanel.add(label);
        mealsPanel.add(backButton);

        // Powrót do ekranu głównego
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Home"));
    }

    public JPanel getPanel() {
        return mealsPanel;
    }
}
