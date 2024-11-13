package logic;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class LogicManagerMeals {

    private FileHandler fH;
    private ArrayList<Meal> meals;
    private LogicManagerIngredients logicManagerIngredients;

    public LogicManagerMeals(ArrayList<Meal> meals, FileHandler fH, LogicManagerIngredients logicManagerIngredients) {
        this.meals = meals;
        this.fH = fH;
        this.logicManagerIngredients = logicManagerIngredients;
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

    public DefaultListModel<String> getMealListData() {
        DefaultListModel<String> mealListModel = new DefaultListModel<>();
        for (Meal meal : meals) {
            mealListModel.addElement(meal.getName()); // Dodaj nazwę posiłku
        }
        return mealListModel;
    }

    public String getMealIngredientsInfo(Meal meal) {
        if (meal == null || meal.getIngredientsIds().isEmpty()) {
            return "Nie znaleziono składników dla wybranego posiłku.";
        }

        StringBuilder ingredientsInfo = new StringBuilder("Składniki dla posiłku: " + meal.getName() + "\n");
        List<Integer> ingredientIds = meal.getIngredientsIds();
        List<Integer> quantities = meal.getQuantities();

        // Inicjalizacja zmiennych do obliczania sum kalorii i makroskładników
        int totalCalories = 0;
        int totalCarbs = 0;
        int totalProtein = 0;
        int totalFat = 0;

        for (int i = 0; i < ingredientIds.size(); i++) {
            Ingredient ingredient = logicManagerIngredients.findIngredientById(ingredientIds.get(i));
            int quantity = quantities.get(i);

            if (ingredient != null) {
                ingredientsInfo.append(String.format("- %s: %d g\n", ingredient.getName(), quantity));

                // Dodawanie do sumy kalorii i makroskładników na podstawie ilości
                totalCalories += ingredient.getCalories() * quantity / 100;
                totalCarbs += ingredient.getCarbs() * quantity / 100;
                totalProtein += ingredient.getProtein() * quantity / 100;
                totalFat += ingredient.getFat() * quantity / 100;
            }
        }

        // Dodanie podsumowania kalorii i makroskładników do informacji o posiłku
        ingredientsInfo.append("\nPodsumowanie makroskładników i kalorii dla posiłku:\n");
        ingredientsInfo.append(String.format("Kalorie: %d kcal\n", totalCalories));
        ingredientsInfo.append(String.format("Węglowodany: %d g\n", totalCarbs));
        ingredientsInfo.append(String.format("Białko: %d g\n", totalProtein));
        ingredientsInfo.append(String.format("Tłuszcze: %d g\n", totalFat));

        return ingredientsInfo.toString();
    }
}


