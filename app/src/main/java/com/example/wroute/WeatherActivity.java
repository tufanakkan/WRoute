package com.example.wroute;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.LauncherActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class WeatherActivity extends AppCompatActivity {
    ArrayList<LatLng> pointsArr = new ArrayList<>();
    ArrayList<LatLng> selectedArr = new ArrayList<>();
    List<String> locationNames = new ArrayList<>();
    List<String> temperatures = new ArrayList<>();
    List<String> mainWeathers = new ArrayList<>();
    List<String> jsonStrings = new ArrayList<>();
    List<String> urls = new ArrayList<>();
    public static final int divider = 300;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        String url ="";
        String result="";
        HashMap<String,String> parsedValues = new HashMap<>();
        MultipleDownloadTask task = new MultipleDownloadTask();

        Intent intent = getIntent();
        pointsArr = (ArrayList<LatLng>) intent.getSerializableExtra("latlngArray");
        Log.i("pointsarr", "pointsarr: " + pointsArr.size());
        if(pointsArr.size()/divider<1){
            Log.i("distance", "Mesafe çok kisa!");
        }else{
            selectedArr = pointsSelector(pointsArr);

            for(int i=0;i<selectedArr.size();i++){
                urls.add(makeWeatherUrl(selectedArr.get(i)));
            }
            try {
                jsonStrings = task.execute(urls).get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for(int i=0;i<jsonStrings.size();i++){
                parsedValues = parser(jsonStrings.get(i));
                temperatures.add(parsedValues.get("temp"));
                locationNames.add(parsedValues.get("name"));
                mainWeathers.add(parsedValues.get("main"));
            }

            initRecyclerView();



        }
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(temperatures,mainWeathers,locationNames);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private String makeWeatherUrl(LatLng place) {
        String result = "http://api.openweathermap.org/data/2.5/weather?lat="
                +place.latitude+
                "&lon="
                +place.longitude+
                "&units=metric&appid=5c12ab09fac5f9a4b6003029864d2188";
        return result;
    }

    private ArrayList<LatLng> pointsSelector(ArrayList<LatLng> arrayList){

        ArrayList<LatLng> selectedArr = new ArrayList<>();
        int increase = arrayList.size()/(1+(arrayList.size()/divider));
        for(int i=0;i<=arrayList.size();i+=increase){

            if(i==arrayList.size()){
                selectedArr.add(arrayList.get(i-1));
            }
            else{
                selectedArr.add(arrayList.get(i));
            }

        }
        return selectedArr;
    }

    private HashMap<String,String> parser (String result){

        HashMap<String,String> values = new HashMap<>();

        try {
            JSONObject jsonObject = new JSONObject(result);

            JSONObject main = (JSONObject) jsonObject.get("main");
            values.put("temp",main.getString("temp"));

            values.put("name", jsonObject.getString("name"));

            JSONArray weatherArr = (JSONArray) jsonObject.get("weather");
            JSONObject weather = (JSONObject) weatherArr.get(0);
            values.put("main", weather.getString("main"));



        } catch (JSONException e) {
            e.printStackTrace();
        }

        return values;
    }



}


