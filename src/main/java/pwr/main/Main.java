package pwr.main;
import GUI.GUI;
import logic.FileHandler;
import logic.Ingredient;
import logic.Meal;
import logic.LogicManagerMeals;
import logic.LogicManagerIngredients;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        FileHandler fH = new FileHandler("src/ingredients.txt","src/meals.txt");
        ArrayList<Ingredient> ingredients = fH.loadIngredients();
        ArrayList<Meal> meals = fH.loadMeals();
        LogicManagerIngredients lGI = new LogicManagerIngredients(ingredients,fH);
        LogicManagerMeals lGM = new LogicManagerMeals(meals,fH,lGI);
        new GUI(lGI,lGM);
    }
}