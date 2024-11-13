package logic;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;

public class FileHandler {
    private String ingredientFilename;
    private String mealFilename;
    private ArrayList<Ingredient> ingredients;
    private List<Meal> meals;

    public FileHandler(String ingredientFilename, String mealFilename) {
        this.ingredientFilename = ingredientFilename;
        this.mealFilename = mealFilename;
        this.ingredients = new ArrayList<>();
        this.meals = new ArrayList<>();
    }

    public ArrayList<Ingredient> loadIngredients() {
        // Wczytuje składniki z pliku do listy
        try (BufferedReader br = new BufferedReader(new FileReader(ingredientFilename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                if (data.length == 6) {
                    Ingredient ingredient = new Ingredient(
                            Integer.parseInt(data[0]),
                            data[1],
                            Integer.parseInt(data[2]),
                            Integer.parseInt(data[3]),
                            Integer.parseInt(data[4]),
                            data[5]
                    );
                    ingredients.add(ingredient);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Plik " + ingredientFilename + " nie został znaleziony.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ingredients;
    }

    public void saveIngredients(ArrayList<Ingredient> ingredients) {
        // Zapisuje składniki z listy do pliku
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ingredientFilename))) {
            for (Ingredient ingredient : ingredients) {
                bw.write(formatForFile(ingredient));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String formatForFile(Ingredient ingredient) {
        return ingredient.getId() + ";" + ingredient.getName() + ";" + ingredient.getCarbs() + ";" +
                ingredient.getFat() + ";" + ingredient.getProtein() + ";" + ingredient.getType();
    }

    // Metoda do wczytywania posiłków z pliku
    public ArrayList<Meal> loadMeals() {
        ArrayList<Meal> loadedMeals = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(mealFilename))) {
            String line;
            while ((line = br.readLine()) != null) {

                String[] data = line.split(";");
                if (data.length == 4) {
                    int mealId = Integer.parseInt(data[0]);
                    String mealName = data[1];

                    Meal meal = new Meal(mealId, mealName);

                    // Wczytanie id składników
                    String[] ingredientIds = data[2].split(",");
                    for (String ingredientId : ingredientIds) {
                        meal.addIngredient(Integer.parseInt(ingredientId));
                    }

                    // Wczytanie ilości składników
                    String[] quantities = data[3].split(",");
                    for (String quantity : quantities) {
                        meal.addQuantity(Integer.parseInt(quantity));
                    }

                    loadedMeals.add(meal);

                    // Debug: Wyświetla szczegóły wczytanego posiłku
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Plik " + mealFilename + " nie został znaleziony.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loadedMeals;
    }


    // Metoda do zapisywania posiłków do pliku
    public void saveMeals(ArrayList<Meal> meals) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(mealFilename))) {
            for (Meal meal : meals) {
                bw.write(formatForFile(meal));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String formatForFile(Meal meal) {
        // Formatuje posiłek do zapisu w pliku: id;nazwa;id1,id2,...;quantity1,quantity2,...
        String ingredientIds = meal.getIngredientsIds().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        String quantities = meal.getQuantities().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        return meal.getId() + ";" + meal.getName() + ";" + ingredientIds + ";" + quantities;
    }
}
