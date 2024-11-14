package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import logic.*;

public class GUI {
    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private LogicManagerIngredients logicManagerIngredients;
    private LogicManagerMeals logicManagerMeals;
    private JList<Meal> mealsList;
    private JList<Ingredient> ingredientsList;

    public GUI(LogicManagerIngredients logicManagerIngredients, LogicManagerMeals logicManagerMeals) {
        this.logicManagerIngredients = logicManagerIngredients;
        this.logicManagerMeals = logicManagerMeals;

        frame = new JFrame("Diet Builder");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        JPanel homePanel = createHomePanel();
        JPanel ingredientsPanel = new IngredientsOptionsGUI(logicManagerIngredients, logicManagerMeals, cardLayout, mainPanel, this).getPanel();
        JPanel mealsPanel = new MealsOptionsGUI(logicManagerMeals, logicManagerIngredients, cardLayout, mainPanel, this).getPanel();

        mainPanel.add(homePanel, "Home");
        mainPanel.add(ingredientsPanel, "Ingredients");
        mainPanel.add(mealsPanel, "Meals");

        frame.add(mainPanel);

        cardLayout.show(mainPanel, "Home");

        frame.setVisible(true);
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel listPanel = new JPanel(new GridLayout(1, 2));

        mealsList = new JList<>(logicManagerMeals.getMealListData());
        mealsList.setBorder(BorderFactory.createTitledBorder("Posiłki"));
        listPanel.add(new JScrollPane(mealsList));

        mealsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = mealsList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        Meal selectedMeal = mealsList.getModel().getElementAt(index);
                        String mealInfo = logicManagerMeals.getMealIngredientsInfo(selectedMeal);
                        JOptionPane.showMessageDialog(frame, mealInfo, "Składniki Posiłku", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });

        ingredientsList = new JList<>(logicManagerIngredients.getIngredientListData());
        ingredientsList.setBorder(BorderFactory.createTitledBorder("Składniki"));
        listPanel.add(new JScrollPane(ingredientsList));

        ingredientsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = ingredientsList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        Ingredient selectedIngredient = ingredientsList.getModel().getElementAt(index);
                        String ingredientInfo = String.format(
                                "Nazwa: %s\nWęglowodany: %d g\nBiałko: %d g\nTłuszcz: %d g\nKalorie: %d kcal\nTyp: %s",
                                selectedIngredient.getName(),
                                selectedIngredient.getCarbs(),
                                selectedIngredient.getProtein(),
                                selectedIngredient.getFat(),
                                selectedIngredient.getCalories(),
                                selectedIngredient.getType()
                        );
                        JOptionPane.showMessageDialog(frame, ingredientInfo, "Makroskładniki Składnika", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });

        panel.add(listPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton mealsButton = new JButton("Posiłki");
        JButton ingredientsButton = new JButton("Składniki");
        JButton shoppingListButton = new JButton("Wygeneruj listę zakupów");

        buttonPanel.add(mealsButton);
        buttonPanel.add(shoppingListButton);
        buttonPanel.add(ingredientsButton);

        panel.add(buttonPanel, BorderLayout.NORTH);

        mealsButton.addActionListener(e -> cardLayout.show(mainPanel, "Meals"));
        ingredientsButton.addActionListener(e -> cardLayout.show(mainPanel, "Ingredients"));
        shoppingListButton.addActionListener(e -> {
            ShoppingListGenerator shoppingListGenerator = new ShoppingListGenerator(logicManagerIngredients, logicManagerMeals);
            shoppingListGenerator.generateShoppingList();
        });


        return panel;
    }

}
