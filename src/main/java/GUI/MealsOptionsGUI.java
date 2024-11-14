package GUI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import logic.Ingredient;
import logic.LogicManagerIngredients;
import logic.LogicManagerMeals;
import logic.Meal;

public class MealsOptionsGUI {
    private JPanel panel;
    private LogicManagerMeals logicManagerMeals;
    private LogicManagerIngredients logicManagerIngredients;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private GUI mainGUI;
    private JPanel addMealPanel;

    public MealsOptionsGUI(LogicManagerMeals logicManagerMeals, LogicManagerIngredients logicManagerIngredients, CardLayout cardLayout, JPanel mainPanel, GUI mainGUI) {
        this.logicManagerMeals = logicManagerMeals;
        this.logicManagerIngredients = logicManagerIngredients;
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.mainGUI = mainGUI;
        this.panel = createMainPanel();

        mainPanel.add(panel, "Meals");
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JButton addMealButton = new JButton("Dodaj posiłek");
        JButton editMealButton = new JButton("Edytuj posiłek");
        JButton deleteMealButton = new JButton("Usuń posiłek");
        JButton backButton = new JButton("Powrót");

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(addMealButton);
        topPanel.add(editMealButton);
        topPanel.add(deleteMealButton);
        topPanel.add(backButton);

        addMealButton.addActionListener(e -> {
            if (addMealPanel != null) {
                mainPanel.remove(addMealPanel);
            }
            addMealPanel = createAddMealPanel(null, false);
            mainPanel.add(addMealPanel, "AddMeal");
            cardLayout.show(mainPanel, "AddMeal");
            mainPanel.revalidate();
            mainPanel.repaint();
        });

        editMealButton.addActionListener(e -> {
            Meal selectedMeal = selectMealFromList("Wybierz posiłek do edycji:");
            if (selectedMeal != null) {
                if (addMealPanel != null) {
                    mainPanel.remove(addMealPanel);
                }
                addMealPanel = createAddMealPanel(selectedMeal, true);
                mainPanel.add(addMealPanel, "EditMeal");
                cardLayout.show(mainPanel, "EditMeal");
                mainPanel.revalidate();
                mainPanel.repaint();
            }
        });

        deleteMealButton.addActionListener(e -> {
            Meal selectedMeal = selectMealFromList("Wybierz posiłek do usunięcia:");
            if (selectedMeal != null) {
                logicManagerMeals.deleteMeal(selectedMeal.getId());
                JOptionPane.showMessageDialog(null, "Posiłek został usunięty.", "Informacja", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        backButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "Home");
        });

        panel.add(topPanel, BorderLayout.NORTH);
        return panel;
    }

    private Meal selectMealFromList(String message) {
        java.util.List<Meal> meals = logicManagerMeals.getMeals();
        if (meals.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Brak dostępnych posiłków.", "Informacja", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        Meal selectedMeal = (Meal) JOptionPane.showInputDialog(
                null, message, "Wybór posiłku",
                JOptionPane.QUESTION_MESSAGE, null,
                meals.toArray(), meals.get(0));

        return selectedMeal;
    }

    private JPanel createAddMealPanel(Meal mealToEdit, boolean isEditMode) {
        JPanel addMealPanel = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 5, 5));

        JTextField nameField = new JTextField();
        inputPanel.add(new JLabel("Nazwa:"));
        inputPanel.add(nameField);

        if (isEditMode && mealToEdit != null) {
            nameField.setText(mealToEdit.getName());
            nameField.setEditable(false); // Blokujemy edycję nazwy
        }

        ArrayList<JCheckBox> checkBoxes = new ArrayList<>();
        ArrayList<JTextField> quantityFields = new ArrayList<>();
        ArrayList<Integer> ingredientIds = new ArrayList<>();

        for (Ingredient ingredient : logicManagerIngredients.getIngredients()) {
            JCheckBox checkBox = new JCheckBox(ingredient.getName());
            JTextField quantityField = new JTextField("0");

            inputPanel.add(checkBox);
            inputPanel.add(quantityField);

            checkBoxes.add(checkBox);
            quantityFields.add(quantityField);
            ingredientIds.add(ingredient.getId());

            if (isEditMode && mealToEdit != null) {
                if (mealToEdit.getIngredientsIds().contains(ingredient.getId())) {
                    checkBox.setSelected(true);
                    int index = mealToEdit.getIngredientsIds().indexOf(ingredient.getId());
                    int quantity = mealToEdit.getQuantities().get(index);
                    quantityField.setText(String.valueOf(quantity));
                }
            }
        }

        addMealPanel.add(new JLabel(isEditMode ? "Edytuj posiłek" : "Dodaj nowy posiłek"), BorderLayout.NORTH);
        addMealPanel.add(new JScrollPane(inputPanel), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton confirmButton = new JButton(isEditMode ? "Zapisz zmiany" : "Dodaj posiłek");
        JButton backButton = new JButton("Powrót");

        buttonPanel.add(confirmButton);
        buttonPanel.add(backButton);

        addMealPanel.add(buttonPanel, BorderLayout.SOUTH);

        confirmButton.addActionListener(e -> {
            try {
                String mealName = nameField.getText();

                if (mealName.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Nazwa posiłku jest wymagana.", "Błąd", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                ArrayList<Integer> selectedIngredientIds = new ArrayList<>();
                ArrayList<Integer> selectedQuantities = new ArrayList<>();

                boolean hasError = false;

                for (int i = 0; i < checkBoxes.size(); i++) {
                    JCheckBox checkBox = checkBoxes.get(i);
                    JTextField quantityField = quantityFields.get(i);
                    int ingredientId = ingredientIds.get(i);

                    if (checkBox.isSelected()) {
                        try {
                            int quantity = Integer.parseInt(quantityField.getText());
                            if (quantity <= 0) {
                                JOptionPane.showMessageDialog(null, "Ilość składnika musi być większa od 0.", "Błąd", JOptionPane.ERROR_MESSAGE);
                                hasError = true;
                                break;
                            }
                            selectedIngredientIds.add(ingredientId);
                            selectedQuantities.add(quantity);
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(null, "Błędna ilość składnika. Wprowadź poprawne wartości liczbowe.", "Błąd", JOptionPane.ERROR_MESSAGE);
                            hasError = true;
                            break;
                        }
                    }
                }

                if (hasError) {
                    return;
                }

                if (selectedIngredientIds.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Wybierz przynajmniej jeden składnik i podaj ilość.", "Błąd", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (isEditMode && mealToEdit != null) {
                    logicManagerMeals.editMeal(mealToEdit.getId(), selectedIngredientIds, selectedQuantities);
                    JOptionPane.showMessageDialog(null, "Posiłek został zaktualizowany.", "Informacja", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    logicManagerMeals.addMeal(mealName, selectedIngredientIds, selectedQuantities);
                    JOptionPane.showMessageDialog(null, "Posiłek został dodany.", "Informacja", JOptionPane.INFORMATION_MESSAGE);
                }


                nameField.setText("");
                for (int i = 0; i < checkBoxes.size(); i++) {
                    checkBoxes.get(i).setSelected(false);
                    quantityFields.get(i).setText("0");
                }

                cardLayout.show(mainPanel, "Meals");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Wystąpił błąd podczas zapisywania posiłku.", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "Meals");
        });

        return addMealPanel;
    }

    public JPanel getPanel() {
        return panel;
    }
}












