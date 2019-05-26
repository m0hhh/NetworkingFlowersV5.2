package com.example.networkingflowersv5;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String FLOWER_OBJECT = "FlowerObject";
    private static final String URL_JSON = "http://services.hanselandpetal.com/feeds/flowers.json";
    private static final String PHOTOS_BASE_URL = "http://services.hanselandpetal.com/photos/";
    ProgressBar progressBar;
    private Map<String, Bitmap> mMap;
    public static final int REQUEST_CODE = 100;
    private FlowerViewModel mFlowerViewModel;
    private List<FlowerModel> mFlowerModelList = null;
    private static Context mContext;
    private RecyclerView mRecyclerView;
    private RecycleViewFlowerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.AppTitle);

        mMap = new HashMap<>();
        mContext = MainActivity.this;

        progressBar = findViewById(R.id.progressbar);

//        if(NetManager.isOnline(this)){
//
//           // Toast.makeText(this, "Your are Online", Toast.LENGTH_SHORT).show();
//            FlowerDownloader flowerDownloader = new FlowerDownloader();
//            flowerDownloader.execute(NetManager.getURL(URL_JSON));
//
//
//        }
//        else{
//            Toast.makeText(this, "Your are Offline", Toast.LENGTH_SHORT).show();
//        }

        mFlowerViewModel = ViewModelProviders.of(this).get(FlowerViewModel.class);
        mFlowerViewModel.getAllFlowers().observe(this, new Observer<List<FlowerModel>>() {
            @Override
            public void onChanged(@Nullable final List<FlowerModel> FlowerModelList) {
                // Update the cached copy of the words in the adapter.
               // adapter.setWords(words);
                if(FlowerModelList != null && FlowerModelList.size() > 0){
                    Toast.makeText(MainActivity.this, "Reading from DB " + FlowerModelList.size() + " items", Toast.LENGTH_SHORT).show();
                    updateDisplay(FlowerModelList);

                    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                        @Override
                        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                            return false;
                        }

                        @Override
                        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                            FlowerModel flower = mAdapter.getFlowerAtPosition(viewHolder.getAdapterPosition());
                            mFlowerViewModel.delete(flower);
                        }
                    });

                    itemTouchHelper.attachToRecyclerView(mRecyclerView);

                }
                else{
                    Toast.makeText(MainActivity.this, "FlowerModelList is null or 0", Toast.LENGTH_SHORT).show();

                    DownloadDataFromInternet();
                }
            }

            private void DownloadDataFromInternet() {
                if(NetManager.isOnline(MainActivity.this)){

                    // Toast.makeText(this, "Your are Online", Toast.LENGTH_SHORT).show();
                    FlowerDownloader flowerDownloader = new FlowerDownloader();
                    flowerDownloader.execute(NetManager.getURL(URL_JSON));


                }
                else{
                    Toast.makeText(MainActivity.this, "Your are Offline", Toast.LENGTH_SHORT).show();
                }
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, InsertUpdateActivity.class);
                startActivityForResult(intent, 55);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE && resultCode == DetailActivity.RESULT_CODE){

            assert data != null;
            Toast.makeText(this, "Thank you for purchasing " + data.getStringExtra(DetailActivity.FLOWER_NAME), Toast.LENGTH_LONG).show();

        }
    }

    static class ImageDownloader extends AsyncTask<DataSender, Void, Bitmap>{

        DataSender dataSender;

        @Override
        protected Bitmap doInBackground(DataSender... dataSenders) {

            dataSender = dataSenders[0];

                URL url = NetManager.getURL(PHOTOS_BASE_URL + dataSender.flowerModel.getPhoto());

                try {

                    InputStream inputStream = (InputStream) new URL(url.toString()).getContent();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    return bitmap;

                } catch (IOException e) {
                    e.printStackTrace();
                }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            dataSender.imageView.setImageBitmap(bitmap);
            dataSender.flowerModel.setBitmap(bitmap);
            // mMap.put(dataSender.flowerModel.getPhoto(), bitmap);
            FileSaver.SaveFile(mContext, dataSender.flowerModel);


        }
    }

    private class FlowerDownloader extends AsyncTask<URL, Void, List<FlowerModel>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<FlowerModel> doInBackground(URL... urls) {

            List<FlowerModel> flowerModelList = Parser.parseJson(NetManager.fetchData(urls[0]));

//            for(FlowerModel flower:flowerModelList){
//                URL url = NetManager.getURL(PHOTOS_BASE_URL + flower.getPhoto());
//
//                try {
//                    InputStream inputStream = (InputStream) new URL(url.toString()).getContent();
//                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                    flower.setBitmap(bitmap);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }

            return flowerModelList;
        }

        @Override
        protected void onPostExecute(List<FlowerModel> flowerModels) {
            super.onPostExecute(flowerModels);

           //testing reading data
          //  Toast.makeText(getApplicationContext(), Parser.text, Toast.LENGTH_LONG);

            if(flowerModels != null){
                progressBar.setVisibility(View.INVISIBLE);
                //filling the database
                mFlowerModelList = flowerModels;
                for(FlowerModel flower : mFlowerModelList){
                    Log.v("FlowerRoomDB", "Inserting: " + flower.getProductId());
                    mFlowerViewModel.insert(flower);
                }
                updateDisplay(flowerModels);

            }
            else{
                Toast.makeText(MainActivity.this, "FlowerModels is null", Toast.LENGTH_SHORT).show();
            }
        }
    }

