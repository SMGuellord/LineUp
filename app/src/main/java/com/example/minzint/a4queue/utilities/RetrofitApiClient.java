package com.example.minzint.a4queue.utilities;

import com.example.minzint.a4queue.ServerDetails;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitApiClient {

    public static Retrofit retrofit;

    public static Retrofit getApiClient(){
        if(retrofit == null){
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                                    .baseUrl(ServerDetails.BASE_URL)
                                    .addConverterFactory(GsonConverterFactory.create(gson)).build();
        }
        return retrofit;
    }
}
