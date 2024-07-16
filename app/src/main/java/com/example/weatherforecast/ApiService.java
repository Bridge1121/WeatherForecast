package com.example.weatherforecast;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    //获得中国省份、直辖市、地区和与之对应的ID
    @GET("getRegionProvince")
    Call<String> getRegionProvince();

    //获取对应省份的城市id
    @GET("getSupportCityString")
    Call<String> getSupportCityString(@Query("theRegionCode") Integer theRegionCode);

    //获取对应城市当天的天气预报，userID也可以不传
    @GET("getWeather")
    Call<String> getWeather(@Query("theCityCode") Integer theCityCode,@Query("theUserID") String userID);




}
