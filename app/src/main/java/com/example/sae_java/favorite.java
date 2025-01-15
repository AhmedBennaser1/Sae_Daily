package com.example.sae_java;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;

import de.hdodenhof.circleimageview.CircleImageView;

public class favorite extends AppCompatActivity {
    CircleImageView accoutImage;
    ImageView back;
    int i ;
    String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        CircleImageView accountImage = findViewById(R.id.profile_image);
        accountImage.bringToFront();
        back=findViewById(R.id.btn_back);


        data = getIntent().getStringExtra("json_data");
        if (data != null) {
            try {
                JSONArray jsonArray = new JSONArray(data);


                AlertDialog alertDialog = new AlertDialog.Builder(favorite.this).create();
                alertDialog.setTitle("Like & dislike");
                alertDialog.setMessage((CharSequence) jsonArray);
                alertDialog.show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(favorite.this).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage("No data received");
            alertDialog.show();
        }







        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent b=new Intent(favorite.this,acceuil.class);
                //b.putExtra(..);
                startActivity(b);

            }
        });




    }
}