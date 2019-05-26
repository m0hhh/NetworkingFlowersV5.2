package com.example.networkingflowersv5;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class RecycleViewFlowerAdapter extends RecyclerView.Adapter<RecycleViewFlowerAdapter.FlowerViewHolder> {

    private List<FlowerModel> mFlowerList = null;
    private Context mContext;
    private Activity mActivity;
    private LayoutInflater mInflater;
    private View mItemView;


    public RecycleViewFlowerAdapter(List<FlowerModel> mFlowerList, Context mContext, Activity mActivity) {

        this.mFlowerList = mFlowerList;
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public FlowerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = mInflater.inflate(R.layout.layout_for_each_flower, viewGroup, false);
        mItemView = view;
        return new FlowerViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull FlowerViewHolder flowerViewHolder, int position) {

        FlowerModel flower = mFlowerList.get(position);
        flowerViewHolder.tvFlowerName.setText(flower.getName());

        Bitmap bitmap = FileSaver.GetFile(mContext, flower);
        if(bitmap != null){

            flower.setBitmap(bitmap);
            flowerViewHolder.ivFlowerImage.setImageBitmap(bitmap);

        }
        else{

            MainActivity.DataSender dataSender = new MainActivity.DataSender(flowerViewHolder.ivFlowerImage, flower);
            new MainActivity.ImageDownloader().execute(dataSender);

        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        Boolean checked = sharedPreferences.getBoolean(mContext.getString(R.string.Key_Enable_Default_Colors),true);
        if(!checked){

            if(position % 2 == 0){
                mItemView.setBackgroundColor(Color.GREEN);
            }
        }

    }

    @Override
    public int getItemCount() {

        if(mFlowerList != null && mFlowerList.size()>0){

            return mFlowerList.size();
        }
        else {
            return 0;
        }

    }

    public class FlowerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvFlowerName;
        private ImageView ivFlowerImage;
        final RecycleViewFlowerAdapter mAdapter;

        public FlowerViewHolder(@NonNull View itemView, RecycleViewFlowerAdapter mAdapter) {
            super(itemView);

            tvFlowerName = itemView.findViewById(R.id.tvFlowerName);
            ivFlowerImage = itemView.findViewById(R.id.ivFlowerImage);
            this.mAdapter = mAdapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            FlowerModel flower = mFlowerList.get(getLayoutPosition());
            Intent intent = new Intent(mContext, DetailActivity.class);
            intent.putExtra(MainActivity.FLOWER_OBJECT, flower);
           mActivity.startActivityForResult(intent, MainActivity.REQUEST_CODE);
           mAdapter.notifyDataSetChanged();

        }
    }

    public FlowerModel getFlowerAtPosition(int position){

        return mFlowerList.get(position);

    }
}
