package GUI;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import logic.Ingredient;
import logic.LogicManagerIngredients;
import logic.LogicManagerMeals;
import logic.Meal;

public class IngredientsOptionsGUI {
    private JPanel ingredientsPanel;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private LogicManagerIngredients logicManagerIngredients;
    private LogicManagerMeals logicManagerMeals;
    private GUI mainGUI;

    public IngredientsOptionsGUI(LogicManagerIngredients logicManagerIngredients, LogicManagerMeals logicManagerMeals, CardLayout cardLayout, JPanel mainPanel, GUI mainGUI) {
        this.logicManagerIngredients = logicManagerIngredients;
        this.logicManagerMeals = logicManagerMeals;
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.mainGUI = mainGUI;

        ingredientsPanel = new JPanel(new FlowLayout());

        JButton addIngredientButton = new JButton("Dodaj składnik");
        JButton editIngredientButton = new JButton("Edytuj składnik");
        JButton deleteIngredientButton = new JButton("Usuń składnik");
        JButton backButton = new JButton("Powrót");

        ingredientsPanel.add(addIngredientButton);
        ingredientsPanel.add(editIngredientButton);
        ingredientsPanel.add(deleteIngredientButton);
        ingredientsPanel.add(backButton);

        addIngredientButton.addActionListener(e -> AddIngredientForm());
        editIngredientButton.addActionListener(e -> EditIngredientForm());
        deleteIngredientButton.addActionListener(e -> deleteIngredient());
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Home"));
    }

    private void AddIngredientForm() {
        JPanel addIngredientPanel = createIngredientForm("Dodaj składnik", false, null);
        mainPanel.add(addIngredientPanel, "AddIngredient");
        cardLayout.show(mainPanel, "AddIngredient");
    }

    private void EditIngredientForm() {
        Ingredient selectedIngredient = selectIngredientFromList("Wybierz składnik do edycji:");
        if (selectedIngredient != null) {
            JPanel editIngredientPanel = createIngredientForm("Edytuj składnik", true, selectedIngredient);
            mainPanel.add(editIngredientPanel, "EditIngredient");
            cardLayout.show(mainPanel, "EditIngredient");
        }
    }

    private void deleteIngredient() {
        Ingredient selectedIngredient = selectIngredientFromList("Wybierz składnik do usunięcia:");
        if (selectedIngredient != null) {
            boolean success = logicManagerIngredients.deleteIngredient(selectedIngredient.getId());
            if (success) {
                // Usuwanie posiłków zawierających usunięty składnik
                List<Meal> deletedMeals = logicManagerMeals.deleteMealsContainingIngredient(selectedIngredient.getId());
                if (deletedMeals.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Składnik został usunięty.");
                } else {
                    StringBuilder message = new StringBuilder("Składnik oraz następujące posiłki zostały usunięte:\n");
                    for (Meal meal : deletedMeals) {
                        message.append("- ").append(meal.getName()).append("\n");
                    }
                    JOptionPane.showMessageDialog(null, message.toString(), "Informacja", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Nie znaleziono składnika do usunięcia.", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Ingredient selectIngredientFromList(String message) {
        List<Ingredient> ingredients = logicManagerIngredients.getIngredients();
        if (ingredients.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Brak dostępnych składników.", "Informacja", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        Ingredient selectedIngredient = (Ingredient) JOptionPane.showInputDialog(
                null, message, "Wybór składnika",
                JOptionPane.QUESTION_MESSAGE, null,
                ingredients.toArray(), ingredients.get(0));

        return selectedIngredient;
    }

    private JPanel createIngredientForm(String title, boolean isEditMode, Ingredient ingredient) {
        JPanel panel = new JPanel(new BorderLayout()); // Zmieniamy na BorderLayout

        // Dodanie etykiety informacyjnej na górze
        panel.add(new JLabel(isEditMode ? "Edytuj składnik (makroskładniki podaj dla 100 gramów)" :"Dodaj składnik (makroskładniki podaj dla 100 gramów)",
                SwingConstants.CENTER), BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));

        JLabel nameLabel = new JLabel("Nazwa:");
        JTextField nameField = new JTextField();
        nameField.setEditable(!isEditMode);

        JLabel carbsLabel = new JLabel("Węglowodany:");
        JTextField carbsField = new JTextField();

        JLabel fatLabel = new JLabel("Tłuszcz:");
        JTextField fatField = new JTextField();

        JLabel proteinLabel = new JLabel("Białko:");
        JTextField proteinField = new JTextField();

        JLabel typeLabel = new JLabel("Typ:");
        JTextField typeField = new JTextField();

        if (isEditMode && ingredient != null) {
            nameField.setText(ingredient.getName());
            carbsField.setText(String.valueOf(ingredient.getCarbs()));
            fatField.setText(String.valueOf(ingredient.getFat()));
            proteinField.setText(String.valueOf(ingredient.getProtein()));
            typeField.setText(ingredient.getType());
        }

        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(carbsLabel);
        inputPanel.add(carbsField);
        inputPanel.add(fatLabel);
        inputPanel.add(fatField);
        inputPanel.add(proteinLabel);
        inputPanel.add(proteinField);
        inputPanel.add(typeLabel);
        inputPanel.add(typeField);

        // Dodanie inputPanel na środek panelu
        panel.add(new JScrollPane(inputPanel), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton(isEditMode ? "Zapisz zmiany" : "Dodaj składnik");
        JButton backButton = new JButton("Powrót");

        buttonPanel.add(saveButton);
        buttonPanel.add(backButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                int carbs = Integer.parseInt(carbsField.getText());
                int fat = Integer.parseInt(fatField.getText());
                int protein = Integer.parseInt(proteinField.getText());
                String type = typeField.getText();

                if (type == null || type.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Pole 'Typ' jest wymagane.", "Błąd", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (isEditMode && ingredient != null) {
                    boolean success = logicManagerIngredients.changeMacro(ingredient.getId(), carbs, fat, protein, type);
                    if (success) {
                        JOptionPane.showMessageDialog(null, "Składnik został zaktualizowany!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Nie znaleziono składnika do edycji.", "Błąd", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    boolean success = logicManagerIngredients.addIngredient(name, carbs, fat, protein, type);
                    if (success) {
                        JOptionPane.showMessageDialog(null, "Składnik został dodany!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Wszystkie pola, w tym 'Typ', są wymagane.", "Błąd", JOptionPane.ERROR_MESSAGE);
                    }
                }

                clearFields(nameField, carbsField, fatField, proteinField, typeField);
                cardLayout.show(mainPanel, "Ingredients");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Wprowadź poprawne dane liczby dla makroskładników.", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        });


        backButton.addActionListener(e -> {
            clearFields(nameField, carbsField, fatField, proteinField, typeField);
            cardLayout.show(mainPanel, "Ingredients");
        });

        return panel;
    }


    private void clearFields(JTextField... fields) {
        for (JTextField field : fields) {
            field.setText("");
        }
    }

    public JPanel getPanel() {
        return ingredientsPanel;
    }
}
