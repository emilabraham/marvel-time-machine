package com.emilabraham.marveltimemachine

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by eabraham on 1/13/18.
 */
interface MarvelApi {
    @GET("https://gateway.marvel.com:443/v1/public/comics")
    fun getComicByYear(@Query("dateRange") dateRange: String,
                       @Query("apikey") apiKey: String,
                       @Query("ts") timestamp: Long,
                       @Query("hash") hash: String) : Call<MarvelApiResponse>
}