package com.rorschach.zcyislim.DatabaseLocal;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.rorschach.zcyislim.Entity.Food;

import java.util.List;


@Dao
public interface FoodDao {
    @Query("select * from t_food where food_type_id==:foodTypeId")
    List<Food> selectAllNoteById(long foodTypeId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertFood(Food food);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long[] insertFoods(List<Food> foods);

}
