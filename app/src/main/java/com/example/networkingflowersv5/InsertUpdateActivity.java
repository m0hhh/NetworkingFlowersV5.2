package com.example.networkingflowersv5;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.IOException;

public class InsertUpdateActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_FOR_SELECT_IMAGE = 23;
    private Bitmap bitmap;
    ImageView iv_ThumbNail;

    private EditText etFlowerName;
    private EditText etFlowerPrice;
    private Spinner spFlowerCategory;
    private EditText etFlowerInstructions;
    private FlowerModel flower;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_update);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.insertFlowerTitle);
        initViews();

        flower = getIntent().getParcelableExtra(DetailActivity.flowerObjectDetail);
        if(flower != null){
            //assign views
          //  assignViews(flower);
        }


        iv_ThumbNail = findViewById(R.id.ivFlowerSelectImage);
    }

    private void initViews(){

        etFlowerName = findViewById(R.id.etFlowerName);
        etFlowerPrice = findViewById(R.id.etFlowerPrice);
        spFlowerCategory = findViewById(R.id.spFlowerCategory);
        etFlowerInstructions = findViewById(R.id.et_Flower_Instruct);

    }

    private void assignViews(FlowerModel flower){

        etFlowerName.setText(flower.getName());
        etFlowerPrice.setText(Double.toString(flower.getPrice()));
        etFlowerInstructions.setText(flower.getInstructions());
        spFlowerCategory.setSelection(((ArrayAdapter) spFlowerCategory.getAdapter()).getPosition(flower.getCategory()));
        iv_ThumbNail.setImageBitmap(flower.getBitmap());

    }

    public void SelectImage(View view) {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), REQUEST_CODE_FOR_SELECT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_CODE_FOR_SELECT_IMAGE && resultCode == RESULT_OK) {
            try {
                if (intent != null) {
                    Uri fileUri = intent.getData();
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), intent.getData());
                    Log.i("BitmapSize", bitmap.getByteCount() + "");
                    bitmap = getResizedBitmap(bitmap, 100);
                    Log.i("BitmapSizeReduced", bitmap.getByteCount() + "");
                    iv_ThumbNail.setImageBitmap(bitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.v("CRUDActivity", e.getMessage());
            }
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public void SaveFlower(View view) {



        String FlowerName = etFlowerName.getText().toString();
        String FlowerPrice = etFlowerPrice.getText().toString();
        String FlowerInstructions = etFlowerInstructions.getText().toString();
        String FlowerCategory = spFlowerCategory.getSelectedItem().toString();


         flower = new FlowerModel();

        if(!FlowerName.isEmpty()){
            flower.setName(FlowerName);
            flower.setPhoto(FlowerName);
        }

        if(!FlowerPrice.isEmpty()){
            flower.setPrice(Double.parseDouble(FlowerPrice));
        }

        if(!FlowerInstructions.isEmpty()){
            flower.setInstructions(FlowerInstructions);
        }

        if(!FlowerCategory.isEmpty()){
            flower.setCategory(FlowerCategory);
        }

        if(bitmap != null){
            flower.setBitmap(bitmap);
        }

        FlowerViewModel mFlowerViewModel = ViewModelProviders.of(this).get(FlowerViewModel.class);

        mFlowerViewModel.insert(flower);
        FileSaver.SaveFile(this, flower);
        finish();
    }
}
