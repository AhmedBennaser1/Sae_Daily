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
    ImageView back, imgglide, logout;
    EditText nfname, nemail, imgurl, opass, npass, ncpass, nage, phone;
    TextView mail, username;
    Button save;
    String jsonData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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

        jsonData = getIntent().getStringExtra("json_data");
        try {
            JSONObject jsonResponse = new JSONObject(jsonData);
            JSONObject userObject = jsonResponse.getJSONObject("user");

            String name = userObject.getString("name");
            String mailprof = userObject.getString("email");
            String img = userObject.getString("img");
            if(!img.toString().isEmpty()) {
                Glide.with(profile.this).load(img).into(imgglide);
            }
            else{
                Glide.with(profile.this).load(R.drawable.user_default).into(imgglide);
            }

            mail.setText(mailprof);
            username.setText(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(profile.this, acceuil.class);
                home.putExtra("json_data", jsonData);
                startActivity(home);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!nfname.getText().toString().isEmpty()) {
                    String fname = nfname.getText().toString();
                    new UpdateProfile().execute("logout", fname);
                } else {
                    String fname = username.getText().toString();
                    new UpdateProfile().execute("logout", fname);
                }
                Intent i = new Intent(profile.this, MainActivity.class);
                startActivity(i);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = nemail.getText().toString();
                String npassword = npass.getText().toString();
                String opassword = opass.getText().toString();
                String cpassword = ncpass.getText().toString();
                String age = nage.getText().toString();
                String nphone = phone.getText().toString();
                String fname = nfname.getText().toString();
                String oemail = mail.getText().toString();
                String img = imgurl.getText().toString();

                new UpdateProfile().execute("update", oemail, opassword, npassword, cpassword, fname, nphone, age, email, img);
            }
        });
    }

    private class UpdateProfile extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String update_url = "http://10.3.122.105/update2.php";
            String logout_url = "http://10.3.122.105/logout.php";
            String type = params[0];
            if (type.equals("update")) {
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

                    if (cpassword.equals(npassword)) {
                        String data = "email=" + URLEncoder.encode(email, "UTF-8")
                                + "&opass=" + URLEncoder.encode(opassword, "UTF-8")
                                + "&fname=" + URLEncoder.encode(name, "UTF-8")
                                + "&phone=" + URLEncoder.encode(tel, "UTF-8")
                                + "&age=" + URLEncoder.encode(age, "UTF-8")
                                + "&npass=" + URLEncoder.encode(npassword, "UTF-8")
                                + "&imgurl=" + URLEncoder.encode(img, "UTF-8")
                                + "&nemail=" + URLEncoder.encode(nemail, "UTF-8");

                        return executeHttpRequest(update_url, data);
                    } else {
                        return "Passwords do not match";
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return "MalformedURLException: " + e.getMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                    return "IOException: " + e.getMessage();
                }
            } else if (type.equals("logout")) {
                try {
                    String fname = params[1];
                    String data = "username=" + URLEncoder.encode(fname, "UTF-8");

                    return executeHttpRequest(logout_url, data);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return "MalformedURLException: " + e.getMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                    return "IOException: " + e.getMessage();
                }
            }
            return null;
        }

        private String executeHttpRequest(String urlWithParams, String data) throws IOException {
            URL url = new URL(urlWithParams);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(data.getBytes("UTF-8"));
            outputStream.flush();

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
        protected void onPostExecute(String result) {
            AlertDialog alertDialog = new AlertDialog.Builder(profile.this).create();
            alertDialog.setTitle("Profile Update Status");
            alertDialog.setMessage(result);
            alertDialog.show();

            if (result.equals("Update successful")) {
                String fname = !nfname.getText().toString().isEmpty() ? nfname.getText().toString() : username.getText().toString();
                new UpdateProfile().execute("logout", fname);
                Intent i = new Intent(profile.this, MainActivity.class);
                startActivity(i);
            }
        }
    }
}
