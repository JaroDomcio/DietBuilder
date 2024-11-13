package logic;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class LogicManagerMeals {

    private FileHandler fH;
    private ArrayList<Meal> meals;
    private LogicManagerIngredients logicManagerIngredients;
    private DefaultListModel<Meal> mealListModel;

    public LogicManagerMeals(ArrayList<Meal> meals, FileHandler fH, LogicManagerIngredients logicManagerIngredients) {
        this.meals = meals;
        this.fH = fH;
        this.logicManagerIngredients = logicManagerIngredients;
        this.mealListModel = new DefaultListModel<>();
        initializeMealListModel();
    }

    private void initializeMealListModel() {
        for (Meal meal : meals) {
            mealListModel.addElement(meal);
        }
    }

    public DefaultListModel<Meal> getMealListData() {
        return mealListModel;
    }

    public ArrayList<Meal> getMeals() {
        return meals;
    }

    public Meal findMealByID(int id) {
        for (Meal meal : meals) {
            if (meal.getId() == id) {
                return meal;
            }
        }
        return null;
    }

    public String getMealIngredientsInfo(Meal meal) {
        if (meal == null || meal.getIngredientsIds().isEmpty()) {
            return "Nie znaleziono składników dla wybranego posiłku.";
        }

        StringBuilder ingredientsInfo = new StringBuilder("Składniki dla posiłku: " + meal.getName() + "\n");
        List<Integer> ingredientIds = meal.getIngredientsIds();
        List<Integer> quantities = meal.getQuantities();

        int totalCalories = 0;
        int totalCarbs = 0;
        int totalProtein = 0;
        int totalFat = 0;

        for (int i = 0; i < ingredientIds.size(); i++) {
            Ingredient ingredient = logicManagerIngredients.findIngredientById(ingredientIds.get(i));
            int quantity = quantities.get(i);

            if (ingredient != null) {
                ingredientsInfo.append(String.format("- %s: %d g\n", ingredient.getName(), quantity));

                totalCalories += ingredient.getCalories() * quantity / 100;
                totalCarbs += ingredient.getCarbs() * quantity / 100;
                totalProtein += ingredient.getProtein() * quantity / 100;
                totalFat += ingredient.getFat() * quantity / 100;
            }
        }

        ingredientsInfo.append("\nPodsumowanie makroskładników i kalorii dla posiłku:\n");
        ingredientsInfo.append(String.format("Kalorie: %d kcal\n", totalCalories));
        ingredientsInfo.append(String.format("Węglowodany: %d g\n", totalCarbs));
        ingredientsInfo.append(String.format("Białko: %d g\n", totalProtein));
        ingredientsInfo.append(String.format("Tłuszcze: %d g\n", totalFat));

        return ingredientsInfo.toString();
    }

    public void addMeal(String mealName, ArrayList<Integer> ingredientIds, ArrayList<Integer> quantities) {
        if (mealName == null || mealName.isEmpty() || ingredientIds.isEmpty() || quantities.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nazwa posiłku i składniki muszą być podane.", "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (ingredientIds.size() != quantities.size()) {
            JOptionPane.showMessageDialog(null, "Liczba składników musi odpowiadać liczbie ilości.", "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int newId = findLowestAvailableMealId();
        Meal newMeal = new Meal(newId, mealName);

        for (int i = 0; i < ingredientIds.size(); i++) {
            newMeal.addIngredient(ingredientIds.get(i));
            newMeal.addQuantity(quantities.get(i));
        }

        meals.add(newMeal);
        mealListModel.addElement(newMeal);
        fH.saveMeals(meals);
    }

    public void editMeal(int mealId, ArrayList<Integer> ingredientIds, ArrayList<Integer> quantities) {
        Meal mealToEdit = findMealByID(mealId);
        if (mealToEdit != null) {
            mealToEdit.getIngredientsIds().clear();
            mealToEdit.getQuantities().clear();

            for (int i = 0; i < ingredientIds.size(); i++) {
                mealToEdit.addIngredient(ingredientIds.get(i));
                mealToEdit.addQuantity(quantities.get(i));
            }

            // Aktualizacja modelu listy
            int index = meals.indexOf(mealToEdit);
            mealListModel.setElementAt(mealToEdit, index);
            fH.saveMeals(meals);
        } else {
            JOptionPane.showMessageDialog(null, "Nie znaleziono posiłku do edycji.", "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteMeal(int mealId) {
        Meal mealToDelete = findMealByID(mealId);
        if (mealToDelete != null) {
            meals.remove(mealToDelete);
            mealListModel.removeElement(mealToDelete);
            fH.saveMeals(meals);
        } else {
            JOptionPane.showMessageDialog(null, "Nie znaleziono posiłku do usunięcia.", "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int findLowestAvailableMealId() {
        int id = 1;
        boolean idExists = true;

        while (idExists) {
            idExists = false;
            for (Meal meal : meals) {
                if (meal.getId() == id) {
                    id++;
                    idExists = true;
                    break;
                }
            }
        }
        return id;
    }
}







