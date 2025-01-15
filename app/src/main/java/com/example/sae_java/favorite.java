package com.example.sae_java;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class favorite extends AppCompatActivity {
    ImageView back;
    LinearLayout scroll;
    ArrayList<String> resultlist;
    EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        back = findViewById(R.id.btn_back);
        scroll = findViewById(R.id.content_layout);



        back.setOnClickListener(view -> {
            Intent intent = new Intent(favorite.this, acceuil.class);
            startActivity(intent);
        });
        String jsonData = getIntent().getStringExtra("json_data");
        resultlist.add(jsonData);

        try {
            JSONArray jsonArray = new JSONArray(jsonData);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String titre = new String(jsonObject.getString("titre").getBytes("ISO-8859-1"), "UTF-8");
                String description = new String(jsonObject.getString("description").getBytes("ISO-8859-1"), "UTF-8");
                String imageLink = new String(jsonObject.getString("image_link").getBytes("ISO-8859-1"), "UTF-8");
                String date = new String(jsonObject.getString("date").getBytes("ISO-8859-1"), "UTF-8");
                String lien = new String(jsonObject.getString("lien").getBytes("ISO-8859-1"), "UTF-8");

                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.HORIZONTAL);
                layout.setPadding(16, 16, 16, 16);
                layout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));

                layout.setBackgroundResource(R.drawable.box_shadow);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                layoutParams.setMargins(0, 0, 0, 50);
                layout.setLayoutParams(layoutParams);



                LinearLayout imageAndButtonsLayout = new LinearLayout(this);
                imageAndButtonsLayout.setOrientation(LinearLayout.VERTICAL);
                imageAndButtonsLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                ));


                ImageView image = new ImageView(this);
                LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(
                        320, 200
                );

                imageLayoutParams.setMargins(0, 50, 0, 0);
                image.setLayoutParams(imageLayoutParams);

                Glide.with(this).load(imageLink).into(image);
                imageAndButtonsLayout.addView(image);


                LinearLayout textLayout = new LinearLayout(this);
                textLayout.setOrientation(LinearLayout.VERTICAL);
                textLayout.setPadding(10, 0, 0, 0);
                textLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        2
                ));


                TextView titleView = new TextView(this);
                titleView.setText(titre);
                titleView.setTextSize(18);
                titleView.setPadding(16, 16, 16, 16);
                titleView.setTypeface(null, Typeface.BOLD);


                TextView descrip = new TextView(this);
                descrip.setText(description);
                descrip.setTextSize(15);
                descrip.setPadding(16, 16, 16, 16);

                TextView moreinfo = new TextView(this);
                moreinfo.setText("Pour savoir plus voici le lien");
                moreinfo.setTextColor(Color.BLUE);
                moreinfo.setTextSize(13);
                moreinfo.setPadding(0, 8, 0, 0);
                moreinfo.setOnClickListener(view -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(lien));
                    startActivity(browserIntent);
                });

                TextView date2 = new TextView(this);
                date2.setText("date: "+date);
                date2.setTextSize(10);
                moreinfo.setPadding(0, 3, 0, 0);

                textLayout.addView(titleView);
                textLayout.addView(descrip);
                textLayout.addView(moreinfo);
                textLayout.addView(date2);
                layout.addView(imageAndButtonsLayout);

                layout.addView(textLayout);

                scroll.addView(layout);
            }


        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }
}
