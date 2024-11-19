package GUI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
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
                boolean success = logicManagerMeals.deleteMeal(selectedMeal.getId());
                if (success) {
                    JOptionPane.showMessageDialog(null, "Posiłek został usunięty.", "Informacja", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Nie znaleziono posiłku do usunięcia.", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        backButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "Home");
        });

        panel.add(topPanel, BorderLayout.NORTH);
        return panel;
    }

    private Meal selectMealFromList(String message) {
        List<Meal> meals = logicManagerMeals.getMeals();
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

        // Panel dla nazwy posiłku
        JPanel inputPanel = new JPanel(new BorderLayout());
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel nameLabel = new JLabel("Nazwa:");
        JTextField nameField = new JTextField(20);
        namePanel.add(nameLabel);
        namePanel.add(nameField);
        inputPanel.add(namePanel, BorderLayout.NORTH);

        if (isEditMode && mealToEdit != null) {
            nameField.setText(mealToEdit.getName());
            nameField.setEditable(false);
        }

        // Panel składników (dynamiczne wiersze z JComboBox i JTextField)
        JPanel ingredientsPanel = new JPanel();
        ingredientsPanel.setLayout(new BoxLayout(ingredientsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(ingredientsPanel);

        // Przycisk dodawania nowego składnika
        JButton addIngredientButton = new JButton("Dodaj składnik");
        addIngredientButton.addActionListener(e -> {
            addIngredientRow(ingredientsPanel, null, "0");
            ingredientsPanel.revalidate();
            ingredientsPanel.repaint();
        });

        // Dodanie istniejących składników w trybie edycji
        if (isEditMode && mealToEdit != null) {
            List<Integer> ingredientIds = mealToEdit.getIngredientsIds();
            List<Integer> quantities = mealToEdit.getQuantities();

            for (int i = 0; i < ingredientIds.size(); i++) {
                Ingredient ingredient = logicManagerIngredients.findIngredientById(ingredientIds.get(i));
                if (ingredient != null) {
                    addIngredientRow(ingredientsPanel, ingredient, String.valueOf(quantities.get(i)));
                }
            }
        } else {
            // Dodajemy pusty wiersz na start
            addIngredientRow(ingredientsPanel, null, "0");
        }

        inputPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel na dole z przyciskiem dodawania składnika
        JPanel addButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addButtonPanel.add(addIngredientButton);
        inputPanel.add(addButtonPanel, BorderLayout.SOUTH);

        addMealPanel.add(new JLabel(isEditMode ? "Edytuj posiłek (składnik podaj w gramach)" : "Dodaj nowy posiłek (składnik podaj w gramach)"), BorderLayout.NORTH);
        addMealPanel.add(inputPanel, BorderLayout.CENTER);

        // Panel z przyciskami
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton confirmButton = new JButton(isEditMode ? "Zapisz zmiany" : "Dodaj posiłek");
        JButton backButton = new JButton("Powrót");
        buttonPanel.add(confirmButton);
        buttonPanel.add(backButton);
        addMealPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Obsługa przycisku potwierdzenia
        confirmButton.addActionListener(e -> {
            try {
                String mealName = nameField.getText().trim();

                if (mealName.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Nazwa posiłku jest wymagana.", "Błąd", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                ArrayList<Integer> selectedIngredientIds = new ArrayList<>();
                ArrayList<Integer> selectedQuantities = new ArrayList<>();

                // Pobranie danych z wierszy
                for (Component component : ingredientsPanel.getComponents()) {
                    if (component instanceof JPanel) {
                        JPanel rowPanel = (JPanel) component;
                        JComboBox<Ingredient> ingredientComboBox = (JComboBox<Ingredient>) rowPanel.getComponent(0);
                        JTextField quantityField = (JTextField) rowPanel.getComponent(1);

                        Ingredient selectedIngredient = (Ingredient) ingredientComboBox.getSelectedItem();
                        if (selectedIngredient == null) {
                            JOptionPane.showMessageDialog(null, "Wybierz składnik w każdym wierszu.", "Błąd", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        try {
                            int quantity = Integer.parseInt(quantityField.getText());
                            if (quantity <= 0) {
                                JOptionPane.showMessageDialog(null, "Ilość składnika " + selectedIngredient.getName() + " musi być większa od 0.", "Błąd", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            if (selectedIngredientIds.contains(selectedIngredient.getId())) {
                                JOptionPane.showMessageDialog(null, "Składnik " + selectedIngredient.getName() + " został wybrany więcej niż raz.", "Błąd", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            selectedIngredientIds.add(selectedIngredient.getId());
                            selectedQuantities.add(quantity);
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(null, "Wprowadź prawidłową ilość dla składnika.", "Błąd", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                }

                if (selectedIngredientIds.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Dodaj przynajmniej jeden składnik.", "Błąd", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean success;
                if (isEditMode && mealToEdit != null) {
                    success = logicManagerMeals.editMeal(mealToEdit.getId(), selectedIngredientIds, selectedQuantities);
                    if (success) {
                        JOptionPane.showMessageDialog(null, "Posiłek został zaktualizowany.", "Informacja", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Nie znaleziono posiłku do edycji.", "Błąd", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    success = logicManagerMeals.addMeal(mealName, selectedIngredientIds, selectedQuantities);
                    if (success) {
                        JOptionPane.showMessageDialog(null, "Posiłek został dodany.", "Informacja", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Błąd podczas dodawania posiłku. Upewnij się, że wszystkie dane są poprawne.", "Błąd", JOptionPane.ERROR_MESSAGE);
                    }
                }

                cardLayout.show(mainPanel, "Meals");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Wystąpił błąd podczas zapisywania posiłku.", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Obsługa przycisku powrotu
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Meals"));

        return addMealPanel;
    }

    // Metoda pomocnicza do dodawania wiersza składnika
    private void addIngredientRow(JPanel parentPanel, Ingredient selectedIngredient, String quantity) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // JComboBox dla składników
        JComboBox<Ingredient> ingredientComboBox = new JComboBox<>();
        for (Ingredient ingredient : logicManagerIngredients.getIngredients()) {
            ingredientComboBox.addItem(ingredient);
        }
        if (selectedIngredient != null) {
            ingredientComboBox.setSelectedItem(selectedIngredient);
        }
        rowPanel.add(ingredientComboBox);

        // JTextField dla ilości
        JTextField quantityField = new JTextField(quantity, 10);
        rowPanel.add(quantityField);

        // Przycisk usuwania wiersza
        JButton removeButton = new JButton("Usuń");
        removeButton.addActionListener(e -> {
            parentPanel.remove(rowPanel);
            parentPanel.revalidate();
            parentPanel.repaint();
        });
        rowPanel.add(removeButton);

        parentPanel.add(rowPanel);
    }


    public JPanel getPanel() {
        return panel;
    }
}
