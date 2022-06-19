package com.example.travelcultureapplicaiton

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkLocationService{
    @GET("http://api.visitkorea.or.kr/openapi/service/rest/KorService/locationBasedList")

    fun getLocationXmlList(
        @Query("serviceKey") apiKey: String?,
        @Query("pageNo") page:Int,
        @Query("numOfRows") pageSize: Int,
        @Query("MobileOS") OSType: String,
        @Query("MobileApp") appServiceName: String,
        @Query("arrange") arrange: String,
        @Query("contentTypeId") contentTypeId: Int,
        @Query("mapX") mapX: Double,
        @Query("mapY") mapY: Double,
        @Query("radius") radius: Int,
    ) : Call<responseInfo_locate>
}