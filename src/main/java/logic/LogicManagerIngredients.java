package logic;

import javax.swing.DefaultListModel;
import java.util.ArrayList;
import java.util.List;

public class LogicManagerIngredients {
    private DatabaseHandler dbHandler;
    private DefaultListModel<Ingredient> ingredients;

    public LogicManagerIngredients(DatabaseHandler dbHandler) {
        this.dbHandler = dbHandler;
        this.ingredients = dbHandler.loadIngredients();
    }

    public DefaultListModel<Ingredient> getIngredientListData() {
        return ingredients;
    }

    public boolean addIngredient(String name, int carbs, int fat, int protein, String type) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        int id = findLowestAvailableIngredientId();
        Ingredient newIngredient = new Ingredient(id, name, carbs, fat, protein, type);
        ingredients.addElement(newIngredient);
        dbHandler.saveIngredient(newIngredient);
        return true;
    }

    public boolean deleteIngredient(int id) {
        Ingredient ingredientToDelete = findIngredientById(id);
        if (ingredientToDelete != null) {
            ingredients.removeElement(ingredientToDelete);
            dbHandler.deleteIngredient(id);
            return true;
        } else {
            return false;
        }
    }

    public boolean changeMacro(int id, int carbs, int fat, int protein, String type) {
        Ingredient ingredientToEdit = findIngredientById(id);
        if (ingredientToEdit != null) {
            ingredientToEdit.setCarbs(carbs);
            ingredientToEdit.setFat(fat);
            ingredientToEdit.setProtein(protein);
            ingredientToEdit.setType(type);

            // Aktualizacja modelu listy
            int index = ingredients.indexOf(ingredientToEdit);
            if (index >= 0) {
                ingredients.setElementAt(ingredientToEdit, index);
            }

            dbHandler.updateIngredient(ingredientToEdit);
            return true;
        } else {
            return false;
        }
    }

    public Ingredient findIngredientById(int id) {
        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ing = ingredients.getElementAt(i);
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
            for (int i = 0; i < ingredients.size(); i++) {
                Ingredient ing = ingredients.getElementAt(i);
                if (ing.getId() == id) {
                    id++;
                    idExists = true;
                    break;
                }
            }
        }
        return id;
    }

    public List<Ingredient> getIngredients() {
        List<Ingredient> list = new ArrayList<>();
        for (int i = 0; i < ingredients.size(); i++) {
            list.add(ingredients.getElementAt(i));
        }
        return list;
    }
}