package com.example.sae_java;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

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

class LoginsValid extends AsyncTask<String, Void, String> {
    private Context context; // Contexte pour les interactions avec l'UI
    private AlertDialog alertDialog; // Dialogue pour afficher des messages

    // Constructeur pour initialiser le contexte
    LoginsValid(Context ctx) {
        context = ctx;
    }

    // Cette méthode s'exécute en arrière-plan pour effectuer la requête réseau
    @Override
    protected String doInBackground(String... params) {
        String login_url = "http://10.3.122.105/login.php"; // URL de l'API de login

        try {
            // Récupérer l'email et le mot de passe passés en paramètre
            String email = params[0];
            String password = params[1];

            // Exécuter la requête HTTP pour tenter de se connecter
            return executeHttpRequest(login_url, email, password);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "MalformedURLException: " + e.getMessage(); // Retourner une erreur si l'URL est mal formée
        } catch (IOException e) {
            e.printStackTrace();
            return "IOException: " + e.getMessage(); // Retourner une erreur en cas de problème réseau
        }
    }

    // Cette méthode envoie une requête HTTP POST avec l'email et le mot de passe
    private String executeHttpRequest(String urlString, String email, String password) throws IOException {
        HttpURLConnection httpURLConnection = null;
        BufferedReader reader = null;

        try {
            // Créer l'URL à partir de la chaîne de caractères
            URL url = new URL(urlString);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST"); // Définir la méthode HTTP en POST
            httpURLConnection.setDoOutput(true); // Activer l'envoi de données
            httpURLConnection.setDoInput(true); // Activer la réception de données
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); // Type de contenu

            // Encoder les paramètres et les envoyer dans la requête POST
            String postData = "email=" + URLEncoder.encode(email, "UTF-8") +
                    "&password=" + URLEncoder.encode(password, "UTF-8");

            DataOutputStream writer = new DataOutputStream(httpURLConnection.getOutputStream());
            writer.writeBytes(postData); // Envoi des données
            writer.flush();
            writer.close();

            // Lire la réponse de l'API
            InputStream inputStream = httpURLConnection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line); // Ajouter chaque ligne de la réponse à un StringBuilder
            }
            return result.toString(); // Retourner la réponse sous forme de chaîne
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

    // Cette méthode est appelée avant l'exécution de la tâche en arrière-plan
    @Override
    protected void onPreExecute() {
        alertDialog = new AlertDialog.Builder(context).create(); // Créer une boîte de dialogue
        alertDialog.setTitle("Status"); // Titre de la boîte de dialogue
        alertDialog.setMessage("Logging in..."); // Message avant la connexion
    }

    // Cette méthode est appelée après l'exécution de la tâche en arrière-plan
    @Override
    protected void onPostExecute(String result) {
        alertDialog.dismiss(); // Fermer la boîte de dialogue après la fin de l'exécution

        try {
            // Si la réponse commence et se termine par des accolades, c'est une réponse JSON
            if (result.startsWith("{") && result.endsWith("}")) {
                JSONObject jsonResponse = new JSONObject(result); // Analyser la réponse JSON
                String status = jsonResponse.optString("status");

                if ("success".equalsIgnoreCase(status)) {
                    // Si la connexion est réussie, démarrer l'activité d'accueil
                    Intent i = new Intent(context, acceuil.class);
                    i.putExtra("json_data", result); // Passer les données JSON à l'activité suivante
                    context.startActivity(i);
                    Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show(); // Afficher un message de succès
                } else {
                    // Si la connexion a échoué, afficher le message d'erreur
                    String message = jsonResponse.optString("message");
                    alertDialog.setMessage("Login failed: " + message);
                    alertDialog.show(); // Afficher l'alerte avec le message d'erreur
                }
            } else {
                // Si la réponse n'est pas au format JSON, l'afficher dans la boîte de dialogue
                alertDialog.setMessage(result);
                alertDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            alertDialog.setMessage("Error: " + e.getMessage()); // En cas d'erreur dans le traitement de la réponse
            alertDialog.show();
        }
    }
}
