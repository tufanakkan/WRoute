package com.example.wroute;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";

    private List<String> mTemperatures = new ArrayList<>();
    private List<String> mMainWeathers = new ArrayList<>();
    private List<String> mLocations = new ArrayList<>();


    public RecyclerViewAdapter(List<String> mTemperatures, List<String> mMainWeathers, List<String> mLocations) {
        this.mTemperatures = mTemperatures;
        this.mMainWeathers = mMainWeathers;
        this.mLocations = mLocations;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        holder.textView_mainWeather.setText(mMainWeathers.get(position));
        holder.textView_location.setText(mLocations.get(position));
        holder.textView_temperature.setText(mTemperatures.get(position));

    }

    @Override
    public int getItemCount() {
        return mLocations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView textView_temperature;
        TextView textView_location;
        TextView textView_mainWeather;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView_temperature = itemView.findViewById(R.id.temperature);
            textView_location = itemView.findViewById(R.id.location);
            textView_mainWeather = itemView.findViewById(R.id.main_weather);

        }
    }
}