//    private void updateDisplay(final List<FlowerModel> flowerModelList){
//        ListView lstFlowers = findViewById(R.id.lstFlowers);
//        ArrayAdapter<FlowerModel> adapter = new DisplayFlowers(this, 0, flowerModelList);
//        lstFlowers.setAdapter(adapter);
//
//        lstFlowers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                FlowerModel flower = flowerModelList.get(i);
//
//                RedirectToDetail(flower);
//
//            }
//        });
//
//
//    }

    private void RedirectToDetail(FlowerModel flower) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(FLOWER_OBJECT, flower);
        startActivityForResult(intent, REQUEST_CODE);
    }

    private void updateDisplay(final List<FlowerModel> flowerModelList){

        mRecyclerView = findViewById(R.id.recyclerview);
        //create an adapter and supply the data to be displayed
        mAdapter = new RecycleViewFlowerAdapter(flowerModelList, MainActivity.this, this);
        //connect the adapter with the recycler view
        mRecyclerView.setAdapter(mAdapter);
        //give the recycler view a default layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private class DisplayFlowers extends ArrayAdapter<FlowerModel>{

        List<FlowerModel> listofFlowerObjects;

        public DisplayFlowers(Context context, int resource, List<FlowerModel> objects) {
            super(context, resource, objects);
            this.listofFlowerObjects = objects;

        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_for_each_flower, parent, false);

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

            Boolean checked = sharedPreferences.getBoolean(getString(R.string.Key_Enable_Default_Colors),true);
            if(!checked){

                if(position % 2 == 0){
                    convertView.setBackgroundColor(Color.GREEN);
                }
            }

            FlowerModel flower = listofFlowerObjects.get(position);

            TextView tvFlowerName = convertView.findViewById(R.id.tvFlowerName);
            tvFlowerName.setText(flower.getName());

            ImageView ivFlowerImage = convertView.findViewById(R.id.ivFlowerImage);
           // ivFlowerImage.setImageBitmap(flower.getBitmap());


           // if(mMap.containsKey(flower.getPhoto())){
            Bitmap bitmap = FileSaver.GetFile(MainActivity.this, flower);
            flower.setBitmap(bitmap);

            if(bitmap != null){
               // ivFlowerImage.setImageBitmap(mMap.get(flower.getPhoto()));
                ivFlowerImage.setImageBitmap(bitmap);

            }else{

                DataSender dataSender = new DataSender(ivFlowerImage, flower);
                new ImageDownloader().execute(dataSender);
            }

            return convertView;
        }
    }

    static class DataSender{

        ImageView imageView;
        FlowerModel flowerModel;

        public DataSender(ImageView imageView, FlowerModel flowerModel) {
            this.imageView = imageView;
            this.flowerModel = flowerModel;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.menu_settings){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
