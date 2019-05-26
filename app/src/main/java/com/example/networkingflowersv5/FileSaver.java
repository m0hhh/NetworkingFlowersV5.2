package com.example.networkingflowersv5;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileSaver {


    public static void SaveFile(Context context, FlowerModel flowerModel) {

        String fileName = context.getCacheDir() + "/" + flowerModel.getPhoto();

        File file = new File(fileName);

        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            if(flowerModel.getBitmap() != null)
            flowerModel.getBitmap().compress(Bitmap.CompressFormat.JPEG, 99, stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public static Bitmap GetFile(Context context, FlowerModel flower) {

        String FileName = context.getCacheDir() + "/" + flower.getPhoto();
        File file = new File(FileName);

        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
            return BitmapFactory.decodeStream(stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
