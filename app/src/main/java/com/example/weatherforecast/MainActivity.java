package com.example.weatherforecast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.weatherforecast.adapter.WeatherInfoAdapter;
import com.example.weatherforecast.entity.City;
import com.example.weatherforecast.entity.Province;
import com.example.weatherforecast.entity.Weather;
import com.example.weatherforecast.entity.WeatherInfo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private AutoCompleteTextView cityEditText;
    private Button searchButton;
    private LinearLayout weatherInfoLayout;
    private TextView provinceCityTextView;
    private TextView districtTextView;
    private TextView reportIdTextView;
    private TextView reportTimeTextView;
    private TextView weatherConditionsTextView;
    private TextView uvIndexTextView;
    private TextView adviceTextView;
    private TextView titleTextView;
    private ListView weatherListView;
    private ApiService apiService;
    private List<WeatherInfo> weatherInfos = new ArrayList<>();//未来五天的天气信息
    private List<City> cities = new ArrayList<>();
    private List<Province> provinces = new ArrayList<>();
    private HashMap<String, Integer> cityMap = new HashMap<>(); // 用于保存城市名称和ID的HashMap
    private Integer selectedCityId;//填写的城市id
    private Weather weather=new Weather();//当天天气信息
    private WeatherInfoAdapter weatherInfoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("中国各城市天气预报查询");
        cityEditText = findViewById(R.id.cityEditText);
        searchButton = findViewById(R.id.searchButton);
        weatherInfoLayout = findViewById(R.id.weatherInfoLayout);
        provinceCityTextView = findViewById(R.id.provinceCityTextView);
        districtTextView = findViewById(R.id.districtTextView);
        reportTimeTextView = findViewById(R.id.reportTimeTextView);
        weatherConditionsTextView = findViewById(R.id.weatherConditionsTextView);
        uvIndexTextView = findViewById(R.id.uvIndexTextView);
        adviceTextView = findViewById(R.id.adviceTextView);
        titleTextView = findViewById(R.id.titleTextView);
        weatherListView = findViewById(R.id.weatherListView);
        apiService = RetrofitClient.getInstance(null).create(ApiService.class);
        apiService.getRegionProvince().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.body()!=null){
                    provinces = parseProvince(response.body());
                    for (Province p:provinces
                         ) {
                        apiService.getSupportCityString(p.getId()).enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.body()!=null){
                                    cities = parseCity(response.body());
                                    for (City city : cities) {
                                        cityMap.put(city.getCity(), city.getId());
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Log.e("城市查询出错啦！！", t.getMessage());
                            }
                        });
                    }
//                    Log.i("省份查询出错啦！！", "hhhhhhh");

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("省份查询出错啦！！", t.getMessage());
            }
        });

        cityEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view;
                String selectedKey = tv.getText().toString();
                cityEditText.setText(selectedKey);
                // 获取选中的频道id
                selectedCityId = cityMap.get(selectedKey); // 注意这里的频道ID类型为Integer
            }
        });

        cityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 根据输入内容过滤提示数据
                ArrayList<String> filteredData = new ArrayList<>();
                for (String key : cityMap.keySet()) {
                    if (key.toLowerCase().contains(s.toString().toLowerCase())) {
                        filteredData.add(key);
                    }
                }

                ArrayAdapter<String> filteredAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, filteredData);
                cityEditText.setAdapter(filteredAdapter);
                cityEditText.setThreshold(1);//设置输入1个字符后开始有提示
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {//点击搜索
            @Override
            public void onClick(View view) {
                if (selectedCityId==null){
                    selectedCityId = cityMap.get(cityEditText.getText());
                }
                apiService.getWeather(selectedCityId,"").enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.body()!=null){
                            parseWeather(response.body());//解析天气信息
                            weatherInfoLayout.setVisibility(View.VISIBLE);
                            provinceCityTextView.setText("省市: "+weather.getProvinceCity());
                            districtTextView.setText("区县: "+weather.getDistrict());
                            reportTimeTextView.setText("报告时间: "+weather.getReportTime());
                            weatherConditionsTextView.setText(weather.getWeatherConditions());
                            uvIndexTextView.setText(weather.getUvIndex());
                            adviceTextView.setText(weather.getAdvice());

                            titleTextView.setVisibility(View.VISIBLE);
                            weatherInfoAdapter = new WeatherInfoAdapter(MainActivity.this,weatherInfos);
                            weatherListView.setAdapter(weatherInfoAdapter);

                        }


                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e("天气预报查询出错啦！！", t.getMessage());
                    }
                });
            }
        });

    }

    //解析获取到的城市地区信息
    public List<City> parseCity(String xmlData) {
        List<City> cityArrayList = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xmlData.getBytes()));

            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("string");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                String[] values = element.getTextContent().split(",");

                City city = new City();
                city.setCity(values[0]);
                city.setId(Integer.parseInt(values[1]));

                cityArrayList.add(city);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return cityArrayList;
    }

    //解析获取到的省份信息
    public List<Province> parseProvince(String xmlData) {
        List<Province> provinceList = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xmlData.getBytes()));

            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("string");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                String[] values = element.getTextContent().split(",");

                Province province = new Province();
                province.setProvince(values[0]);
                province.setId(Integer.parseInt(values[1]));

                provinceList.add(province);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return provinceList;
    }

    public void parseWeather(String xmlData) {

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xmlData.getBytes()));

            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("string");
            Element cityElement = (Element) nodeList.item(0);
            Element districtElement = (Element) nodeList.item(1);
            Element idElement = (Element) nodeList.item(2);
            Element dateTimeElement = (Element) nodeList.item(3);
            Element weatherConditionElement = (Element) nodeList.item(4);
            Element temperatureElement = (Element) nodeList.item(5);
            Element adviceElement = (Element) nodeList.item(6);

            String cityName = cityElement.getTextContent();
            String district = districtElement.getTextContent();
            Integer id = Integer.parseInt(idElement.getTextContent());
            String dateAndTime = dateTimeElement.getTextContent();
            String weatherCondition = weatherConditionElement.getTextContent();
            String temperature = temperatureElement.getTextContent();
            String advice = adviceElement.getTextContent();
            weather.setAdvice(advice);
            weather.setDistrict(district);
            weather.setWeatherConditions(weatherCondition);
            weather.setReportId(id);
            weather.setProvinceCity(cityName);
            weather.setUvIndex(temperature);
            weather.setReportTime(dateAndTime);

            for (int i = 7; i < nodeList.getLength(); i=i+5) {
                Element dateElement = (Element) nodeList.item(i);
                Element temperatureElement1 = (Element) nodeList.item(i + 1);
                Element windElement = (Element) nodeList.item(i + 2);
                Element image1Element = (Element) nodeList.item(i + 3);
                Element image2Element = (Element) nodeList.item(i + 4);

                String date = dateElement.getTextContent();
                String temperature1 = temperatureElement1.getTextContent();
                String wind = windElement.getTextContent();
                String image1 = image1Element.getTextContent();
                String image2 = image2Element.getTextContent();
                WeatherInfo weatherInfo = new WeatherInfo();
                weatherInfo.setDate(date);
                weatherInfo.setWindInfo(wind);
                weatherInfo.setTemperatureRange(temperature1);
                weatherInfos.add(weatherInfo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ;
    }
}

