package com.example.sae_java;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class acceuil extends AppCompatActivity {
    private Toolbar toolbar;
    LinearLayout scroll,scroll2;
    Button commmentaire;
    EditText username,comsent;

    ImageView profile,com,favoritee;
    ArrayList<String> resultlist;
    String jsonData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceuil);

        // Initialisation des vues et des variables
        resultlist = new ArrayList<>();
        favoritee = findViewById(R.id.fav);
        commmentaire = findViewById(R.id.sentcom);
        profile = findViewById(R.id.account);
        scroll = findViewById(R.id.content);
        username = findViewById(R.id.nameacceuil);
        toolbar = findViewById(R.id.toolbar);
        com = findViewById(R.id.acom);
        comsent = findViewById(R.id.editTextText);
        scroll2 = findViewById(R.id.content_com);

        // Configuration de la barre d'action
        setSupportActionBar(toolbar);

        // Récupération des données JSON envoyées à l'activité
        jsonData = getIntent().getStringExtra("json_data");

        // Gestion du clic sur l'image de profil pour naviguer vers l'activité de profil
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent i = new Intent(acceuil.this, profile.class);
                i.putExtra("json_data", jsonData);
                startActivity(i);
            }
        });

        // Gestion du clic sur le bouton pour ajouter un commentaire
        com.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent i = new Intent(acceuil.this, addcom.class);
                i.putExtra("json_data", jsonData);
                startActivity(i);
            }
        });

        try {
            // Traitement du JSON pour récupérer les données de l'utilisateur
            JSONObject jsonResponse = new JSONObject(jsonData);
            JSONObject userObject = jsonResponse.getJSONObject("user");
            String user = userObject.getString("name");
            String img = userObject.getString("img");

            // Gestion du clic sur le bouton "favoris"
            favoritee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new likedislike().execute("favorite", user);
                }
            });

            // Chargement de l'image du profil
            if (!img.isEmpty()) {
                Glide.with(acceuil.this).load(img).into(profile);
            } else {
                Glide.with(acceuil.this).load(R.drawable.user_default).into(profile);
            }

            // Mise à jour du nom de l'utilisateur
            username.setText(user);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            // Tentative de mise à jour de l'image de fond du profil
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

        // Traitement du JSON pour afficher les différentes données
        if (jsonData != null) {
            try {
                JSONObject jsonResponse = new JSONObject(jsonData);
                String status = jsonResponse.optString("status");
                JSONObject user = jsonResponse.getJSONObject("user");

                String name = user.getString("name");

                if ("success".equalsIgnoreCase(status)) {
                    JSONArray dataArray = jsonResponse.getJSONArray("data");

                    // Parcours des éléments de données pour afficher chaque élément
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject data = dataArray.getJSONObject(i);

                        // Extraction des informations
                        String titre = new String(data.optString("titre").getBytes("ISO-8859-1"), "UTF-8");
                        String description = new String(data.optString("description").getBytes("ISO-8859-1"), "UTF-8");
                        String link = new String(data.optString("lien").getBytes("ISO-8859-1"), "UTF-8");
                        String date = new String(data.optString("date").getBytes("ISO_8859_1"), "UTF-8");
                        String imageLink = data.optString("image_link");

                        // Création de la mise en page pour chaque élément
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

                        // Gestion de l'image et des boutons associés
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

                        // Création des boutons "Like" et "Dislike"
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

                        // Ajout des boutons dans la mise en page
                        Button likeButton = new Button(this);
                        likeButton.setBackgroundResource(R.drawable.like);
                        LinearLayout.LayoutParams likeButtonParams = new LinearLayout.LayoutParams(
                                10,
                                50
                        );
                        likeButton.setTag(titre);
                        likeButtonParams.weight = 1;
                        likeButton.setLayoutParams(likeButtonParams);

                        Button dislikeButton = new Button(this);
                        dislikeButton.setBackgroundResource(R.drawable.dislike);
                        LinearLayout.LayoutParams dislikeButtonParams = new LinearLayout.LayoutParams(
                                10,
                                50
                        );
                        dislikeButton.setTag(titre);
                        dislikeButtonParams.weight = 1;
                        dislikeButton.setLayoutParams(dislikeButtonParams);

                        // Clic sur Like
                        likeButton.setOnClickListener(view -> {
                            likeButton.setBackgroundResource(R.drawable.like_selected);
                            dislikeButton.setBackgroundResource(R.drawable.dislike);
                            String titree = (String) dislikeButton.getTag();
                            new likedislike().execute("like_dislike", name, "like", titree);
                        });

                        // Clic sur Dislike
                        dislikeButton.setOnClickListener(view -> {
                            dislikeButton.setBackgroundResource(R.drawable.dislike_selected);
                            likeButton.setBackgroundResource(R.drawable.like);
                            String titree = (String) dislikeButton.getTag();
                            new likedislike().execute("like_dislike", name, "dislike", titree);
                        });

                        buttonsLayout.addView(likeButton);
                        buttonsLayout.addView(dislikeButton);

                        imageAndButtonsLayout.addView(image);
                        imageAndButtonsLayout.addView(buttonsLayout);

                        // Affichage des autres informations textuelles (titre, description, lien)
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
                        titrescroll.setTypeface(null, Typeface.BOLD);

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
                        date2.setText("date: " + date);
                        date2.setTextSize(10);
                        moreinfo.setPadding(0, 3, 0, 0);

                        textLayout.addView(titrescroll);
                        textLayout.addView(descriptionscroll);
                        textLayout.addView(moreinfo);
                        textLayout.addView(date2);

                        layout.addView(imageAndButtonsLayout);
                        layout.addView(textLayout);

                        // Ajout du layout principal dans le scroll
                        scroll.addView(layout);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Gestion du clic sur le bouton pour envoyer un commentaire
        commmentaire.setOnClickListener(view -> {
            String commentText = comsent.getText().toString().trim();
            String usernametext = username.getText().toString().trim();

            // Vérification que le commentaire n'est pas vide
            if (commentText.isEmpty()) {
                comsent.setError("Please enter a comment");
                return;
            }

            // Création du layout pour afficher le commentaire
            LinearLayout layout2 = new LinearLayout(acceuil.this);
            layout2.setOrientation(LinearLayout.VERTICAL);
            layout2.setPadding(16, 16, 16, 16);
            layout2.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            // Affichage du nom de l'utilisateur et du commentaire
            TextView commentLabel = new TextView(acceuil.this);
            commentLabel.setText("comment by " + usernametext);
            commentLabel.setTextSize(12);
            commentLabel.setPadding(0, 0, 0, 8);

            TextView descriptionscroll = new TextView(acceuil.this);
            descriptionscroll.setText(commentText);
            descriptionscroll.setTextSize(15);
            descriptionscroll.setBackgroundResource(R.drawable.box_shadow_com);
            descriptionscroll.setPadding(10, 10, 10, 10);

            layout2.addView(commentLabel);
            layout2.addView(descriptionscroll);

            // Ajout du commentaire dans le scroll des commentaires
            scroll2.addView(layout2);

            // Réinitialisation du champ de texte du commentaire
            comsent.setText("");
        });
    }

    // AsyncTask pour gérer les actions "Like", "Dislike" et "Favorite"
    private class likedislike extends AsyncTask<String, Void, String> {
        private String type;

        @Override
        protected String doInBackground(String... params) {
            String like_dis = "http://10.3.122.105/like_dis.php";
            String fav_url = "http://10.3.122.105/favorite.php";

            type = params[0];
            if (type.equals("like_dislike")) {
                try {
                    // Préparation des données pour "Like" ou "Dislike"
                    String fname = params[1];
                    String action = params[2];
                    String titre = params[3];

                    String data = "fname=" + URLEncoder.encode(fname, "UTF-8")
                            + "&titre=" + URLEncoder.encode(titre, "UTF-8")
                            + "&action=" + URLEncoder.encode(action, "UTF-8");

                    return executeHttpRequest(like_dis, data);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return "MalformedURLException: " + e.getMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                    return "IOException: " + e.getMessage();
                }
            } else if (type.equals("favorite")) {
                try {
                    // Préparation des données pour "Favorite"
                    String fname = params[1];
                    String data = "fname=" + URLEncoder.encode(fname, "UTF-8");

                    return executeHttpRequest(fav_url, data);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return "MalformedURLException: " + e.getMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                    return "IOException: " + e.getMessage();
                }
            }
            return null;
        }

        // Méthode pour exécuter une requête HTTP avec les données données
        private String executeHttpRequest(String urlWithParams, String data) throws IOException {
            URL url = new URL(urlWithParams);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(data.getBytes("UTF-8"));
            outputStream.flush();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
            StringBuilder result = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }

            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();

            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            // Si le type est "favorite", on redirige vers la page des favoris
            if ("favorite".equals(type)) {
                Intent fav = new Intent(acceuil.this, favorite.class);
                fav.putExtra("json_data", result);
                fav.putExtra("json_acceuil", jsonData);
                startActivity(fav);
            }
        }
    }
}
