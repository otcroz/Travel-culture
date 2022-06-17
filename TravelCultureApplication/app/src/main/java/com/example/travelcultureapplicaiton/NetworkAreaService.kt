package com.example.travelcultureapplicaiton

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkAreaService {
    @GET("http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList")

    fun getAreaXmlList(
        @Query("serviceKey") apiKey: String?,
        @Query("pageNo") page:Int,
        @Query("numOfRows") pageSize: Int,
        @Query("MobileOS") OSType: String,
        @Query("MobileApp") appServiceName: String,
        @Query("arrange") arrange: String,
        @Query("areaCode") areaCode: Int,
        @Query("cat1") cat1: String,
        @Query("cat2") cat2: String,
        @Query("contentTypeId") contentTypeId: Int,

    ) : Call<responseInfo_area>
}