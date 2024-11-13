package logic;

public class Ingredient {

    private int id;
    private String name;
    private int carbs;
    private int fat;
    private int protein;
    private String type;

    public Ingredient(int id, String name, int carbs, int fat, int protein, String type){
        this.id = id;
        this.name = name;
        this.carbs = carbs;
        this.fat = fat;
        this.protein = protein;
        this.type = type;

    }

    public int getId() {
        return id;
    }

    public String getName(){
        return name;
    }

    public int getCarbs() {
        return carbs;
    }

    public int getFat() {
        return fat;
    }

    public int getProtein() {
        return protein;
    }

    public String getType() {
        return type;
    }

    public int getCalories(){
        return carbs*4+fat*9+protein*4;
    }

    public void setCarbs(int carbs) {
        this.carbs = carbs;
    }

    public void setFat(int fat) {
        this.fat = fat;
    }

    public void setProtein(int protein) {
        this.protein = protein;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return name;
    }
}
