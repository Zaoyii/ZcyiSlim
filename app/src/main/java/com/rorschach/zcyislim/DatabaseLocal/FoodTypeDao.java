package com.rorschach.zcyislim.DatabaseLocal;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.rorschach.zcyislim.Entity.FoodType;

import java.util.List;


@Dao
public interface FoodTypeDao {
    @Query("select * from t_food_type")
    List<FoodType> selectAllFoodType();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertNote(FoodType notes);

}
