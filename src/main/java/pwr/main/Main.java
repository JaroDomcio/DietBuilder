package pwr.main;
import GUI.GUI;
import logic.*;

import javax.swing.*;


public class Main {
    public static void main(String[] args) {
        FileHandler fH = new FileHandler("src/ingredients.txt","src/meals.txt");
        DefaultListModel<Ingredient> ingredients = fH.loadIngredients();
        DefaultListModel<Meal> meals = fH.loadMeals();
        LogicManagerIngredients lGI = new LogicManagerIngredients(ingredients,fH);
        LogicManagerMeals lGM = new LogicManagerMeals(meals,fH,lGI);
        new GUI(lGI,lGM);
    }
}