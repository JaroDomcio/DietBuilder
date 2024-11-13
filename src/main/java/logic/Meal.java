package logic;

import java.util.ArrayList;

public class Meal {

    private int id;
    private String name;
    private ArrayList<Integer> ingredientsIds = new ArrayList<>();
    private ArrayList<Integer> quantities = new ArrayList<>();

    public Meal (int id, String name){
        this.id = id;
        this.name = name;
    }

    public void addIngredient(int id){
        this.ingredientsIds.add(id);
    }

    public void addQuantity(int quantity){
        this.quantities.add(quantity);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Integer> getIngredientsIds() {
        return ingredientsIds;
    }
    public ArrayList<Integer> getQuantities() {
        return quantities;
    }

    public void setIngredientsIds(ArrayList<Integer> ingredientsIds) {
        this.ingredientsIds = ingredientsIds;
    }

    public void setQuantities(ArrayList<Integer> quantities) {
        this.quantities = quantities;
    }

    @Override
    public String toString() {
        return name ;
    }
}
