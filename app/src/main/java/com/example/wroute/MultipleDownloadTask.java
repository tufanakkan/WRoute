package com.example.wroute;

import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;



public class MultipleDownloadTask extends AsyncTask<List<String>, Void, List<String>> {

    String result = "";
    URL url;
    HttpURLConnection urlConnection = null;

    @Override
    protected List<String> doInBackground(List<String>... lists) {

        List<String> resultArr = new ArrayList<>();

        for(int i=0;i<lists[0].size();i++){
            try{
                url = new URL(lists[0].get(i));
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while (data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
            }catch(Exception e){
                e.printStackTrace();
                result = "fail";
            }
            if(result != "fail"){
                resultArr.add(result);
            }else{
                resultArr.add(null);
            }
            result="";
        }

        return resultArr;
    }
}
