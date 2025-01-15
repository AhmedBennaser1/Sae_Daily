package com.example.sae_java;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class addcom extends AppCompatActivity {
    String jsonData;

    EditText imgurl, lien, titre, descrip; // Champs de texte pour l'URL de l'image, le lien, le titre et la description
    ImageView imgglide, back; // Image de profil et bouton de retour

    TextView username, mail; // Texte affichant le nom d'utilisateur et l'email

    Button journee; // Bouton pour ajouter une nouvelle journée

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcom); // Définir le layout de l'activité

        // Initialisation des vues
        imgglide = findViewById(R.id.picom);
        mail = findViewById(R.id.emailcom);
        username = findViewById(R.id.usercom);
        back = findViewById(R.id.homecom);
        journee = findViewById(R.id.journeebutton);
        lien = findViewById(R.id.lien);
        imgurl = findViewById(R.id.url);
        titre = findViewById(R.id.titre);
        descrip = findViewById(R.id.description);

        // Récupérer les données JSON passées à l'activité
        jsonData = getIntent().getStringExtra("json_data");

        try {
            // Parser les données JSON de l'utilisateur
            JSONObject jsonResponse = new JSONObject(jsonData);
            JSONObject userObject = jsonResponse.getJSONObject("user");

            String name = userObject.getString("name"); // Récupérer le nom de l'utilisateur
            String mailprof = userObject.getString("email"); // Récupérer l'email de l'utilisateur
            String img = userObject.getString("img"); // Récupérer l'URL de l'image de profil

            // Charger l'image de profil dans l'ImageView en utilisant Glide
            if (!img.toString().isEmpty()) {
                Glide.with(addcom.this).load(img).into(imgglide);
            } else {
                Glide.with(addcom.this).load(R.drawable.user_default).into(imgglide); // Image par défaut si pas d'URL
            }

            // Afficher le nom et l'email dans les TextViews
            mail.setText(mailprof);
            username.setText(name);
        } catch (JSONException e) {
            e.printStackTrace(); // Gérer les erreurs de parsing JSON
        }

        // Gérer le clic sur le bouton de retour
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(addcom.this, acceuil.class); // Retour à l'écran d'accueil
                home.putExtra("json_data", jsonData); // Passer les données JSON à l'écran d'accueil
                startActivity(home); // Lancer l'activité d'accueil
            }
        });

        // Gérer le clic sur le bouton "Ajouter une journée"
        journee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Récupérer les données entrées par l'utilisateur
                String link = lien.getText().toString();
                String img = imgurl.getText().toString();
                String title = titre.getText().toString();
                String description = descrip.getText().toString();

                // Lancer la tâche asynchrone pour envoyer les données
                new addjournee().execute(link, img, title, description);
            }
        });
    }

    // Classe AsyncTask pour gérer l'ajout de la journée en arrière-plan
    private class addjournee extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String com_url = "http://10.3.122.105/addjournee.php"; // URL de l'API PHP
            try {
                String lien = params[0];
                String img = params[1];
                String title = params[2];
                String description = params[3];

                // Appeler la méthode pour envoyer la requête HTTP
                return executeHttpRequest(com_url, lien, img, title, description);

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "MalformedURLException: " + e.getMessage(); // Gestion des erreurs de l'URL
            } catch (IOException e) {
                e.printStackTrace();
                return "IOException: " + e.getMessage(); // Gestion des erreurs d'entrée/sortie
            }
        }

        // Méthode pour exécuter la requête HTTP
        private String executeHttpRequest(String urlString, String lien, String img, String title, String description) throws IOException {
            HttpURLConnection httpURLConnection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(urlString); // Créer l'URL
                httpURLConnection = (HttpURLConnection) url.openConnection(); // Ouvrir la connexion HTTP
                httpURLConnection.setRequestMethod("POST"); // Utiliser la méthode POST
                httpURLConnection.setDoOutput(true); // Autoriser l'envoi de données
                httpURLConnection.setDoInput(true); // Autoriser la réception de données
                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); // Définir le type de contenu

                // Créer la chaîne de données à envoyer avec la requête POST
                String postData = "url=" + URLEncoder.encode(lien, "UTF-8")
                        + "&titre=" + URLEncoder.encode(title, "UTF-8")
                        + "&descrip=" + URLEncoder.encode(description, "UTF-8")
                        + "&imgurl=" + URLEncoder.encode(img, "UTF-8");

                // Envoyer les données avec POST
                DataOutputStream writer = new DataOutputStream(httpURLConnection.getOutputStream());
                writer.writeBytes(postData);
                writer.flush();
                writer.close();

                // Lire la réponse du serveur
                InputStream inputStream = httpURLConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                return result.toString(); // Retourner la réponse du serveur
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect(); // Fermer la connexion
                }
                if (reader != null) {
                    reader.close(); // Fermer le reader
                }
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            // Afficher un message de confirmation lorsque la journée est ajoutée
            AlertDialog alertDialog = new AlertDialog.Builder(addcom.this).create();
            alertDialog.setTitle("Journee added successfully");
            alertDialog.setMessage(result); // Afficher le message du serveur
            alertDialog.show();

            // Si la journée a été ajoutée avec succès
            if (result.equals("Journee ajoutee")) {
                try {
                    // Créer un objet JSON pour la nouvelle journée
                    JSONObject newJournee = new JSONObject();
                    newJournee.put("titre", titre.getText().toString());
                    newJournee.put("description", descrip.getText().toString());
                    newJournee.put("lien", lien.getText().toString());
                    newJournee.put("image_link", imgurl.getText().toString());

                    // Ajouter la nouvelle journée au tableau "data"
                    JSONObject jsonResponse = new JSONObject(jsonData);
                    JSONArray dataArray = jsonResponse.getJSONArray("data");

                    dataArray.put(newJournee); // Ajouter la nouvelle journée

                    jsonResponse.put("data", dataArray); // Mettre à jour les données JSON

                    Log.d("Updated JSON", jsonResponse.toString()); // Afficher le JSON mis à jour dans le log

                    // Retourner à l'écran d'accueil avec les données mises à jour
                    Intent i = new Intent(addcom.this, acceuil.class);
                    i.putExtra("json_data", jsonResponse.toString());
                    startActivity(i);

                } catch (JSONException e) {
                    e.printStackTrace(); // Gérer les erreurs de parsing JSON
                }
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
