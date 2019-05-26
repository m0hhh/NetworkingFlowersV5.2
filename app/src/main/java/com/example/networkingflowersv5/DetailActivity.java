package com.example.networkingflowersv5;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {


    public static final String FLOWER_NAME = "Flower Name";
    public static final int RESULT_CODE = 200;
    public static final String ONLINE_WEBSERVICES_BASE_URL = "http://10.0.2.2/shopping_cart/";
    private String FlowerName = "";
    private int FlowerID;
    public static final String flowerObjectDetail = "FlowerObjectDetail";
    public static final String FLOWER_ID = "flower_id";
    private FlowerModel flower;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

         flower = getIntent().getParcelableExtra(MainActivity.FLOWER_OBJECT);

        TextView tvFlowerName = findViewById(R.id.tvFlowerNameDetail);
        TextView tvFlowerInstr = findViewById(R.id.tvFlowerInstructionsDetail);
        TextView tvFlowerPrice = findViewById(R.id.tvFlowerPriceDetail);
        ImageView ivFlowerImage = findViewById(R.id.ivFlowerImageDetail);


        tvFlowerName.setText(flower.getName() +" [" + flower.getCategory() + "]");
        tvFlowerInstr.setText(flower.getInstructions());
        tvFlowerPrice.setText(String.format("%s", flower.getPrice()));
        ivFlowerImage.setImageBitmap(flower.getBitmap());

        FlowerName = flower.getName();
        FlowerID = flower.getProductId();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void BuyOnClickHandler(View view){

//        Intent intent = getIntent().putExtra(FLOWER_NAME, FlowerName);
//        setResult(RESULT_CODE, intent);
//        finish();

        String url = ONLINE_WEBSERVICES_BASE_URL + "insert.php";
        new AddFlowerToShoppingAsyncTask().execute(url);

    }

    private class AddFlowerToShoppingAsyncTask extends AsyncTask<String, Void, String>{


        @Override
        protected String doInBackground(String... strings) {

            Map<String, String> params = new HashMap<>();
            params.put(FLOWER_ID, String.valueOf(FlowerID));

            return NetManager.postData(strings[0], "GET", params);


        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            if(json != null){
                Toast.makeText(DetailActivity.this, json.toString(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DetailActivity.this, ShoppingCart.class);
                startActivity(intent);

            }else{
                Toast.makeText(DetailActivity.this, (CharSequence) json, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void EditFlower(View view) {

        Intent intent = new Intent(this, InsertUpdateActivity.class);
        intent.putExtra(flowerObjectDetail, flower);
        startActivity(intent);
    }
}
