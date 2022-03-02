package com.hfdevs.bassic.api;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface LyricsAPIInterface {

    @GET("v1/{artistName}/{songName}")
    Call<ResponseBody> getLyrics(
            @Path(value = "artistName") String artistName,
            @Path(value = "songName") String songName
    );


}
