package com.rorschach.zcyislim.DatabaseLocal;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.rorschach.zcyislim.Entity.Exercise;
import com.rorschach.zcyislim.Entity.Food;
import com.rorschach.zcyislim.Entity.FoodType;


@Database(entities = {Exercise.class, Food.class, FoodType.class}, version = 1, exportSchema = false)
public abstract class BaseRoomDatabase extends RoomDatabase {
    public abstract ExerciseDao getExerciseDao();

    public abstract FoodDao getFoodDao();

    public abstract FoodTypeDao getFoodTypeDao();

}
