package com.example.dat.rxjavaretrofitsync;

import java.util.Map;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;
import retrofit.http.QueryMap;
import rx.Observable;

/**
 * Created by DAT on 20-Jan-16.
 */
public class WeatherService {
    private static final String OPEN_WEATHER_MAP_URL = "http://api.openweathermap.org/data/2.5";
    WeatherAPI weatherAPI;

    public WeatherService() {
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Accept", "application/json");
            }
        };

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(OPEN_WEATHER_MAP_URL)
                .setRequestInterceptor(requestInterceptor)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        weatherAPI = restAdapter.create(WeatherAPI.class);
    }

    public WeatherAPI getAPI() {
        return weatherAPI;
    }


    public interface WeatherAPI {
        @GET("/forecast/daily")
        Observable<Response> getWeathersOfWeek(@QueryMap Map<String, String> params);

        @GET("/weather")
        Observable<Response> getCurrentWeather(@QueryMap Map<String, String> params);


        @GET("/forecast")
        Response getCurrentWeatherByHours(@QueryMap Map<String, String> params);
    }
}
