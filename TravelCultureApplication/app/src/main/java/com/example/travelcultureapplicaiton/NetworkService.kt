package com.example.travelcultureapplicaiton

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkService {
    @GET("openapi/service/rest/KorService/searchFestival")
    fun getXmlList(
        @Query("serviceKey") apiKey: String?,
        @Query("pageNo") page:Int,
        @Query("numOfRows") pageSize: Int,
        @Query("MobileOS") OSType: String,
        @Query("MobileApp") appServiceName: String
    ) : Call<responseInfo>
}