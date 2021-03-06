package com.example.assigmenttask.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.assigmenttask.R
import com.example.assigmenttask.app.MyApplication
import com.example.assigmenttask.models.NewsResponse
import com.example.assigmenttask.repo.AppRepository
import com.example.assigmenttask.utils.Constants
import com.example.assigmenttask.utils.Resource
import com.example.ihtask.utils.Utils.hasInternetConnection
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class NewsViewModel(
    app: Application,
    private val appRepository: AppRepository
) : AndroidViewModel(app) {
    val articlesDATA: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()

    init {
        getArticles()
    }

    fun getArticles() = viewModelScope.launch {
        fetchArticles()
    }


    private suspend fun fetchArticles() {
        articlesDATA.postValue(Resource.Loading())
        try {
            if (hasInternetConnection(getApplication<MyApplication>())) {
                val response = appRepository.getLiveNews(
                    Constants.VERISION, SimpleDateFormat(Constants.ARTICLE_DATE, Locale.getDefault()).format(
                        Date()
                    ), Constants.SORT_BY
                )
                articlesDATA.postValue(handlePicsResponse(response))
            } else {
                articlesDATA.postValue(Resource.Error(getApplication<MyApplication>().getString(R.string.no_internet_connection)))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> articlesDATA.postValue(
                    Resource.Error(
                        getApplication<MyApplication>().getString(
                            R.string.network_failure
                        )
                    )
                )
                else -> articlesDATA.postValue(
                    Resource.Error(
                        getApplication<MyApplication>().getString(
                            R.string.conversion_error
                        )
                    )
                )
            }
        }
    }
    private fun handlePicsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}