package logic;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class ShoppingListGenerator {

    private LogicManagerIngredients logicManagerIngredients;
    private LogicManagerMeals logicManagerMeals;

    public ShoppingListGenerator(LogicManagerIngredients logicManagerIngredients, LogicManagerMeals logicManagerMeals) {
        this.logicManagerIngredients = logicManagerIngredients;
        this.logicManagerMeals = logicManagerMeals;
    }

    public void generateShoppingList() {
        List<Meal> selectedMeals = selectMeals();
        if (selectedMeals.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nie wybrano żadnych posiłków.", "Informacja", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Agregacja składników
        Map<String, Map<String, Integer>> categoryToIngredients = new HashMap<>();
        for (Meal meal : selectedMeals) {
            List<Integer> ingredientIds = meal.getIngredientsIds();
            List<Integer> quantities = meal.getQuantities();
            for (int i = 0; i < ingredientIds.size(); i++) {
                int ingredientId = ingredientIds.get(i);
                int quantity = quantities.get(i);

                Ingredient ingredient = logicManagerIngredients.findIngredientById(ingredientId);
                if (ingredient != null) {
                    String category = ingredient.getType();
                    String ingredientName = ingredient.getName();

                    Map<String, Integer> ingredientsInCategory = categoryToIngredients.getOrDefault(category, new HashMap<>());
                    int currentQuantity = ingredientsInCategory.getOrDefault(ingredientName, 0);
                    ingredientsInCategory.put(ingredientName, currentQuantity + quantity);

                    categoryToIngredients.put(category, ingredientsInCategory);
                }
            }
        }

        if (categoryToIngredients.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Brak składników do wygenerowania listy zakupów.", "Informacja", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Wybór lokalizacji zapisu pliku
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Zapisz listę zakupów jako PDF");
        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".pdf")) {
                filePath += ".pdf";
            }
            try {
                createPDF(filePath, categoryToIngredients);
                JOptionPane.showMessageDialog(null, "Lista zakupów została zapisana jako PDF.", "Sukces", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Wystąpił błąd podczas zapisu pliku PDF.", "Błąd", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private List<Meal> selectMeals() {
        List<Meal> meals = logicManagerMeals.getMeals();
        if (meals.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Brak dostępnych posiłków.", "Informacja", JOptionPane.INFORMATION_MESSAGE);
            return Collections.emptyList();
        }

        JPanel panel = new JPanel(new GridLayout(0, 1));
        JCheckBox[] checkBoxes = new JCheckBox[meals.size()];
        for (int i = 0; i < meals.size(); i++) {
            checkBoxes[i] = new JCheckBox(meals.get(i).getName());
            panel.add(checkBoxes[i]);
        }

        int result = JOptionPane.showConfirmDialog(null, new JScrollPane(panel), "Wybierz posiłki", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            List<Meal> selectedMeals = new ArrayList<>();
            for (int i = 0; i < meals.size(); i++) {
                if (checkBoxes[i].isSelected()) {
                    selectedMeals.add(meals.get(i));
                }
            }
            return selectedMeals;
        } else {
            return Collections.emptyList();
        }
    }

    private void createPDF(String filePath, Map<String, Map<String, Integer>> categoryToIngredients) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
        contentStream.beginText();
        contentStream.newLineAtOffset(50, 750);
        contentStream.showText("Lista zakupów");
        contentStream.endText();

        contentStream.setFont(PDType1Font.HELVETICA, 12);
        int yPosition = 720;

        for (String category : categoryToIngredients.keySet()) {
            // Nagłówek kategorii
            contentStream.beginText();
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.showText(category);
            contentStream.endText();
            yPosition -= 20;

            Map<String, Integer> ingredients = categoryToIngredients.get(category);
            for (Map.Entry<String, Integer> entry : ingredients.entrySet()) {
                String ingredientName = entry.getKey();
                int totalQuantity = entry.getValue();

                contentStream.beginText();
                contentStream.newLineAtOffset(70, yPosition);
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.showText(String.format("- %s: %d g", ingredientName, totalQuantity));
                contentStream.endText();
                yPosition -= 20;

                if (yPosition < 50) {
                    contentStream.close();
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    yPosition = 750;
                }
            }

            yPosition -= 10;
        }

        contentStream.close();
        document.save(filePath);
        document.close();
    }
}

