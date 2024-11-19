package pwr.main;

import GUI.GUI;
import logic.*;

public class Main {
    public static void main(String[] args) {
        // Sprawdzenie, czy ścieżka została podana
        if (args.length == 0) {
            System.out.println("Podaj ścieżkę do bazy danych jako argument.");
            return;
        }

        // Pobranie ścieżki z argumentu
        String dbPath = args[0];

        // Inicjalizacja aplikacji
        DatabaseHandler dbHandler = new DatabaseHandler(dbPath);
        LogicManagerIngredients lGI = new LogicManagerIngredients(dbHandler);
        LogicManagerMeals lGM = new LogicManagerMeals(dbHandler, lGI);
        new GUI(lGI, lGM);
    }
}

