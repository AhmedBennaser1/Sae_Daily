package com.example.sae_java;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import android.graphics.drawable.Drawable;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import androidx.appcompat.widget.Toolbar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class acceuil extends AppCompatActivity {
    private Toolbar toolbar;
    LinearLayout scroll;
    Button  commmentaire;
    EditText username;

    ImageView profile,com;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceuil);
        commmentaire=findViewById(R.id.sentcom);
        profile=findViewById(R.id.account);
        scroll = findViewById(R.id.content);
        username=findViewById(R.id.nameacceuil);
        toolbar=findViewById(R.id.toolbar);
        com=findViewById(R.id.acom);
        setSupportActionBar(toolbar);


        String jsonData = getIntent().getStringExtra("json_data");
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent i = new Intent(acceuil.this, profile.class);
                i.putExtra("json_data", jsonData);
                startActivity(i);

            }

        });
        com.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent i = new Intent(acceuil.this, addcom.class);
                i.putExtra("json_data", jsonData);

                startActivity(i);

            }

        });

        try {
            JSONObject jsonResponse = new JSONObject(jsonData);
            JSONObject userObject = jsonResponse.getJSONObject("user");

            String user = userObject.getString("name");
            String img = userObject.getString("img");

            Glide.with(acceuil.this).load(img).into(profile);

            username.setText(user);

        } catch (JSONException e) {
            e.printStackTrace();
        }



        try {
            JSONObject image = new JSONObject(jsonData);
            JSONObject userObject = image.getJSONObject("user");

            String imgUrl = userObject.getString("img");

            Glide.with(this)
                    .load(imgUrl)
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                            profile.setBackground(resource);
                        }

                        @Override
                        public void onLoadCleared(Drawable placeholder) {
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (jsonData != null) {
            try {
                JSONObject jsonResponse = new JSONObject(jsonData);
                String status = jsonResponse.optString("status");

                if ("success".equalsIgnoreCase(status)) {
                    JSONArray dataArray = jsonResponse.getJSONArray("data");

                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject data = dataArray.getJSONObject(i);

                        String titre = new String(data.optString("titre").getBytes("ISO-8859-1"), "UTF-8");
                        String description = new String(data.optString("description").getBytes("ISO-8859-1"), "UTF-8");
                        String link = new String(data.optString("lien").getBytes("ISO-8859-1"), "UTF-8");
                        String date =new String(data.optString("date").getBytes("ISO_8859_1"),"UTF-8");
                        String imageLink = data.optString("image_link");

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


                        LinearLayout buttonsLayout = new LinearLayout(this);
                        buttonsLayout.setOrientation(LinearLayout.HORIZONTAL);
                        buttonsLayout.setPadding(0, 16, 0, 0);
                        buttonsLayout.setWeightSum(2);

                        LinearLayout.LayoutParams buttonsLayoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        buttonsLayoutParams.setMargins(0, 20, 0, 0);
                        buttonsLayout.setLayoutParams(buttonsLayoutParams);

                        Button likeButton = new Button(this);
                        likeButton.setBackgroundResource(R.drawable.like);
                        LinearLayout.LayoutParams likeButtonParams = new LinearLayout.LayoutParams(
                                10,
                                50
                        );
                        likeButtonParams.weight = 1;
                        likeButton.setLayoutParams(likeButtonParams);

                        Button dislikeButton = new Button(this);
                        dislikeButton.setBackgroundResource(R.drawable.dislike);
                        LinearLayout.LayoutParams dislikeButtonParams = new LinearLayout.LayoutParams(
                                10,
                                50
                        );
                        dislikeButtonParams.weight = 1;
                        dislikeButton.setLayoutParams(dislikeButtonParams);

                        likeButton.setOnClickListener(view -> {
                            likeButton.setBackgroundResource(R.drawable.like_selected);
                            dislikeButton.setBackgroundResource(R.drawable.dislike);
                        });

                        dislikeButton.setOnClickListener(view -> {
                            dislikeButton.setBackgroundResource(R.drawable.dislike_selected);
                            likeButton.setBackgroundResource(R.drawable.like);
                        });

                        buttonsLayout.addView(likeButton);
                        buttonsLayout.addView(dislikeButton);

                        imageAndButtonsLayout.addView(image);
                        imageAndButtonsLayout.addView(buttonsLayout);

                        LinearLayout textLayout = new LinearLayout(this);
                        textLayout.setOrientation(LinearLayout.VERTICAL);
                        textLayout.setPadding(10, 0, 0, 0);
                        textLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                0,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                2
                        ));

                        TextView titrescroll = new TextView(this);
                        titrescroll.setText(titre);
                        titrescroll.setTextSize(18);

                        TextView descriptionscroll = new TextView(this);
                        descriptionscroll.setText(description);
                        descriptionscroll.setTextSize(15);
                        descriptionscroll.setPadding(0, 8, 0, 8);

                        TextView moreinfo = new TextView(this);
                        moreinfo.setText("Pour savoir plus voici le lien");
                        moreinfo.setTextColor(Color.BLUE);
                        moreinfo.setTextSize(13);
                        moreinfo.setPadding(0, 8, 0, 0);
                        moreinfo.setOnClickListener(view -> {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                            startActivity(browserIntent);
                        });

                        TextView date2 = new TextView(this);
                        date2.setText("date: "+date);
                        date2.setTextSize(10);
                        moreinfo.setPadding(0, 3, 0, 0);



                        textLayout.addView(titrescroll);
                        textLayout.addView(descriptionscroll);
                        textLayout.addView(moreinfo);
                        textLayout.addView(date2);

                        layout.addView(imageAndButtonsLayout);
                        layout.addView(textLayout);

                        scroll.addView(layout);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }
}
