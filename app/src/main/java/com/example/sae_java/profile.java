package com.example.sae_java;

import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.OutputStream;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

public class profile extends AppCompatActivity {
    // Déclaration des éléments de l'interface utilisateur
    ImageView back, imgglide, logout; // ImageView pour le bouton de retour, l'image de profil, et le bouton de déconnexion
    EditText nfname, nemail, imgurl, opass, npass, ncpass, nage, phone; // Champs de texte pour modifier le profil
    TextView mail, username; // Affichage du mail et du nom d'utilisateur
    Button save; // Bouton pour enregistrer les modifications
    String jsonData; // Variable pour stocker les données JSON passées à l'activité

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Définit le layout de l'activité

        // Initialisation des vues à partir du layout
        back = findViewById(R.id.homecom);
        logout = findViewById(R.id.logoutpro);
        imgglide = findViewById(R.id.profilepic);
        mail = findViewById(R.id.emailcom);
        username = findViewById(R.id.usercom);
        nfname = findViewById(R.id.nfname);
        nemail = findViewById(R.id.nemail);
        imgurl = findViewById(R.id.nimage);
        phone = findViewById(R.id.nphone);
        nage = findViewById(R.id.nage);
        opass = findViewById(R.id.oldpass);
        npass = findViewById(R.id.npass);
        ncpass = findViewById(R.id.ncpass);
        save = findViewById(R.id.sett);

        // Récupération des données JSON passées à l'activité
        jsonData = getIntent().getStringExtra("json_data");
        try {
            // Parsing des données JSON pour obtenir les informations de l'utilisateur
            JSONObject jsonResponse = new JSONObject(jsonData);
            JSONObject userObject = jsonResponse.getJSONObject("user");

            String name = userObject.getString("name");
            String mailprof = userObject.getString("email");
            String img = userObject.getString("img");

            // Chargement de l'image de profil avec Glide (si l'URL d'image est vide, on charge une image par défaut)
            if(!img.toString().isEmpty()) {
                Glide.with(profile.this).load(img).into(imgglide);
            } else {
                Glide.with(profile.this).load(R.drawable.user_default).into(imgglide);
            }

            // Affichage de l'email et du nom de l'utilisateur
            mail.setText(mailprof);
            username.setText(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Écouteur pour le bouton "retour" (redirige vers l'écran d'accueil)
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(profile.this, acceuil.class);
                home.putExtra("json_data", jsonData); // On passe à l'activité d'accueil les données JSON
                startActivity(home);
            }
        });

        // Écouteur pour le bouton "déconnexion"
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Si le champ prénom est rempli, on utilise le prénom pour la déconnexion, sinon le nom d'utilisateur
                String fname = !nfname.getText().toString().isEmpty() ? nfname.getText().toString() : username.getText().toString();
                // Lancer la tâche de déconnexion
                new update().execute("logout", fname);
                // Rediriger vers l'écran de connexion après la déconnexion
                Intent i = new Intent(profile.this, MainActivity.class);
                startActivity(i);
            }
        });

        // Écouteur pour le bouton "enregistrer" (sauvegarder les modifications du profil)
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Récupération des valeurs des champs de texte
                String email = nemail.getText().toString();
                String npassword = npass.getText().toString();
                String opassword = opass.getText().toString();
                String cpassword = ncpass.getText().toString();
                String age = nage.getText().toString();
                String nphone = phone.getText().toString();
                String fname = nfname.getText().toString();
                String oemail = mail.getText().toString();
                String img = imgurl.getText().toString();

                // Lancer la tâche de mise à jour du profil avec les nouvelles données
                new update().execute("update", oemail, opassword, npassword, cpassword, fname, nphone, age, email, img);
            }
        });
    }

    // Classe AsyncTask pour effectuer les opérations réseau en arrière-plan (mise à jour du profil et déconnexion)
    private class update extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // URLs pour les scripts PHP pour la mise à jour du profil et la déconnexion
            String update_url = "http://10.3.122.105/update2.php";
            String logout_url = "http://10.3.122.105/logout.php";
            String type = params[0];

            if (type.equals("update")) { // Mise à jour du profil
                try {
                    String email = params[1];
                    String opassword = params[2];
                    String npassword = params[3];
                    String cpassword = params[4];
                    String name = params[5];
                    String tel = params[6];
                    String age = params[7];
                    String nemail = params[8];
                    String img = params[9];

                    // Vérification si les mots de passe correspondent
                    if (cpassword.equals(npassword)) {
                        String data = "email=" + URLEncoder.encode(email, "UTF-8")
                                + "&opass=" + URLEncoder.encode(opassword, "UTF-8")
                                + "&fname=" + URLEncoder.encode(name, "UTF-8")
                                + "&phone=" + URLEncoder.encode(tel, "UTF-8")
                                + "&age=" + URLEncoder.encode(age, "UTF-8")
                                + "&npass=" + URLEncoder.encode(npassword, "UTF-8")
                                + "&imgurl=" + URLEncoder.encode(img, "UTF-8")
                                + "&nemail=" + URLEncoder.encode(nemail, "UTF-8");

                        // Exécution de la requête HTTP pour la mise à jour du profil
                        return executeHttpRequest(update_url, data);
                    } else {
                        return "Les mots de passe ne correspondent pas"; // Si les mots de passe ne correspondent pas
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return "Erreur MalformedURLException : " + e.getMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                    return "Erreur IOException : " + e.getMessage();
                }
            } else if (type.equals("logout")) { // Déconnexion
                try {
                    String fname = params[1];
                    String data = "username=" + URLEncoder.encode(fname, "UTF-8");

                    // Exécution de la requête HTTP pour la déconnexion
                    return executeHttpRequest(logout_url, data);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return "Erreur MalformedURLException : " + e.getMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                    return "Erreur IOException : " + e.getMessage();
                }
            }
            return null;
        }

        // Méthode pour exécuter une requête HTTP (POST)
        private String executeHttpRequest(String urlWithParams, String data) throws IOException {
            URL url = new URL(urlWithParams);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Envoi des données dans le corps de la requête HTTP
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(data.getBytes("UTF-8"));
            outputStream.flush();

            // Lecture de la réponse du serveur
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

        // Une fois la tâche terminée, on affiche le résultat de la mise à jour ou de la déconnexion
        @Override
        protected void onPostExecute(String result) {
            AlertDialog alertDialog = new AlertDialog.Builder(profile.this).create();
            alertDialog.setTitle("Statut de la mise à jour du profil");
            alertDialog.setMessage(result);
            alertDialog.show();

            // Si la mise à jour est réussie, on déconnecte l'utilisateur et on le redirige vers l'écran de connexion
            if (result.equals("Mise à jour réussie")) {
                String fname = !nfname.getText().toString().isEmpty() ? nfname.getText().toString() : username.getText().toString();
                new update().execute("logout", fname);
                Intent i = new Intent(profile.this, MainActivity.class);
                startActivity(i);
            }
        }
    }
}
