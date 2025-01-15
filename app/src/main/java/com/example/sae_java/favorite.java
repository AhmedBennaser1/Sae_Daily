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
    ImageView back; // Bouton de retour
    LinearLayout scroll; // Layout contenant tous les éléments favoris
    EditText text; // Champ de texte pour la recherche (non utilisé dans ce code)
    ImageView prof_fav; // Image de profil de l'utilisateur

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite); // Définir le layout de l'activité

        // Initialisation des vues
        back = findViewById(R.id.btn_back);
        scroll = findViewById(R.id.content_layout);
        prof_fav = findViewById(R.id.profile_image);

        // Récupérer les données JSON passées à l'activité
        String jsonData = getIntent().getStringExtra("json_data"); // Données des éléments favoris
        String jsonData2 = getIntent().getStringExtra("json_acceuil"); // Données de l'utilisateur

        try {
            // Parser les données JSON de l'utilisateur
            JSONObject jsonResponse = new JSONObject(jsonData2);
            JSONObject userObject = jsonResponse.getJSONObject("user");

            // Charger et afficher l'image de profil de l'utilisateur
            String img = userObject.getString("img");
            Glide.with(favorite.this).load(img).into(prof_fav);

            // Parser les données JSON des éléments favoris
            JSONArray jsonArray = new JSONArray(jsonData);

            // Boucle à travers les éléments favoris et affichage dynamique
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // Récupération des données pour chaque élément favori
                String titre = new String(jsonObject.getString("titre").getBytes("ISO-8859-1"), "UTF-8");
                String description = new String(jsonObject.getString("description").getBytes("ISO-8859-1"), "UTF-8");
                String imageLink = new String(jsonObject.getString("image_link").getBytes("ISO-8859-1"), "UTF-8");
                String date = new String(jsonObject.getString("date").getBytes("ISO-8859-1"), "UTF-8");
                String lien = new String(jsonObject.getString("lien").getBytes("ISO-8859-1"), "UTF-8");

                // Création d'un layout horizontal pour chaque élément favori
                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.HORIZONTAL); // Disposition horizontale
                layout.setPadding(16, 16, 16, 16); // Espacement autour de l'élément
                layout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));

                layout.setBackgroundResource(R.drawable.box_shadow); // Appliquer une ombre à l'élément

                // Définir les marges pour l'élément
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                layoutParams.setMargins(0, 0, 0, 50);
                layout.setLayoutParams(layoutParams);

                // Création d'un layout vertical pour l'image et les boutons
                LinearLayout imageAndButtonsLayout = new LinearLayout(this);
                imageAndButtonsLayout.setOrientation(LinearLayout.VERTICAL);
                imageAndButtonsLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                ));

                // Image de l'élément favori
                ImageView image = new ImageView(this);
                LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(
                        320, 200
                );
                imageLayoutParams.setMargins(0, 50, 0, 0); // Marges autour de l'image
                image.setLayoutParams(imageLayoutParams);

                // Charger l'image à partir du lien
                Glide.with(this).load(imageLink).into(image);
                imageAndButtonsLayout.addView(image); // Ajouter l'image au layout

                // Layout pour le texte (titre, description, etc.)
                LinearLayout textLayout = new LinearLayout(this);
                textLayout.setOrientation(LinearLayout.VERTICAL);
                textLayout.setPadding(10, 0, 0, 0); // Espacement interne
                textLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        2
                ));

                // Titre de l'élément favori
                TextView titleView = new TextView(this);
                titleView.setText(titre);
                titleView.setTextSize(18); // Taille de texte
                titleView.setPadding(16, 16, 16, 16); // Espacement autour du texte
                titleView.setTypeface(null, Typeface.BOLD); // Mettre en gras

                // Description de l'élément favori
                TextView descrip = new TextView(this);
                descrip.setText(description);
                descrip.setTextSize(15);
                descrip.setPadding(16, 16, 16, 16); // Espacement autour du texte

                // Lien pour plus d'informations
                TextView moreinfo = new TextView(this);
                moreinfo.setText("Pour savoir plus voici le lien");
                moreinfo.setTextColor(Color.BLUE); // Couleur bleue pour le lien
                moreinfo.setTextSize(13);
                moreinfo.setPadding(0, 8, 0, 0);
                moreinfo.setOnClickListener(view -> {
                    // Ouvrir un navigateur pour afficher le lien
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(lien));
                    startActivity(browserIntent);
                });

                // Date de l'élément favori
                TextView date2 = new TextView(this);
                date2.setText("date: " + date);
                date2.setTextSize(10);
                moreinfo.setPadding(0, 3, 0, 0);

                // Ajouter les vues au layout du texte
                textLayout.addView(titleView);
                textLayout.addView(descrip);
                textLayout.addView(moreinfo);
                textLayout.addView(date2);

                // Ajouter les layouts d'image et de texte au layout principal de l'élément
                layout.addView(imageAndButtonsLayout);
                layout.addView(textLayout);

                // Ajouter l'élément complet (image + texte) au scroll principal
                scroll.addView(layout);
            }

        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace(); // Gérer les exceptions lors du parsing des JSON
        }

        // Gestion du clic sur le bouton retour
        back.setOnClickListener(view -> {
            Intent intent = new Intent(favorite.this, acceuil.class);
            intent.putExtra("json_data", jsonData2); // Passer les données JSON à l'écran d'accueil
            startActivity(intent); // Démarrer l'activité d'accueil
        });

    }
}
