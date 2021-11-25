package com.example.jsonparsingtraining;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.jsonparsingtraining.databinding.ActivityMainBinding;

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
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding; //3rd step
    Handler mainHandler = new Handler();
    ProgressDialog progressDialog;
    ArrayList<String> userList;
    ArrayAdapter<String> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        inisializeUserList(); //create array_adapter

        binding.fectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new fectData().start(); //get json data
            }
        });


    }

    private void inisializeUserList() { //5rd step
        userList = new ArrayList<>();
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, userList);
        binding.listData.setAdapter(listAdapter);

        binding.listData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "click", Toast.LENGTH_SHORT).show();
                ; //show
            }
        });
    }

    class fectData extends Thread { //4rd step #main class that implements read the data and store into arraylist

        String data = ""; // inside this string variable will read hold json data


        @Override
        public void run() {

            mainHandler.post(new Runnable() { //progressDialog bar appearance on button click
                @Override
                public void run() {
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Loading");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                }
            });

            try {
                URL url = new URL("https://api.npoint.io/c2639ca370bbe424c062");  //create object urlConnection
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection(); //create object and open urlConnection
                InputStream inputStream = httpURLConnection.getInputStream(); //create object inputStream to read data from http
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream)); //READ data from INputStreamReader
                String line; //json data is string this create temporary string variable

                while ((line = bufferedReader.readLine()) != null) {
                    data = data + line;
                }

                if (!data.isEmpty()) { //check if json data is not empty and return json data
                    JSONObject jsonObject = new JSONObject(data);
                    JSONArray users = jsonObject.getJSONArray("Users");
                    userList.clear(); //will delete data if press button
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject user = users.getJSONObject(i);
                        String name = user.getString("name"); //json is data type array
                        userList.add(name); //store data into array

                    }

                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    listAdapter.notifyDataSetChanged(); //notif adapter that data has changed, and refresh data listview
                }
            });
        }
    }
}