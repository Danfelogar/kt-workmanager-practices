package app.vercel.danfelogarporfolios.ktworkmanagertasks.ultimateGuide

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET

interface FileApi {

    @GET("/6eyo1w.png")
    suspend fun downloadImage(): Response<ResponseBody>

    companion object {
        val instance by lazy {
            Retrofit.Builder()
                .baseUrl("https://i.imgflip.com")
                .build()
                .create(FileApi::class.java)
        }
    }
}