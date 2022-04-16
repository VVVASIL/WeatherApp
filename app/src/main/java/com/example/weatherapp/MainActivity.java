package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText user_field;
    private Button button;
    private TextView information;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user_field = findViewById(R.id.user_field);
        button = findViewById(R.id.button);
        information = findViewById(R.id.information);

        //обработчик события при нажатии на кнопку
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user_field.getText().toString().trim().equals(""))
                // всплывающее окно
                Toast.makeText(MainActivity.this, R.string.error_String, Toast.LENGTH_LONG).show();
                else {
                    String city = user_field.getText().toString();
                    String key = "870311481613e8e57a8dbbccb1093d3c";
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city
                            +"&appid=" + key + "&units=metric&lang=ru";

                    new GetURLData().execute(url);
                }
            }
        });
    }
    private class GetURLData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            information.setText("Ожидайте, пожалуйста...");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null){
                    buffer.append(line).append("\n");
                }
                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (connection!= null){
                    connection.disconnect();
                }else if (reader!= null){
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                information.setText("Температура: " + jsonObject.getJSONObject("main")
                        .getDouble("temp") + " ℃ \n" + "Ощущается как: " +
                        jsonObject.getJSONObject("main")
                        .getDouble("feels_like"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}