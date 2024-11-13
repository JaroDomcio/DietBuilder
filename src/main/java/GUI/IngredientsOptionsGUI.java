package GUI;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import logic.Ingredient;
import logic.LogicManagerIngredients;

public class IngredientsOptionsGUI {
    private JPanel ingredientsPanel;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private LogicManagerIngredients logicManagerIngredients;
    private GUI mainGUI;

    public IngredientsOptionsGUI(LogicManagerIngredients logicManagerIngredients, CardLayout cardLayout, JPanel mainPanel, GUI mainGUI) {
        this.logicManagerIngredients = logicManagerIngredients;
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
            logicManagerIngredients.deleteIngredient(selectedIngredient.getId());
            JOptionPane.showMessageDialog(null, "Składnik został usunięty!");
            mainGUI.refreshIngredientList(); // Odświeżenie listy składników w GUI
        }
    }

    private Ingredient selectIngredientFromList(String message) {
        List<Ingredient> ingredients = logicManagerIngredients.getIngredients();
        Ingredient selectedIngredient = (Ingredient) JOptionPane.showInputDialog(
                null, message, "Wybór składnika",
                JOptionPane.QUESTION_MESSAGE, null,
                ingredients.toArray(), ingredients.get(0));

        return selectedIngredient;
    }

    private JPanel createIngredientForm(String title, boolean isEditMode, Ingredient ingredient) {
        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));

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

        JButton saveButton = new JButton(isEditMode ? "Zapisz zmiany" : "Dodaj składnik");
        JButton backButton = new JButton("Powrót");

        panel.add(saveButton);
        panel.add(backButton);

        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                int carbs = Integer.parseInt(carbsField.getText());
                int fat = Integer.parseInt(fatField.getText());
                int protein = Integer.parseInt(proteinField.getText());
                String type = typeField.getText();

                if (isEditMode && ingredient != null) {
                    logicManagerIngredients.changeMacro(ingredient.getId(), carbs, fat, protein, type);
                    JOptionPane.showMessageDialog(null, "Składnik został zaktualizowany!");
                } else {
                    logicManagerIngredients.addIngredient(name, carbs, fat, protein, type);
                    JOptionPane.showMessageDialog(null, "Składnik został dodany!");
                }

                mainGUI.refreshIngredientList(); // Odświeżenie listy składników w GUI

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






