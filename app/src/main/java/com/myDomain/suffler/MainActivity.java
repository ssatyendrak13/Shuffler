package com.myDomain.suffler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.myDomain.suffler.R;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    
    TextView noMusicTextView;
    RecyclerView recyclerView;
    ArrayList<AudioModel> songsList = new ArrayList<>(); // all the songs will store in this list
    
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        recyclerView = findViewById(R.id.recycle_view);
        noMusicTextView = findViewById(R.id.no_songs_text);

        // if there no permission then request for the permission
        if(checkPermission()==false){
            requestPermission();
            return ;
        }
        // other wise music list will show here
        // so for that

        String [] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC + " !=0";// this selection string pass if we want music from database

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection, selection , null,null);
        // this way all the music file will be store in cursor reference variable

        while(cursor.moveToNext()){
            AudioModel songsData = new AudioModel(cursor.getString(1) , cursor.getString(0) ,cursor.getString(2));
            // sometime if we delete the songs, it stays in database and when we access from database it shows deleted song
            // so lets check song exist or not if present then add otherwise not
            if(new File(songsData.getPath()).exists()){
                songsList.add(songsData);
            }

            // if no any song present in database then show the no song message othe wise add the song in recycle view
            if(songsList.size()==0){
                noMusicTextView.setVisibility(View.VISIBLE);
            }else{
                // show the songs list in recycleView
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(new MusicListAdapter(songsList , getApplicationContext()));
            }
        }

        
    }
    
    // to check the permission 
    boolean checkPermission(){
        // firstly we will check for the permission if we don't have permission we will request for the permission 
        int result = ContextCompat.checkSelfPermission(MainActivity.this , Manifest.permission.READ_EXTERNAL_STORAGE);

        // if the permission is granted we will return true othe wise return false
        if(result== PackageManager.PERMISSION_GRANTED){
            return true;
        }else{
            return false;
        }
    }
    // to request the permission 
    void requestPermission(){

        // if user deny the permission then we will show the some message and ask them to allow the permission to us so for that
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this , Manifest.permission.READ_EXTERNAL_STORAGE)){
            Toast.makeText(MainActivity.this , "READ PERMISSION IS REQUIRED, PLEASE ALLOW FROM SETTINGS" , Toast.LENGTH_SHORT).show();
        }else{
            // if there is no permission we will request for the permission
            ActivityCompat.requestPermissions(MainActivity.this , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE} , 123);
            // this way we will requst for the permission
        }
    }
    protected void onResume() {

        super.onResume();
        if(recyclerView != null){
            recyclerView.setAdapter(new MusicListAdapter(songsList , getApplicationContext()));
        }
    }
}