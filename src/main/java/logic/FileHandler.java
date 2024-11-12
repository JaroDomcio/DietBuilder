package logic;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class FileHandler {
    private String ingredientFilename;
    private String mealFilename;
    private ArrayList<Ingredient> ingredients;
    private List<Meal> meals;

    public FileHandler(String ingredientFilename,String mealFilename) {
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
}