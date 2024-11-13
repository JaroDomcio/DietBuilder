package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import logic.LogicManagerIngredients;
import logic.LogicManagerMeals;
import logic.Meal;

public class GUI {
    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private LogicManagerIngredients logicManagerIngredients;
    private LogicManagerMeals logicManagerMeals;

    public GUI(LogicManagerIngredients logicManagerIngredients, LogicManagerMeals logicManagerMeals) {
        this.logicManagerIngredients = logicManagerIngredients;
        this.logicManagerMeals = logicManagerMeals;

        // Tworzenie głównego okna (JFrame)
        frame = new JFrame("Diet Builder");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Inicjalizacja CardLayout i głównego panelu
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Tworzenie paneli dla poszczególnych widoków
        JPanel homePanel = createHomePanel();
        JPanel ingredientsPanel = new IngredientsOptionsGUI(logicManagerIngredients, cardLayout, mainPanel).getPanel();
        JPanel mealsPanel = new MealsOptionsGUI(logicManagerMeals, cardLayout, mainPanel).getPanel();

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

        // Utworzenie panelu z listami
        JPanel listPanel = new JPanel(new GridLayout(1, 2));

        // Lista posiłków (po lewej stronie)
        JList<String> mealsList = new JList<>(logicManagerMeals.getMealListData());
        mealsList.setBorder(BorderFactory.createTitledBorder("Posiłki"));
        listPanel.add(new JScrollPane(mealsList));

        // Dodanie MouseListener dla podwójnego kliknięcia na element w liście posiłków
        mealsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Sprawdzamy, czy jest podwójne kliknięcie
                    int index = mealsList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        Meal selectedMeal = logicManagerMeals.findMealByID(index + 1); // Zakładamy, że ID = indeks + 1
                        String mealInfo = logicManagerMeals.getMealIngredientsInfo(selectedMeal);
                        JOptionPane.showMessageDialog(frame, mealInfo, "Składniki Posiłku", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });

        // Lista składników (po prawej stronie)
        JList<String> ingredientsList = new JList<>(logicManagerIngredients.getIngredientListData());
        ingredientsList.setBorder(BorderFactory.createTitledBorder("Składniki i Makroskładniki"));
        listPanel.add(new JScrollPane(ingredientsList));

        // Dodanie panelu z listami do głównego panelu
        panel.add(listPanel, BorderLayout.CENTER);

        // Dodanie przycisków nawigacyjnych
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton mealsButton = new JButton("Posiłki");
        JButton ingredientsButton = new JButton("Składniki");

        buttonPanel.add(mealsButton);
        buttonPanel.add(ingredientsButton);

        panel.add(buttonPanel, BorderLayout.NORTH);

        JLabel label = new JLabel("Witaj w aplikacji!", SwingConstants.CENTER);
        panel.add(label, BorderLayout.SOUTH);

        // Akcje przycisków do przełączania widoków
        mealsButton.addActionListener(e -> cardLayout.show(mainPanel, "Meals"));
        ingredientsButton.addActionListener(e -> cardLayout.show(mainPanel, "Ingredients"));

        return panel;
    }
}
