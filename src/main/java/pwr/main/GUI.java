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
        JPanel ingredientsPanel = createIngredientsOptionsPanel();
        JPanel addIngredientPanel = createAddIngredientPanel();
        JPanel mealsPanel = createMealsOptionsPanel();

        // Dodanie paneli do CardLayout
        mainPanel.add(homePanel, "Home");
        mainPanel.add(ingredientsPanel, "Ingredients");
        mainPanel.add(addIngredientPanel, "AddIngredient");
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

    private JPanel createIngredientsOptionsPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        JButton addIngredientButton = new JButton("Dodaj składnik");
        JButton editIngredientButton = new JButton("Edytuj składnik");
        JButton deleteIngredientButton = new JButton("Usuń składnik");
        JButton backButton = new JButton("Powrót");

        panel.add(addIngredientButton);
        panel.add(editIngredientButton);
        panel.add(deleteIngredientButton);
        panel.add(backButton);

        // Przejście do formularza dodawania składnika
        addIngredientButton.addActionListener(e -> cardLayout.show(mainPanel, "AddIngredient"));
        editIngredientButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Edytuj składnik"));
        deleteIngredientButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Usuń składnik"));

        // Powrót do ekranu głównego
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Home"));

        return panel;
    }

    private JPanel createAddIngredientPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));

        // Pola do wprowadzenia danych składnika
        JLabel nameLabel = new JLabel("Nazwa:");
        JTextField nameField = new JTextField();

        JLabel carbsLabel = new JLabel("Węglowodany:");
        JTextField carbsField = new JTextField();

        JLabel fatLabel = new JLabel("Tłuszcz:");
        JTextField fatField = new JTextField();

        JLabel proteinLabel = new JLabel("Białko:");
        JTextField proteinField = new JTextField();

        JLabel typeLabel = new JLabel("Typ:");
        JTextField typeField = new JTextField();

        // Dodanie etykiet i pól tekstowych do panelu
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(carbsLabel);
        panel.add(carbsField);
        panel.add(fatLabel);
        panel.add(fatField);
        panel.add(proteinLabel);
        panel.add(proteinField);
        panel.add(typeLabel);
        panel.add(typeField);

        // Przycisk zapisu i powrotu
        JButton saveButton = new JButton("Zapisz");
        JButton backButton = new JButton("Powrót");

        panel.add(saveButton);
        panel.add(backButton);

        // Akcja dla przycisku zapisu składnika
        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                int carbs = Integer.parseInt(carbsField.getText());
                int fat = Integer.parseInt(fatField.getText());
                int protein = Integer.parseInt(proteinField.getText());
                String type = typeField.getText();
                // Użycie LogicManager do dodania składnika
                logicManager.addIngredient(name, carbs, fat, protein, type);

                JOptionPane.showMessageDialog(frame, "Składnik został dodany!");
                clearFields(nameField, carbsField, fatField, proteinField, typeField);
                cardLayout.show(mainPanel, "Ingredients");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Wprowadź poprawne dane liczby dla makroskładników.", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Akcja dla przycisku powrotu do głównego panelu składników
        backButton.addActionListener(e -> {
            clearFields(nameField, carbsField, fatField, proteinField, typeField);
            cardLayout.show(mainPanel, "Ingredients");
        });

        return panel;
    }

    private JPanel createMealsOptionsPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        JLabel label = new JLabel("Tutaj będą opcje dla posiłków.");
        JButton backButton = new JButton("Powrót");

        panel.add(label);
        panel.add(backButton);

        // Powrót do ekranu głównego
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Home"));

        return panel;
    }

    private void clearFields(JTextField... fields) {
        for (JTextField field : fields) {
            field.setText("");
        }
    }
}
