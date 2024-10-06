package com.rorschach.zcyislim.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "t_food_type")
public class FoodType implements Serializable {
    private static final long serialVersionUID = 9271L;
    @PrimaryKey()
    @ColumnInfo(name = "food_type_id")
    long foodTypeId;
    @ColumnInfo(name = "food_type_name")
    String foodTypeName;
    @ColumnInfo(name = "food_type_pinyin")
    String foodTypePinyin;
    @ColumnInfo(name = "food_type_img")
    String foodTypeImg;
    @ColumnInfo(name = "food_type_create_time")
    String foodTypeCreateTime;

    public FoodType() {
    }

    public long getFoodTypeId() {
        return foodTypeId;
    }

    public void setFoodTypeId(long foodTypeId) {
        this.foodTypeId = foodTypeId;
    }

    public String getFoodTypeName() {
        return foodTypeName;
    }

    public void setFoodTypeName(String foodTypeName) {
        this.foodTypeName = foodTypeName;
    }

    public String getFoodTypePinyin() {
        return foodTypePinyin;
    }

    public void setFoodTypePinyin(String foodTypePinyin) {
        this.foodTypePinyin = foodTypePinyin;
    }

    public String getFoodTypeImg() {
        return foodTypeImg;
    }

    public void setFoodTypeImg(String foodTypeImg) {
        this.foodTypeImg = foodTypeImg;
    }

    public String getFoodTypeCreateTime() {
        return foodTypeCreateTime;
    }

    public void setFoodTypeCreateTime(String foodTypeCreateTime) {
        this.foodTypeCreateTime = foodTypeCreateTime;
    }

    @Override
    public String toString() {
        return "FoodType{" +
                "foodTypeId=" + foodTypeId +
                ", foodTypeName='" + foodTypeName + '\'' +
                ", foodTypePinyin='" + foodTypePinyin + '\'' +
                ", foodTypeImg='" + foodTypeImg + '\'' +
                ", foodTypeCreateTime='" + foodTypeCreateTime + '\'' +
                '}';
    }
}
