package com.example.weatherforecast.entity;

public class WeatherInfo {
    private String date; // 日期
    private String weatherDesc; // 天气描述
    private String temperatureRange; // 温度范围
    private String windInfo; // 风力信息
    private String weatherIconDay; // 白天天气图标
    private String weatherIconNight; // 晚上天气图标

    public WeatherInfo() {
    }

    // 构造函数
    public WeatherInfo(String date, String weatherDesc, String temperatureRange, String windInfo, String weatherIconDay, String weatherIconNight) {
        this.date = date;
        this.weatherDesc = weatherDesc;
        this.temperatureRange = temperatureRange;
        this.windInfo = windInfo;
        this.weatherIconDay = weatherIconDay;
        this.weatherIconNight = weatherIconNight;
    }

    // Getter和Setter方法
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWeatherDesc() {
        return weatherDesc;
    }

    public void setWeatherDesc(String weatherDesc) {
        this.weatherDesc = weatherDesc;
    }

    public String getTemperatureRange() {
        return temperatureRange;
    }

    public void setTemperatureRange(String temperatureRange) {
        this.temperatureRange = temperatureRange;
    }

    public String getWindInfo() {
        return windInfo;
    }

    public void setWindInfo(String windInfo) {
        this.windInfo = windInfo;
    }

    public String getWeatherIconDay() {
        return weatherIconDay;
    }

    public void setWeatherIconDay(String weatherIconDay) {
        this.weatherIconDay = weatherIconDay;
    }

    public String getWeatherIconNight() {
        return weatherIconNight;
    }

    public void setWeatherIconNight(String weatherIconNight) {
        this.weatherIconNight = weatherIconNight;
    }
}