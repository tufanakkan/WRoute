package com.example.wroute;


import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;


public class DownloadTask extends AsyncTask<String, Void, String> {



    @Override
    protected String doInBackground(String... params) {

        String result = "";
        URL url;
        HttpsURLConnection urlConnection = null;

        try{

            url = new URL(params[0]);
            urlConnection = (HttpsURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);

            int data = reader.read();
            while (data != -1){
                char current = (char) data;
                result += current;
                data = reader.read();
            }

            return result;
        }catch(Exception e){
            e.printStackTrace();
            return "Failed!";
        }
    }

}
