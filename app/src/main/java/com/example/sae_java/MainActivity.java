package com.example.sae_java;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.app.AlertDialog;

public class MainActivity extends AppCompatActivity {
    // Déclaration des éléments d'interface utilisateur
    EditText emaillog, passwdlog;  // Champs pour l'email et le mot de passe
    Button btnlog, btnsins;        // Boutons pour la connexion et l'inscription

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // Charge le layout de l'activité principale

        // Initialisation des vues en les associant aux éléments du layout
        emaillog = findViewById(R.id.email);
        passwdlog = findViewById(R.id.passwd);
        btnlog = findViewById(R.id.connexion);
        btnsins = findViewById(R.id.sins);

        // Écouteur de clic pour le bouton d'inscription
        btnsins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lancer l'activité d'inscription lorsque l'utilisateur clique sur le bouton
                Intent i = new Intent(MainActivity.this, inscription.class);
                startActivity(i);
            }
        });

        // Écouteur de clic pour le bouton de connexion
        btnlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Appel de la méthode onLogin lorsqu'on clique sur le bouton de connexion
                onLogin(view);
            }
        });
    }

    // Méthode qui s'active lors du clic sur le bouton de connexion
    public void onLogin(View view) {
        // Récupération des valeurs entrées par l'utilisateur dans les champs email et mot de passe
        String email = emaillog.getText().toString();
        String password = passwdlog.getText().toString();

        // Vérification si l'email ou le mot de passe sont vides
        if (email.isEmpty() || password.isEmpty()) {
            // Création d'une boîte de dialogue pour afficher un message d'erreur
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Erreur");
            alertDialog.setMessage("L'email ou le mot de passe ne peuvent pas être vides !");
            alertDialog.show();  // Affichage de la boîte de dialogue
            return;  // On arrête l'exécution de la méthode si un champ est vide
        }

        // Si les champs sont remplis, on exécute une tâche pour valider les identifiants
        LoginsValid loginsValidTask = new LoginsValid(MainActivity.this);
        loginsValidTask.execute(email, password);  // Lancement de la tâche de validation des identifiants
    }
}
