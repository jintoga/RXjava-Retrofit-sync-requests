package com.example.dat.rxjavaretrofitsync;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.HashMap;
import java.util.Map;

import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {


    private CompositeSubscription mCompositeSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCompositeSubscription = new CompositeSubscription();
        weatherService = new WeatherService();
        startLoading();
    }

    private WeatherService weatherService;
    private final String city_id_key = "id";
    private final String unit_key = "units";
    private final String lang_key = "lang";
    private final String appid_key = "appid";

    private void startLoading() {
        Map<String, String> params = new HashMap<>();
        params.put(city_id_key, getResources().getString(R.string.barnaul_weather_id));
        params.put(unit_key, getResources().getString(R.string.weather_units));
        params.put(lang_key, getResources().getString(R.string.weather_lang));
        params.put(appid_key, getResources().getString(R.string.open_weather_map_AppID));
        update(params);
    }

    @Override
    protected void onDestroy() {
        mCompositeSubscription.unsubscribe();
        super.onDestroy();
    }

    private static final String KEY_CURRENT_WEATHER = "key_current_weather";
    private static final String KEY_WEATHER_FORECASTS = "key_weather_forecasts";


    private void update(final Map<String, String> params) {

        Observable weathersObservable = Observable.zip(
                weatherService.getAPI().getCurrentWeather(params),
                weatherService.getAPI().getWeathersOfWeek(params),
                new Func2<Response, Response, HashMap<String, Response>>() {
                    @Override
                    public HashMap<String, Response> call(Response response, Response response2) {
                        String res = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.d("res", res);
                        String res2 = new String(((TypedByteArray) response2.getBody()).getBytes());
                        Log.d("res2", res2);

                        HashMap weatherData = new HashMap();
                        weatherData.put(KEY_CURRENT_WEATHER, response);
                        weatherData.put(KEY_WEATHER_FORECASTS, response2);
                        return weatherData;
                    }
                });

        mCompositeSubscription.add(weathersObservable
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<HashMap<String, Response>>() {
                            @Override
                            public void onCompleted() {
                                Log.e("complete", "complete");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("MyError", e.toString());
                            }

                            @Override
                            public void onNext(HashMap<String, Response> data) {
                                Log.i("data", data.toString());
                                Response response = data.get(KEY_CURRENT_WEATHER);
                                Response response2 = data.get(KEY_WEATHER_FORECASTS);


                                String res = new String(((TypedByteArray) response.getBody()).getBytes());
                                Log.d("res", res);
                                String res2 = new String(((TypedByteArray) response2.getBody()).getBytes());
                                Log.d("res2", res2);
                            }
                        })
        );
    }


}
