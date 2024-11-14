package logic;

import javax.swing.DefaultListModel;
import java.util.ArrayList;
import java.util.List;

public class LogicManagerIngredients {
    private FileHandler fH;
    private DefaultListModel<Ingredient> ingredients;

    public LogicManagerIngredients(DefaultListModel<Ingredient> ingredients, FileHandler fH) {
        this.ingredients = ingredients;
        this.fH = fH;
    }

    public DefaultListModel<Ingredient> getIngredientListData() {
        return ingredients;
    }

    public void addIngredient(String name, int carbs, int fat, int protein, String type) {
        int id = findLowestAvailableIngredientId();
        Ingredient newIngredient = new Ingredient(id, name, carbs, fat, protein, type);
        ingredients.addElement(newIngredient);
        fH.saveIngredients(ingredients);
    }

    public void deleteIngredient(int id) {
        Ingredient ingredientToDelete = findIngredientById(id);
        if (ingredientToDelete != null) {
            ingredients.removeElement(ingredientToDelete);
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
            if (index >= 0) {
                ingredients.setElementAt(ingredientToEdit, index);
            }

            fH.saveIngredients(ingredients);
        } else {
            System.out.println("Składnik o podanym ID nie został znaleziony.");
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







