package com.example.networkingflowersv5;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class FlowerViewModel extends AndroidViewModel {

    private FlowerRepository mRepository;

    private LiveData<List<FlowerModel>> mAllFlowers;

    public FlowerViewModel (Application application) {
        super(application);
        mRepository = new FlowerRepository(application);
        mAllFlowers = mRepository.getAllFlowers();
    }

    LiveData<List<FlowerModel>> getAllFlowers() { return mAllFlowers; }

    public void insert(FlowerModel flower) { mRepository.insert(flower); }

    public void delete(FlowerModel flower) { mRepository.delete(flower); }

}
