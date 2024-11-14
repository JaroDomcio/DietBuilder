package logic;

import javax.swing.DefaultListModel;
import java.io.*;
import java.util.stream.Collectors;

public class FileHandler {
    private String ingredientFilename;
    private String mealFilename;

    public FileHandler(String ingredientFilename, String mealFilename) {
        this.ingredientFilename = ingredientFilename;
        this.mealFilename = mealFilename;
    }

    public DefaultListModel<Ingredient> loadIngredients() {
        DefaultListModel<Ingredient> ingredients = new DefaultListModel<>();
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
                    ingredients.addElement(ingredient);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Plik " + ingredientFilename + " nie został znaleziony.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ingredients;
    }

    public void saveIngredients(DefaultListModel<Ingredient> ingredients) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ingredientFilename))) {
            for (int i = 0; i < ingredients.size(); i++) {
                Ingredient ingredient = ingredients.getElementAt(i);
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

    public DefaultListModel<Meal> loadMeals() {
        DefaultListModel<Meal> loadedMeals = new DefaultListModel<>();
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

                    loadedMeals.addElement(meal);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Plik " + mealFilename + " nie został znaleziony.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loadedMeals;
    }

    public void saveMeals(DefaultListModel<Meal> meals) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(mealFilename))) {
            for (int i = 0; i < meals.size(); i++) {
                Meal meal = meals.getElementAt(i);
                bw.write(formatForFile(meal));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String formatForFile(Meal meal) {
        String ingredientIds = meal.getIngredientsIds().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        String quantities = meal.getQuantities().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        return meal.getId() + ";" + meal.getName() + ";" + ingredientIds + ";" + quantities;
    }
}

