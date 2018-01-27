package com.emilabraham.marveltimemachine

import com.mcxiaoke.koi.HASH
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.logging.Logger

/**
 * Created by eabraham on 1/13/18.
 */
class RestApi {
    private val marvelApi: MarvelApi
    private val log = Logger.getLogger(RestApi::class.java.name)

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://gateway.marvel.com:443")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        marvelApi = retrofit.create(MarvelApi::class.java)
    }

    fun getComic(dateRange: String): Call<MarvelApiResponse> {
        val publicapiKey = "70983eedc7d37af9a72b4da32798ccf0"
        val privateapikey = "4a5a43467f9c234cb5d47860b9f8c3a9675f1766"
        val timestamp = Date()
        val hash = HASH.md5(timestamp.time.toString() + privateapikey + publicapiKey)
        return marvelApi.getComicByYear(dateRange, publicapiKey, timestamp.time, hash)
    }
}