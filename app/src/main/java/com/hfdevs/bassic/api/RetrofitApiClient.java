package com.hfdevs.bassic.api;

import androidx.annotation.NonNull;

import com.hfdevs.bassic.utils.Constants;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

public class RetrofitApiClient {

    private static Retrofit retrofit = null;
    private static LyricsAPIInterface lyricsAPIInterface;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .callTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .addInterceptor(interceptor)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .addConverterFactory(ScalarsConverterFactory.create())
                    .client(client)
                    .build();
        }

        return retrofit;
    }

    public static LyricsAPIInterface getLyricsAPIInterface() {
        if(lyricsAPIInterface == null){
            lyricsAPIInterface = getInstance().create(LyricsAPIInterface.class);
        }
        return lyricsAPIInterface;
    }
}
