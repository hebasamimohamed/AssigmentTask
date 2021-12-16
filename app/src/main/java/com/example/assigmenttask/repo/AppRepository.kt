package com.example.assigmenttask.repo

import com.example.assigmenttask.network.RetrofitInstance

class AppRepository {

    suspend fun getLiveNews(q: String, from: String, sortBy: String) =
        RetrofitInstance.newsApi.getLiveNews(q, from, sortBy)

}