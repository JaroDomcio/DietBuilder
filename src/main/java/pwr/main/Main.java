package pwr.main;

import GUI.GUI;
import logic.*;

public class Main {
    public static void main(String[] args) {
        DatabaseHandler dbHandler = new DatabaseHandler();
        LogicManagerIngredients lGI = new LogicManagerIngredients(dbHandler);
        LogicManagerMeals lGM = new LogicManagerMeals(dbHandler, lGI);
        new GUI(lGI, lGM);
    }
}
