package pwr.main;

import javax.swing.*;
import java.awt.*;
import logic.LogicManager;

public class GUI {
    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private LogicManager logicManager;

    public GUI(LogicManager logicManager) {
        this.logicManager = logicManager;

        // Tworzenie głównego okna (JFrame)
        frame = new JFrame("Diet Builder");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Inicjalizacja CardLayout i głównego panelu
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Tworzenie paneli dla poszczególnych widoków
        JPanel homePanel = createHomePanel();
        JPanel ingredientsPanel = new IngredientsOptionsGUI(logicManager, cardLayout, mainPanel).getPanel();
        JPanel mealsPanel = new MealsOptionsGUI(cardLayout, mainPanel).getPanel();

        // Dodanie paneli do CardLayout
        mainPanel.add(homePanel, "Home");
        mainPanel.add(ingredientsPanel, "Ingredients");
        mainPanel.add(mealsPanel, "Meals");

        // Dodanie głównego panelu do okna
        frame.add(mainPanel);

        // Ustawienie początkowego widoku
        cardLayout.show(mainPanel, "Home");

        // Wyświetlenie okna
        frame.setVisible(true);
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton mealsButton = new JButton("Posiłki");
        JButton ingredientsButton = new JButton("Składniki");

        buttonPanel.add(mealsButton);
        buttonPanel.add(ingredientsButton);

        panel.add(buttonPanel, BorderLayout.NORTH);

        JLabel label = new JLabel("Witaj w aplikacji!", SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);

        mealsButton.addActionListener(e -> cardLayout.show(mainPanel, "Meals"));
        ingredientsButton.addActionListener(e -> cardLayout.show(mainPanel, "Ingredients"));

        return panel;
    }
}

