package com.example.networkingflowersv5;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class FlowerRepository {

    private LiveData<List<FlowerModel>> mAllFlowers;
    private FlowerDAO mFlowerDao;

    public FlowerRepository(Application application) {

        //create a connection to the database
        FlowerDB db = FlowerDB.getDatabase(application.getApplicationContext());

        mFlowerDao = db.flowerDao();
        mAllFlowers = mFlowerDao.getAllFlowers();

    }

    public LiveData<List<FlowerModel>> getAllFlowers() {
        return mAllFlowers;
    }

    //this is done in the background
    void insert(FlowerModel flower){

        new insertAsyncTask(mFlowerDao).execute(flower);
    }

    void delete(FlowerModel flower){

        new deleteAsyncTask(mFlowerDao).execute(flower);
    }

    private static class insertAsyncTask extends AsyncTask<FlowerModel, Void, Void> {

        private FlowerDAO mAsyncTaskDao;

        insertAsyncTask(FlowerDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final FlowerModel... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<FlowerModel, Void, Void> {

        private FlowerDAO mAsyncTaskDao;

        deleteAsyncTask(FlowerDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final FlowerModel... params) {
            mAsyncTaskDao.deleteFlower(params[0]);
            return null;
        }
    }

}
