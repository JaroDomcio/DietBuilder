package logic;

import javax.swing.DefaultListModel;
import java.util.ArrayList;
import java.util.List;

public class LogicManagerMeals {

    private DatabaseHandler dbHandler;
    private DefaultListModel<Meal> meals;
    private LogicManagerIngredients logicManagerIngredients;

    public LogicManagerMeals(DatabaseHandler dbHandler, LogicManagerIngredients logicManagerIngredients) {
        this.dbHandler = dbHandler;
        this.logicManagerIngredients = logicManagerIngredients;
        this.meals = dbHandler.loadMeals();
    }

    public DefaultListModel<Meal> getMealListData() {
        return meals;
    }

    public Meal findMealByID(int id) {
        for (int i = 0; i < meals.size(); i++) {
            Meal meal = meals.getElementAt(i);
            if (meal.getId() == id) {
                return meal;
            }
        }
        return null;
    }

    public String getMealIngredientsInfo(Meal meal){

        StringBuilder ingredientsInfo = new StringBuilder();
        List<Integer> ingredientIds = meal.getIngredientsIds();
        List<Integer> quantities = meal.getQuantities();
        for (int i = 0; i < ingredientIds.size(); i++) {
            Ingredient ingredient = logicManagerIngredients.findIngredientById(ingredientIds.get(i));
            int quantity = quantities.get(i);

            if (ingredient != null) {
                ingredientsInfo.append(String.format("- %s: %d g\n", ingredient.getName(), quantity));
            }
        }
        return ingredientsInfo.toString();
    }

    public ArrayList<Integer> getMealMacroInfo(Meal meal) {
        ArrayList<Integer> macro = new ArrayList<>();
        if (meal == null || meal.getIngredientsIds().isEmpty()) {
            return macro;
        }

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

                totalCalories += ingredient.getCalories() * quantity / 100;
                totalCarbs += ingredient.getCarbs() * quantity / 100;
                totalProtein += ingredient.getProtein() * quantity / 100;
                totalFat += ingredient.getFat() * quantity / 100;
            }
        }


        macro.add(totalCalories);
        macro.add(totalCarbs);
        macro.add(totalProtein);
        macro.add(totalFat);

        return macro;
    }

    public boolean addMeal(String mealName, ArrayList<Integer> ingredientIds, ArrayList<Integer> quantities) {
        if (mealName == null || mealName.isEmpty() || ingredientIds.isEmpty() || quantities.isEmpty()) {
            return false;
        }

        if (ingredientIds.size() != quantities.size()) {
            return false;
        }

        int newId = findLowestAvailableMealId();
        Meal newMeal = new Meal(newId, mealName);

        for (int i = 0; i < ingredientIds.size(); i++) {
            newMeal.addIngredient(ingredientIds.get(i));
            newMeal.addQuantity(quantities.get(i));
        }

        meals.addElement(newMeal);
        dbHandler.saveMeal(newMeal);
        return true;
    }

    public boolean editMeal(int mealId, ArrayList<Integer> ingredientIds, ArrayList<Integer> quantities) {
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
            if (index >= 0) {
                meals.setElementAt(mealToEdit, index);
            }
            dbHandler.updateMeal(mealToEdit);
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteMeal(int mealId) {
        Meal mealToDelete = findMealByID(mealId);
        if (mealToDelete != null) {
            meals.removeElement(mealToDelete);
            dbHandler.deleteMeal(mealId);
            return true;
        } else {
            return false;
        }
    }

    public List<Meal> deleteMealsContainingIngredient(int ingredientId) {
        List<Meal> mealsToDelete = new ArrayList<>();
        for (int i = meals.size() - 1; i >= 0; i--) {
            Meal meal = meals.getElementAt(i);
            if (meal.getIngredientsIds().contains(ingredientId)) {
                mealsToDelete.add(meal);
                meals.removeElementAt(i);
                dbHandler.deleteMeal(meal.getId());
            }
        }
        return mealsToDelete;
    }

    private int findLowestAvailableMealId() {
        int id = 1;
        boolean idExists = true;

        while (idExists) {
            idExists = false;
            for (int i = 0; i < meals.size(); i++) {
                Meal meal = meals.getElementAt(i);
                if (meal.getId() == id) {
                    id++;
                    idExists = true;
                    break;
                }
            }
        }
        return id;
    }

    public List<Meal> getMeals() {
        List<Meal> list = new ArrayList<>();
        for (int i = 0; i < meals.size(); i++) {
            list.add(meals.getElementAt(i));
        }
        return list;
    }
}