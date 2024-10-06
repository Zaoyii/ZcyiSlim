package com.rorschach.zcyislim.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "t_food")
public class Food implements Serializable {
    private static final long serialVersionUID = 9270L;
    @PrimaryKey()
    @ColumnInfo(name = "food_id")
    long foodId;
    @ColumnInfo(name = "food_name")
    String foodName;
    @ColumnInfo(name = "food_image")
    String foodImage;
    @ColumnInfo(name = "food_type_id")
    long foodTypeId;
    @ColumnInfo(name = "food_calories")
    String foodCalories;
    @ColumnInfo(name = "food_carbohydrate")
    String foodCarbohydrate;
    @ColumnInfo(name = "food_protein")
    String foodProtein;
    @ColumnInfo(name = "food_fat")
    String foodFat;
    @ColumnInfo(name = "food_create_time")
    String foodCreateTime;

    public Food() {
    }

    public long getFoodId() {
        return foodId;
    }

    public void setFoodId(long foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodImage() {
        return foodImage;
    }

    public void setFoodImage(String foodImage) {
        this.foodImage = foodImage;
    }

    public long getFoodTypeId() {
        return foodTypeId;
    }

    public void setFoodTypeId(long foodTypeId) {
        this.foodTypeId = foodTypeId;
    }

    public String getFoodCalories() {
        return foodCalories;
    }

    public void setFoodCalories(String foodCalories) {
        this.foodCalories = foodCalories;
    }

    public String getFoodCarbohydrate() {
        return foodCarbohydrate;
    }

    public void setFoodCarbohydrate(String foodCarbohydrate) {
        this.foodCarbohydrate = foodCarbohydrate;
    }

    public String getFoodProtein() {
        return foodProtein;
    }

    public void setFoodProtein(String foodProtein) {
        this.foodProtein = foodProtein;
    }

    public String getFoodFat() {
        return foodFat;
    }

    public void setFoodFat(String foodFat) {
        this.foodFat = foodFat;
    }

    public String getFoodCreateTime() {
        return foodCreateTime;
    }

    public void setFoodCreateTime(String foodCreateTime) {
        this.foodCreateTime = foodCreateTime;
    }

    @Override
    public String toString() {
        return "Food{" +
                "foodId=" + foodId +
                ", foodName='" + foodName + '\'' +
                ", foodImage='" + foodImage + '\'' +
                ", foodTypeId=" + foodTypeId +
                ", foodCalories='" + foodCalories + '\'' +
                ", foodCarbohydrate='" + foodCarbohydrate + '\'' +
                ", foodProtein='" + foodProtein + '\'' +
                ", foodFat='" + foodFat + '\'' +
                ", foodCreateTime='" + foodCreateTime + '\'' +
                '}';
    }
}
