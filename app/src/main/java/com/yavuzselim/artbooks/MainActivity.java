package com.yavuzselim.artbooks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.yavuzselim.artbooks.databinding.ActivityMainBinding;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    ArrayList<Art> artArrayList;
    ArtAdapter artAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        View view =binding.getRoot();
        setContentView(view);
        artArrayList=new ArrayList<>();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        artAdapter=new ArtAdapter(artArrayList);
        binding.recyclerView.setAdapter(artAdapter);

        getData();



    }


    private  void  getData(){
        try {
            SQLiteDatabase sqLiteDatabase =this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);
            Cursor cursor=sqLiteDatabase.rawQuery("SELECT * FROM arts ",null);
            int nameIx=cursor.getColumnIndex("artname");
            int idIx=cursor.getColumnIndex("id");


            while (cursor.moveToNext()){
                String name=cursor.getString(nameIx);
                int id=cursor.getInt(idIx);

                Art art=new Art(name,id);
                artArrayList.add(art);

            }

            //her ekleme yapıldığında veri seti değişti deriz
            artAdapter.notifyDataSetChanged();
            cursor.close();

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override //oluşturduğumuz menu'yü koda bağlıcaz
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater(); // menüleri bağlmak için ManuInflater kullanılır
        menuInflater.inflate(R.menu.art_menu,menu);  //bağlamak istediği menüyü yazıyoruz

        return super.onCreateOptionsMenu(menu);
    }


    @Override // menu'ye bağlanınca ne olacağını yazdığımız sınıf
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.add_art){// menü de bu durum seçildiyse ne olacak

            Intent intent=new Intent(MainActivity.this,ArtActivity.class);  //hangi sınıfa geçeceğimizi yazıyoruz
            startActivity(intent);   //başka sınıfa geçmeyi başlatıyoruz



        }

        /*else if(){
            menü de birden çok seçenek varsa else if şeklinde hangi durum da hangi görev yapılacak yazılır
        }

         */


        return super.onOptionsItemSelected(item);
    }





}