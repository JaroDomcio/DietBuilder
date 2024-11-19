package logic;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;

public class DatabaseHandler {
    private String url = "jdbc:sqlite:dietbuilder.db";

    public DatabaseHandler() {
        createTables();
    }

    private void createTables() {
        String createIngredientsTable = "CREATE TABLE IF NOT EXISTS ingredients (\n"
                + " id INTEGER PRIMARY KEY,\n"
                + " name TEXT NOT NULL,\n"
                + " carbs INTEGER,\n"
                + " fat INTEGER,\n"
                + " protein INTEGER,\n"
                + " type TEXT\n"
                + ");";

        String createMealsTable = "CREATE TABLE IF NOT EXISTS meals (\n"
                + " id INTEGER PRIMARY KEY,\n"
                + " name TEXT NOT NULL\n"
                + ");";

        String createMealIngredientsTable = "CREATE TABLE IF NOT EXISTS meal_ingredients (\n"
                + " meal_id INTEGER,\n"
                + " ingredient_id INTEGER,\n"
                + " quantity INTEGER,\n"
                + " FOREIGN KEY(meal_id) REFERENCES meals(id),\n"
                + " FOREIGN KEY(ingredient_id) REFERENCES ingredients(id),\n"
                + " PRIMARY KEY (meal_id, ingredient_id)\n"
                + ");";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createIngredientsTable);
            stmt.execute(createMealsTable);
            stmt.execute(createMealIngredientsTable);
        } catch (SQLException e) {
            System.out.println("Błąd podczas tworzenia tabel: " + e.getMessage());
        }
    }

    // Metody do obsługi składników

    public DefaultListModel<Ingredient> loadIngredients() {
        DefaultListModel<Ingredient> ingredients = new DefaultListModel<>();
        String sql = "SELECT * FROM ingredients";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Ingredient ingredient = new Ingredient(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("carbs"),
                        rs.getInt("fat"),
                        rs.getInt("protein"),
                        rs.getString("type")
                );
                ingredients.addElement(ingredient);
            }
        } catch (SQLException e) {
            System.out.println("Błąd podczas wczytywania składników: " + e.getMessage());
        }
        return ingredients;
    }

    public void saveIngredient(Ingredient ingredient) {
        String sql = "INSERT INTO ingredients(id, name, carbs, fat, protein, type) VALUES(?,?,?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ingredient.getId());
            pstmt.setString(2, ingredient.getName());
            pstmt.setInt(3, ingredient.getCarbs());
            pstmt.setInt(4, ingredient.getFat());
            pstmt.setInt(5, ingredient.getProtein());
            pstmt.setString(6, ingredient.getType());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Błąd podczas zapisywania składnika: " + e.getMessage());
        }
    }

    public void updateIngredient(Ingredient ingredient) {
        String sql = "UPDATE ingredients SET carbs = ?, fat = ?, protein = ?, type = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ingredient.getCarbs());
            pstmt.setInt(2, ingredient.getFat());
            pstmt.setInt(3, ingredient.getProtein());
            pstmt.setString(4, ingredient.getType());
            pstmt.setInt(5, ingredient.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Błąd podczas aktualizacji składnika: " + e.getMessage());
        }
    }

    public void deleteIngredient(int ingredientId) {
        String sql = "DELETE FROM ingredients WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ingredientId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Błąd podczas usuwania składnika: " + e.getMessage());
        }
    }

    // Metody do obsługi posiłków

    public DefaultListModel<Meal> loadMeals() {
        DefaultListModel<Meal> meals = new DefaultListModel<>();
        String sqlMeals = "SELECT * FROM meals";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmtMeals = conn.createStatement();
             ResultSet rsMeals = stmtMeals.executeQuery(sqlMeals)) {

            while (rsMeals.next()) {
                int mealId = rsMeals.getInt("id");
                String mealName = rsMeals.getString("name");

                Meal meal = new Meal(mealId, mealName);

                // Wczytaj składniki dla danego posiłku
                String sqlIngredients = "SELECT * FROM meal_ingredients WHERE meal_id = ?";
                try (PreparedStatement pstmtIngredients = conn.prepareStatement(sqlIngredients)) {
                    pstmtIngredients.setInt(1, mealId);
                    ResultSet rsIngredients = pstmtIngredients.executeQuery();

                    while (rsIngredients.next()) {
                        meal.addIngredient(rsIngredients.getInt("ingredient_id"));
                        meal.addQuantity(rsIngredients.getInt("quantity"));
                    }
                }

                meals.addElement(meal);
            }
        } catch (SQLException e) {
            System.out.println("Błąd podczas wczytywania posiłków: " + e.getMessage());
        }
        return meals;
    }

    public void saveMeal(Meal meal) {
        String sqlMeal = "INSERT INTO meals(id, name) VALUES(?,?)";
        String sqlMealIngredient = "INSERT INTO meal_ingredients(meal_id, ingredient_id, quantity) VALUES(?,?,?)";

        try (Connection conn = DriverManager.getConnection(url)) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtMeal = conn.prepareStatement(sqlMeal);
                 PreparedStatement pstmtMealIngredient = conn.prepareStatement(sqlMealIngredient)) {

                // Zapisz posiłek
                pstmtMeal.setInt(1, meal.getId());
                pstmtMeal.setString(2, meal.getName());
                pstmtMeal.executeUpdate();

                // Zapisz składniki posiłku
                List<Integer> ingredientIds = meal.getIngredientsIds();
                List<Integer> quantities = meal.getQuantities();

                for (int i = 0; i < ingredientIds.size(); i++) {
                    pstmtMealIngredient.setInt(1, meal.getId());
                    pstmtMealIngredient.setInt(2, ingredientIds.get(i));
                    pstmtMealIngredient.setInt(3, quantities.get(i));
                    pstmtMealIngredient.executeUpdate();
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Błąd podczas zapisywania posiłku: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Błąd podczas zapisywania posiłku: " + e.getMessage());
        }
    }

    public void updateMeal(Meal meal) {
        String sqlUpdateMeal = "UPDATE meals SET name = ? WHERE id = ?";
        String sqlDeleteIngredients = "DELETE FROM meal_ingredients WHERE meal_id = ?";
        String sqlInsertIngredients = "INSERT INTO meal_ingredients(meal_id, ingredient_id, quantity) VALUES(?,?,?)";

        try (Connection conn = DriverManager.getConnection(url)) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtUpdateMeal = conn.prepareStatement(sqlUpdateMeal);
                 PreparedStatement pstmtDeleteIngredients = conn.prepareStatement(sqlDeleteIngredients);
                 PreparedStatement pstmtInsertIngredients = conn.prepareStatement(sqlInsertIngredients)) {

                // Aktualizuj nazwę posiłku
                pstmtUpdateMeal.setString(1, meal.getName());
                pstmtUpdateMeal.setInt(2, meal.getId());
                pstmtUpdateMeal.executeUpdate();

                // Usuń stare składniki posiłku
                pstmtDeleteIngredients.setInt(1, meal.getId());
                pstmtDeleteIngredients.executeUpdate();

                // Dodaj nowe składniki posiłku
                List<Integer> ingredientIds = meal.getIngredientsIds();
                List<Integer> quantities = meal.getQuantities();

                for (int i = 0; i < ingredientIds.size(); i++) {
                    pstmtInsertIngredients.setInt(1, meal.getId());
                    pstmtInsertIngredients.setInt(2, ingredientIds.get(i));
                    pstmtInsertIngredients.setInt(3, quantities.get(i));
                    pstmtInsertIngredients.executeUpdate();
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Błąd podczas aktualizacji posiłku: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Błąd podczas aktualizacji posiłku: " + e.getMessage());
        }
    }

    public void deleteMeal(int mealId) {
        String sqlDeleteMeal = "DELETE FROM meals WHERE id = ?";
        String sqlDeleteIngredients = "DELETE FROM meal_ingredients WHERE meal_id = ?";

        try (Connection conn = DriverManager.getConnection(url)) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtDeleteIngredients = conn.prepareStatement(sqlDeleteIngredients);
                 PreparedStatement pstmtDeleteMeal = conn.prepareStatement(sqlDeleteMeal)) {

                // Usuń składniki posiłku
                pstmtDeleteIngredients.setInt(1, mealId);
                pstmtDeleteIngredients.executeUpdate();

                // Usuń posiłek
                pstmtDeleteMeal.setInt(1, mealId);
                pstmtDeleteMeal.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Błąd podczas usuwania posiłku: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Błąd podczas usuwania posiłku: " + e.getMessage());
        }
    }
}
