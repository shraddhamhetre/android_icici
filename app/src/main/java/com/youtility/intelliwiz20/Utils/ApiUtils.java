package com.youtility.intelliwiz20.Utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiUtils {

    /*public static RetrofitServices getUserService(){
        return RetrofitClient.getClient(Constants.BASE_URL).create(RetrofitServices.class);
    }*/

    private static Retrofit retrofit;
    private static final String BASE_URL = Constants.BASE_URL;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
