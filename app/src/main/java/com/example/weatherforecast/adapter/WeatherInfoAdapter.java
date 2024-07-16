package com.example.weatherforecast.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.weatherforecast.R;
import com.example.weatherforecast.entity.WeatherInfo;

import java.util.List;

public class WeatherInfoAdapter extends BaseAdapter {
    private Context context;
    private List<WeatherInfo> weatherInfoList;

    public WeatherInfoAdapter(Context context, List<WeatherInfo> weatherInfoList) {
        this.context = context;
        this.weatherInfoList = weatherInfoList;
    }

    @Override
    public int getCount() {
        return weatherInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return weatherInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_weather_info, parent, false);
            holder = new ViewHolder();
            holder.dateTextView = convertView.findViewById(R.id.dateTextView);
            holder.temperatureTextView = convertView.findViewById(R.id.temperatureTextView);
            holder.windTextView = convertView.findViewById(R.id.windTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        WeatherInfo weatherInfo = weatherInfoList.get(position);

        holder.dateTextView.setText(weatherInfo.getDate());
        holder.temperatureTextView.setText(weatherInfo.getTemperatureRange());
        holder.windTextView.setText(weatherInfo.getWindInfo());

        return convertView;
    }

    static class ViewHolder {
        TextView dateTextView;
        TextView temperatureTextView;
        TextView windTextView;
    }
}