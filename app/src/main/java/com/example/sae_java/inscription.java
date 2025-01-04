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
    EditText emailinscri, passwdinscri, passwdconfirm, fname, phone, age;
    TextView display;
    Button btnlog, btnsins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        emailinscri = findViewById(R.id.mail);
        passwdinscri = findViewById(R.id.pass);
        passwdconfirm = findViewById(R.id.cpasswd);
        fname = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        age = findViewById(R.id.age);

        btnsins = findViewById(R.id.inscri);
        btnlog = findViewById(R.id.login);

        btnlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(inscription.this, MainActivity.class);
                startActivity(i);
            }
        });
    }

    public void onregister(View view) {
        String mail = emailinscri.getText().toString();
        String pass = passwdinscri.getText().toString();
        String passc = passwdconfirm.getText().toString();
        String name = fname.getText().toString();
        String tel = phone.getText().toString();
        String ans = age.getText().toString();

        if (!pass.equals(passc)) {
            showAlert("Error", "Password confirmation does not match!");
            return;
        }

        RegisterTask registerTask = new RegisterTask(this);
        registerTask.execute(mail, pass, passc, name, tel, ans);
    }

    private void showAlert(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.show();
    }

    private static class RegisterTask extends AsyncTask<String, Void, String> {
        private Context context;
        private AlertDialog alertDialog;

        RegisterTask(Context ctx) {
            context = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            String register_url = "http://10.3.122.105/users.php";  // Your server URL for registration

            try {
                String email = params[0];
                String password = params[1];
                String passwordc = params[2];
                String name = params[3];
                String tel = params[4];
                String age = params[5];

                if (!password.equals(passwordc)) {
                    return "Password confirmation does not match!";
                }

                String data = "email=" + URLEncoder.encode(email, "UTF-8")
                        + "&password=" + URLEncoder.encode(password, "UTF-8")
                        + "&fname=" + URLEncoder.encode(name, "UTF-8")
                        + "&phone=" + URLEncoder.encode(tel, "UTF-8")
                        + "&age=" + URLEncoder.encode(age, "UTF-8");

                return executeHttpRequest(register_url, data);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "MalformedURLException: " + e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return "IOException: " + e.getMessage();
            }
        }

        private String executeHttpRequest(String register_url, String data) throws IOException {
            HttpURLConnection httpURLConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(register_url);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                httpURLConnection.getOutputStream().write(data.getBytes("UTF-8"));

                InputStream inputStream = httpURLConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                return result.toString();
            } finally {
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

        @Override
        protected void onPreExecute() {
            alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Status");
            alertDialog.setMessage("Registering...");
            Intent i=new Intent(context,MainActivity.class);
            context.startActivity(i);
        }

        @Override
        protected void onPostExecute(String result) {
            alertDialog.dismiss();

            if (result.contains("success")) {
                Intent i = new Intent(context, MainActivity.class);
                context.startActivity(i);
            } else {
                showAlert("Registration failed", result);
            }
        }

        private void showAlert(String title, String message) {
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle(title);
            alertDialog.setMessage(message);
            alertDialog.show();
        }
    }
}
