package com.example.networkingflowersv5;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;


//Creating the Database
@Database(entities = {FlowerModel.class}, version = 1, exportSchema = false)
public abstract class FlowerDB extends RoomDatabase {

    public abstract FlowerDAO flowerDao();

    private static volatile FlowerDB INSTANCE;

    static FlowerDB getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (FlowerDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            FlowerDB.class, "flower_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback(){

                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db){
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final FlowerDAO mDao;

        PopulateDbAsync(FlowerDB db) {
            mDao = db.flowerDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {

            return null;
        }
    }
}
