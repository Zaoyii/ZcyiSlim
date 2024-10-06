package com.rorschach.zcyislim.DatabaseLocal;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.rorschach.zcyislim.Entity.Exercise;

import java.util.List;


@Dao
public interface ExerciseDao {
    @Query("select * from t_exercise where exercise_user_id==:userId")
    List<Exercise> selectAllNoteById(long userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertNote(Exercise notes);

}
