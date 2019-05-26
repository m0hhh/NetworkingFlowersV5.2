package com.example.networkingflowersv5;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;
@Dao
public interface FlowerDAO {

    //all the sql statements

    @Query("SELECT * FROM FLowers order by productId desc")
    LiveData<List<FlowerModel>>  getAllFlowers();

    @Insert
    void insert(FlowerModel flower);

    @Delete
    void deleteFlower(FlowerModel flower);

}
