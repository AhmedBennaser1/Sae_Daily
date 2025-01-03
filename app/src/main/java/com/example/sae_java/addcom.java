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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class addcom extends AppCompatActivity {
    String jsonData;

    EditText imgurl, lien, titre, descrip;
    ImageView imgglide, back;

    TextView username, mail;

    Button journee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcom);

        imgglide = findViewById(R.id.picom);
        mail = findViewById(R.id.emailcom);
        username = findViewById(R.id.usercom);
        back = findViewById(R.id.homecom);
        journee = findViewById(R.id.journeebutton);
        lien = findViewById(R.id.lien);
        imgurl = findViewById(R.id.url);
        titre = findViewById(R.id.titre);
        descrip = findViewById(R.id.description);

        jsonData = getIntent().getStringExtra("json_data");

        try {
            JSONObject jsonResponse = new JSONObject(jsonData);
            JSONObject userObject = jsonResponse.getJSONObject("user");

            String name = userObject.getString("name");
            String mailprof = userObject.getString("email");
            String img = userObject.getString("img");

            Glide.with(addcom.this).load(img).into(imgglide);

            mail.setText(mailprof);
            username.setText(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(addcom.this, acceuil.class);
                home.putExtra("json_data", jsonData);
                startActivity(home);
            }
        });

        journee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String link = lien.getText().toString();
                String img = imgurl.getText().toString();
                String title = titre.getText().toString();
                String description = descrip.getText().toString();

                new addjournee().execute(link, img, title, description);
            }
        });
    }

    private class addjournee extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String com_url = "http://10.3.122.105/addjournee.php";
            try {
                String lien = params[0];
                String img = params[1];
                String title = params[2];
                String description = params[3];

                String urlWithParams = com_url + "?url=" + URLEncoder.encode(lien, "UTF-8")
                        + "&titre=" + URLEncoder.encode(description, "UTF-8")
                        + "&descrip=" + URLEncoder.encode(title, "UTF-8")
                        + "&imgurl=" + URLEncoder.encode(img, "UTF-8");

                return executeHttpRequest(urlWithParams);

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "MalformedURLException: " + e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return "IOException: " + e.getMessage();
            }
        }

        private String executeHttpRequest(String urlWithParams) throws IOException {
            URL url = new URL(urlWithParams);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);

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
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(String result) {
            // Show an alert dialog to indicate success or failure
            AlertDialog alertDialog = new AlertDialog.Builder(addcom.this).create();
            alertDialog.setTitle("Journee added successfully");
            alertDialog.setMessage(result);
            alertDialog.show();

            if (result.equals("Journee ajoutee")) {
                try {
                    JSONObject newJournee = new JSONObject();
                    newJournee.put("titre", titre.getText().toString());
                    newJournee.put("description", descrip.getText().toString());
                    newJournee.put("lien", lien.getText().toString());
                    newJournee.put("image_link", imgurl.getText().toString());

                    JSONObject jsonResponse = new JSONObject(jsonData);
                    JSONArray dataArray = jsonResponse.getJSONArray("data");

                    dataArray.put(newJournee);

                    jsonResponse.put("data", dataArray);

                    Log.d("Updated JSON", jsonResponse.toString());

                    Intent i = new Intent(addcom.this, acceuil.class);
                    i.putExtra("json_data", jsonResponse.toString());
                    startActivity(i);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            // Optionally, update UI to show progress
        }
    }
}
