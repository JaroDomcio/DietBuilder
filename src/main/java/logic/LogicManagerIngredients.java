package logic;

import javax.swing.*;
import java.util.ArrayList;

public class LogicManagerIngredients {
    private FileHandler fH;
    private ArrayList<Ingredient> ingredients;
    private DefaultListModel<Ingredient> ingredientListModel;

    public LogicManagerIngredients(ArrayList<Ingredient> ingredients, FileHandler fH) {
        this.ingredients = ingredients;
        this.fH = fH;
        this.ingredientListModel = new DefaultListModel<>();
        initializeIngredientListModel();
    }

    private void initializeIngredientListModel() {
        for (Ingredient ingredient : ingredients) {
            ingredientListModel.addElement(ingredient);
        }
    }

    public DefaultListModel<Ingredient> getIngredientListData() {
        return ingredientListModel;
    }

    public void addIngredient(String name, int carbs, int fat, int protein, String type) {
        int id = findLowestAvailableIngredientId();
        Ingredient newIngredient = new Ingredient(id, name, carbs, fat, protein, type);
        ingredients.add(newIngredient);
        ingredientListModel.addElement(newIngredient);
        fH.saveIngredients(ingredients);
    }

    public void deleteIngredient(int id) {
        Ingredient ingredientToDelete = findIngredientById(id);
        if (ingredientToDelete != null) {
            ingredients.remove(ingredientToDelete);
            ingredientListModel.removeElement(ingredientToDelete);
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
            // Aktualizacja modelu listy
            int index = ingredients.indexOf(ingredientToEdit);
            ingredientListModel.setElementAt(ingredientToEdit, index);
            fH.saveIngredients(ingredients);
        } else {
            System.out.println("Składnik o podanym ID nie został znaleziony.");
        }
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
        return null;
    }

    private int findLowestAvailableIngredientId() {
        int id = 1;
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





