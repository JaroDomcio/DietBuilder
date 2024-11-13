package logic;

import javax.swing.*;
import java.util.ArrayList;

public class LogicManagerIngredients {
    private FileHandler fH;
    private ArrayList<Ingredient> ingredients;

    public LogicManagerIngredients(ArrayList<Ingredient> ingredients, FileHandler fH) {
        this.ingredients = ingredients;
        this.fH = fH;
    }

    public void addIngredient(String name, int carbs, int fat, int protein, String type) {
        int id = findLowestAvailableIngredientId();
        Ingredient newIngredient = new Ingredient(id, name, carbs, fat, protein, type);
        ingredients.add(newIngredient);
        fH.saveIngredients(ingredients);
    }

    public void deleteIngredient(int id) {
        Ingredient ingredientToDelete = findIngredientById(id);
        if (ingredientToDelete != null) {
            ingredients.remove(ingredientToDelete);
            fH.saveIngredients(ingredients);
        } else {
            System.out.println("Składnik o podanym ID nie został znaleziony.");
        }
    }


    public void changeMacro(int id, int carbs, int fat, int protein, String type) {
        Ingredient ingredientToEdit = findIngredientById(id);
        if (ingredientToEdit != null) {
            ingredientToEdit.setCarbs(carbs);
            ingredientToEdit.setFat(fat);
            ingredientToEdit.setProtein(protein);
            ingredientToEdit.setType(type);
            fH.saveIngredients(ingredients);
        } else {
            System.out.println("Składnik o podanym ID nie został znaleziony.");
        }
    }

    public DefaultListModel<String> getIngredientListData() {
        DefaultListModel<String> ingredientListModel = new DefaultListModel<>();
        for (Ingredient ingredient : ingredients) {
            String ingredientData = String.format("%s - carbs: %d, fat: %d, protein: %d, type: %s",
                    ingredient.getName(), ingredient.getCarbs(), ingredient.getFat(), ingredient.getProtein(), ingredient.getType());
            ingredientListModel.addElement(ingredientData);
        }
        return ingredientListModel;
    }

    public void endProgram() {
        fH.saveIngredients(ingredients);
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }


    public Ingredient findIngredientById(int id) {
        for (Ingredient ing : ingredients) {
            if (ing.getId() == id) {
                return ing;
            }
        }
        return null; // Zwraca null, jeśli składnik o podanym ID nie został znaleziony
    }

    private int findLowestAvailableIngredientId() {
        int id = 1; // Zaczynamy od ID 1
        boolean idExists = true;

        while (idExists) {
            idExists = false;
            for (Ingredient ing : ingredients) {
                if (ing.getId() == id) {
                    id++;
                    idExists = true;
                    break;
                }
            }
        }
        return id;
    }
}
