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
    EditText emaillog, passwdlog;
    Button btnlog, btnsins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emaillog = findViewById(R.id.email);
        passwdlog = findViewById(R.id.passwd);
        btnlog = findViewById(R.id.connexion);
        btnsins = findViewById(R.id.sins);

        btnsins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, inscription.class);
                startActivity(i);
            }
        });

        // Login button listener
        btnlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLogin(view);
            }
        });
    }

    public void onLogin(View view) {
        String email = emaillog.getText().toString();
        String password = passwdlog.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage("Email or password cannot be empty!");
            alertDialog.show();
            return;
        }

        LoginsValid loginsValidTask = new LoginsValid(MainActivity.this);
        loginsValidTask.execute(email, password);

    }
}
