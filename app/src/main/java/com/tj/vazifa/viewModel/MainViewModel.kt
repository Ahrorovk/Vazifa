package com.tj.vazifa.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tj.vazifa.data.local.DataStoreManager
import com.tj.vazifa.data.network.ApplicationApi
import com.tj.vazifa.data.network.StatusResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {
    private val _serverUrlState = mutableStateOf("")

    init {
        dataStoreManager.getServerUrl.onEach { value ->
            _serverUrlState.value = value
        }.launchIn(viewModelScope)
    }

    fun getFromServer() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://vazifa.tj/api/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .header("User-Agent", System.getProperty("http.agent"))
                            .build()
                        chain.proceed(request)
                    }
                    .readTimeout(120, TimeUnit.SECONDS)
                    .connectTimeout(120, TimeUnit.SECONDS)
                    .build()
            )
            .build()
            .create(ApplicationApi::class.java)
        val call = retrofit.getData()
        call.enqueue(object : retrofit2.Callback<StatusResponse> {
            override fun onResponse(
                call: Call<StatusResponse>,
                response: Response<StatusResponse>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()?.status
                    Log.e("Success", "Turned")
                    if (response.code() == 200) {
                        viewModelScope.launch(Dispatchers.IO) {
                            if (data == "false")
                                dataStoreManager.updateIsInServer(2)
                            else if (data == "true") {
                                dataStoreManager.updateIsInServer(2)
                            }
                        }
                    }

                } else {
                    if (response.code() == 403) {
                        viewModelScope.launch(Dispatchers.IO) {
                            dataStoreManager.updateIsInServer(1)
                        }
                    }
                    Log.e(
                        "Error",
                        "${response.message()} \n ${response.code()} \n ${response.errorBody()}"
                    )
                }
            }

            override fun onFailure(call: Call<StatusResponse>, t: Throwable) {
                Log.e("Failure", "${t.message}")
            }
        })
    }
}