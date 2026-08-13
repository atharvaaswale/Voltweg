package com.voltweg.core.network

import com.voltweg.BuildConfig
import java.util.concurrent.TimeUnit
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/**
 * Manual dependency provider for the Open Charge Map network layer.
 * (The project does not use Hilt; keep wiring explicit and lazy.)
 */
object NetworkModule {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", "Voltweg/1.0 (Android)")
                    .header("X-API-Key", BuildConfig.OCM_API_KEY)
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(
                HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
            )
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openchargemap.io/v3/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    val openChargeMapApi: OpenChargeMapApi by lazy {
        retrofit.create(OpenChargeMapApi::class.java)
    }

    val nominatimRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    val nominatimApi: NominatimApi by lazy {
        nominatimRetrofit.create(NominatimApi::class.java)
    }
}
