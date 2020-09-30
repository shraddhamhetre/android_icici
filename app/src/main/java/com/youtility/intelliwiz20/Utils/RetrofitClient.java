package com.youtility.intelliwiz20.Utils;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    /*private static Retrofit retrofit = null;

    public static Retrofit getClient(String url){
        if(retrofit == null){
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            Retrofit.Builder builder =
                    new Retrofit.Builder()
                            .baseUrl(url)
                            .addConverterFactory(
                                    GsonConverterFactory.create()
                            );
            retrofit =
                    builder
                            .client(
                                    httpClient.build()
                            )
                            .build();

            *//*retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create(new Gson()))
                    .build();*//*
        }
        return retrofit;
    }*/

    private static Retrofit retrofit=null ;


    public static Retrofit getClient() {
        /*System.out.println("BaseURL: "+Constants.BASE_URL);
        if (retrofit==null) {

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;*/
        System.out.println("BaseURL:1 "+Constants.BASE_URL);
        if (Constants.BASE_URL== ""){
            System.out.println("BaseURL is null");
            if (retrofit==null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl("https://intelliwiz.youtility.in/service/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }

        }else if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

       /* if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }*/
        return retrofit;
    }

}
