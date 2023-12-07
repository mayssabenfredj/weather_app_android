package com.example.mymeteo;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class myAdapter extends RecyclerView.Adapter<myAdapter.ViewHolder>{
    private List<ForecastItem> forecastItemList;
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView day , temperature ;
        ImageView icon ;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            day =itemView.findViewById(R.id.day);
            temperature = itemView.findViewById(R.id.temperature);
            icon = itemView.findViewById(R.id.icon);
        }
    }

    private Context context ;


    public myAdapter(Context c , List<ForecastItem> newsList){

        this.context =c ;
        this.forecastItemList=newsList;
    }

    @NonNull
    @Override
    public myAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.rv_item , parent , false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myAdapter.ViewHolder holder, int position) {
        ForecastItem n = forecastItemList.get(position);
        holder.day.setText(n.getDay());
        holder.temperature.setText(String.format("%.0f°C", n.getTemperature()));
        holder.icon.setImageResource(getDrawableResourceId(n.getIcon()));
    }

    private int getDrawableResourceId(String icon) {
        try {
            Log.d("Icon", "Icon: " + icon);

            return context.getResources().getIdentifier(icon, "drawable", context.getPackageName());
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            e.printStackTrace();
            return R.drawable.a02d;
        }
    }



    @Override
    public int getItemCount() {
        return forecastItemList != null ? forecastItemList.size() : 0;
    }
}
