package com.emilabraham.marveltimemachine

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Calls to the Marvel API.
 */
interface MarvelApi {
    @GET("https://gateway.marvel.com:443/v1/public/comics")
    fun getComicByYear(@Query("dateRange") dateRange: String,
                       @Query("apikey") apiKey: String,
                       @Query("ts") timestamp: Long,
                       @Query("hash") hash: String) : Call<MarvelApiResponse>
}