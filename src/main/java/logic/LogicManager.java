package logic;

import java.util.ArrayList;

public class LogicManager {
    private FileHandler fH ;
    private ArrayList<Ingredient> ingredients;
    private ArrayList<Meal> Meals;

    public LogicManager(ArrayList<Ingredient> ingredients, FileHandler fH){
        this.ingredients = ingredients;
        this.fH = fH;
    }

    public void addIngredient(String name, int carbs, int fat, int protein, String type) {
        int id = findLowestAvailableIngredientId();
        Ingredient newIngredient = new Ingredient(id, name, carbs, fat, protein, type);
        System.out.println(newIngredient);
        ingredients.add(newIngredient);
        System.out.println(ingredients);
        fH.saveIngredients(ingredients);
    }
    public void deleteIngredient(){

    }
    public void displayMacro(){
        for (Ingredient ing : ingredients){
            System.out.println(ing);
        }
    }
    public void changeMacro(int id){
        Ingredient choosenIngredient = findIngredientById(id);

    }

    public void endProgram(){
        fH.saveIngredients(ingredients);
    }
    public ArrayList<Ingredient> returnIngredient(){
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

        // Pętla działa dopóki istnieje pracownik z danym id
        while (idExists) {
            idExists = false; // Zakładamy, że ID jest wolne
            for (Ingredient ing : ingredients) {
                if (ing.getId() == id) { // Jeśli ID jest zajęte
                    id++; // Przechodzimy do kolejnego ID
                    idExists = true; // Flaga ustawiona, kontynuujemy szukanie
                    break;
                }
            }
        }
        return id; // Zwracamy najmniejsze wolne ID
    }

}

