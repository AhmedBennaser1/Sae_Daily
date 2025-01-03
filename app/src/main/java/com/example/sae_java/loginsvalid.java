package com.example.sae_java;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

class LoginsValid extends AsyncTask<String, Void, String> {
    private Context context;
    private AlertDialog alertDialog;

    LoginsValid(Context ctx) {
        context = ctx;
    }

    @Override
    protected String doInBackground(String... params) {
        String login_url = "http://10.3.122.105/login.php";

        try {
            String email = params[0];
            String password = params[1];

            String urlWithParams = login_url + "?email=" + URLEncoder.encode(email, "UTF-8")
                    + "&password=" + URLEncoder.encode(password, "UTF-8");

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
        HttpURLConnection httpURLConnection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(urlWithParams);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);

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
        alertDialog.setMessage("Logging in...");
    }

    @Override
    protected void onPostExecute(String result) {
        alertDialog.dismiss();

        try {
            JSONObject jsonResponse = new JSONObject(result);
            String status = jsonResponse.optString("status");

            if ("success".equalsIgnoreCase(status)) {
                Intent i = new Intent(context, acceuil.class);
                i.putExtra("json_data", result);
                context.startActivity(i);
                Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show();
            } else {
                String message = jsonResponse.optString("message");
                alertDialog.setMessage("Login failed: " + message);
                alertDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            alertDialog.setMessage("Error: " + e.getMessage());
            alertDialog.show();
        }
    }
}
