package com.example.sae_java;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class inscription extends AppCompatActivity {
    // Déclarations des variables pour les champs du formulaire
    EditText emailinscri, passwdinscri, passwdconfirm, fname, phone, age;
    TextView display;
    Button btnlog, btnsins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        // Initialisation des vues
        emailinscri = findViewById(R.id.mail);
        passwdinscri = findViewById(R.id.pass);
        passwdconfirm = findViewById(R.id.cpasswd);
        fname = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        age = findViewById(R.id.age);

        btnsins = findViewById(R.id.inscri);
        btnlog = findViewById(R.id.login);

        // Lors du clic sur le bouton de connexion, on redirige vers l'écran de connexion
        btnlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(inscription.this, MainActivity.class);
                startActivity(i);
            }
        });
    }

    // Cette méthode est appelée lors du clic sur le bouton d'inscription
    public void onregister(View view) {
        // Récupérer les valeurs saisies dans les champs du formulaire
        String mail = emailinscri.getText().toString();
        String pass = passwdinscri.getText().toString();
        String passc = passwdconfirm.getText().toString();
        String name = fname.getText().toString();
        String tel = phone.getText().toString();
        String ans = age.getText().toString();

        // Vérification que les mots de passe correspondent
        if (!pass.equals(passc)) {
            showAlert("Error", "Password confirmation does not match!"); // Afficher une alerte si les mots de passe ne correspondent pas
            return;
        }

        // Exécuter la tâche d'inscription en arrière-plan
        RegisterTask registerTask = new RegisterTask(this);
        registerTask.execute(mail, pass, passc, name, tel, ans);
    }

    // Méthode pour afficher une alerte
    private void showAlert(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.show();
    }

    // Tâche asynchrone pour l'inscription de l'utilisateur
    private static class RegisterTask extends AsyncTask<String, Void, String> {
        private Context context;
        private AlertDialog alertDialog;

        // Constructeur qui reçoit le contexte pour pouvoir manipuler l'UI
        RegisterTask(Context ctx) {
            context = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            String register_url = "http://10.3.122.105/users.php";  // URL de votre serveur pour l'inscription

            try {
                // Récupération des données envoyées depuis l'activité
                String email = params[0];
                String password = params[1];
                String passwordc = params[2];
                String name = params[3];
                String tel = params[4];
                String age = params[5];

                // Vérification que les mots de passe correspondent
                if (!password.equals(passwordc)) {
                    return "Password confirmation does not match!";
                }

                // Création des données à envoyer dans la requête POST
                String data = "email=" + URLEncoder.encode(email, "UTF-8")
                        + "&password=" + URLEncoder.encode(password, "UTF-8")
                        + "&fname=" + URLEncoder.encode(name, "UTF-8")
                        + "&phone=" + URLEncoder.encode(tel, "UTF-8")
                        + "&age=" + URLEncoder.encode(age, "UTF-8");

                // Appel à la méthode pour exécuter la requête HTTP
                return executeHttpRequest(register_url, data);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "MalformedURLException: " + e.getMessage(); // Gestion des erreurs liées à l'URL
            } catch (IOException e) {
                e.printStackTrace();
                return "IOException: " + e.getMessage(); // Gestion des erreurs réseau
            }
        }

        // Exécution de la requête HTTP pour envoyer les données
        private String executeHttpRequest(String register_url, String data) throws IOException {
            HttpURLConnection httpURLConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(register_url);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST"); // Envoi en méthode POST
                httpURLConnection.setDoOutput(true); // Autoriser l'envoi de données
                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                // Envoi des données au serveur
                httpURLConnection.getOutputStream().write(data.getBytes("UTF-8"));

                // Récupération de la réponse du serveur
                InputStream inputStream = httpURLConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line); // Ajouter la réponse à la chaîne
                }
                return result.toString(); // Retourner la réponse du serveur
            } finally {
                // Assurer la fermeture des connexions
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Avant d'exécuter la tâche, afficher un message de chargement
        @Override
        protected void onPreExecute() {
            alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Status");
            alertDialog.setMessage("Registering...");
            // Rediriger vers l'écran de connexion après l'inscription
            Intent i = new Intent(context, MainActivity.class);
            context.startActivity(i);
        }

        // Après l'exécution de la tâche, traiter la réponse du serveur
        @Override
        protected void onPostExecute(String result) {
            alertDialog.dismiss(); // Masquer l'alerte de chargement

            // Si l'inscription est réussie, rediriger vers l'écran de connexion
            if (result.contains("success")) {
                Intent i = new Intent(context, MainActivity.class);
                context.startActivity(i);
            } else {
                // Si l'inscription échoue, afficher un message d'erreur
                showAlert("Registration failed", result);
            }
        }

        // Méthode pour afficher une alerte
        private void showAlert(String title, String message) {
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle(title);
            alertDialog.setMessage(message);
            alertDialog.show();
        }
    }
}
