package com.yavuzselim.artbooks;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.yavuzselim.artbooks.databinding.ActivityArtBinding;
import com.yavuzselim.artbooks.databinding.ActivityMainBinding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class ArtActivity extends AppCompatActivity {
    private ActivityArtBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher; // galeriye gitmek
    ActivityResultLauncher<String> permissionLauncher; //izin istemek
    Bitmap selectedImage;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArtBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        registerLauncher();

    }

    //save butonu'nun görevini yazdığımız sınıf
    public  void  Save(View view){
        String name=binding.NameText.getText().toString();
        String artistname=binding.ArtisNmae.getText().toString();
        String enteryear=binding.EnterYear.getText().toString();

        Bitmap smallImage=makeSmallerImage(selectedImage,300);

        //SQLite içerisine koymak için
        ByteArrayOutputStream OutputStream=new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG,50,OutputStream);
        byte[] byteArray=OutputStream.toByteArray();


        try {
            database=this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);
            database.execSQL("CREATE TABLE IF NOT EXISTS  arts(id INTEGER PRIMARY KEY,artname VARCHAR,paintername VARCHAR,year VARCHAR,image BLOB)");
            String sqlString="INSERT INTO arts(artname, paintername, year, image) VALUES(?, ?, ?, ?)";
            SQLiteStatement sqLiteStatement=database.compileStatement(sqlString);
            sqLiteStatement.bindString(1,name);
            sqLiteStatement.bindString(2,artistname);
            sqLiteStatement.bindString(3,enteryear);
            sqLiteStatement.bindBlob(4,byteArray);
            sqLiteStatement.execute();



        }catch (Exception e){
            e.printStackTrace();
        }

        Intent intent=new Intent(ArtActivity.this,MainActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        

    }

    //
    public  Bitmap makeSmallerImage(@org.jetbrains.annotations.NotNull Bitmap image , int maximumSize){

        int width=image.getWidth();
        int height=image.getHeight();


        float bitmapRatio = (float)width/(float)height;

        //görsel yatay ise
        if (bitmapRatio > 1){
            width=maximumSize;
            height=(int) (width/bitmapRatio);
        }

        //görsel dikey ise
        else{
            height=maximumSize;
            width=(int)(height*bitmapRatio);

        }

        return image.createScaledBitmap(image,100,100,true);
    }


    public  void  SelectImage(View view){
        // galeriye ulaşmasına izin verilmiş mi verilmemişmi onu kontrol ediyoruz
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            //izin isteme mantiğını kullanıcıya göstereyim mi
            if( ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)) {

                //kullanıcıya izin vermediği taktirde neden izin vermesi gerektiğinş açıklayan snackbar mesajı
                Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give permision", new View.OnClickListener() {

                    //setAction buton getiriri ve tıklanması halinde olumlu ve olumsuz durumlarda ne yapacagımızı yazdığımız kısım
                    @Override
                    public void onClick(View v) {
                        //request permission

                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

                    }
                }).show();

            }else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

            }

            //request permission

        }else {
            //gallery

            //galeriye gidip görsel almak
            Intent intentToGallery= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);

        }

    }



    //ResultLauncher ın ne yapcagını tanımlıyoruz ve bunu   onCreate altına çağrıyoruz
    private  void  registerLauncher(){

        activityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                //kullanıcı galeriden birşey seçtiyse
                if (result.getResultCode() == RESULT_OK){

                    Intent intentFromResualt=result.getData();

                    if (intentFromResualt != null){

                        Uri imageData= intentFromResualt.getData();
                      //  binding.imageView.setImageURI(imageData);

                        try {

                            if (Build.VERSION.SDK_INT >= 28){
                                ImageDecoder.Source source=ImageDecoder.createSource(ArtActivity.this.getContentResolver(),imageData);
                                selectedImage= ImageDecoder.decodeBitmap(source);
                                binding.imageView.setImageBitmap(selectedImage);
                            }

                            else {
                                selectedImage= MediaStore.Images.Media.getBitmap(ArtActivity.this.getContentResolver(),imageData);
                                binding.imageView.setImageBitmap(selectedImage);

                            }

                        }catch (Exception e){

                            e.printStackTrace();
                        }


                    }
                }


            }
        });



        //izin istemek
            permissionLauncher =registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {

            //resual true ise izin verildi false ise izin verilmedi
            @Override
            public void onActivityResult(Boolean result) {

                //izin verildi
                if (result){

                    Intent intentToGallery= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);


                }

                //izin verilmedi
                else {

                    //izin verilmediği taktirde bir Toast mesajı göstererek izin istiyoruz
                    Toast.makeText(ArtActivity.this,"permission needed",Toast.LENGTH_LONG).show();

                }


            }
        });

    }





}