package pwr.main;
import logic.FileHandler;
import logic.Ingredient;
import logic.LogicManager;
import logic.Meal;

import javax.swing.*;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        FileHandler fH = new FileHandler("src/ingredients.txt","src/meals.txt");
        ArrayList<Ingredient> ingredients = fH.loadIngredients();
        LogicManager lG = new LogicManager(ingredients,fH);
        GUI gui = new GUI(lG);
    }
}