package com.example.travelcultureapplicaiton

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkDetailService {
    @GET("http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailCommon")

    fun getDetailXmlList(
        @Query("serviceKey") apiKey: String?,
        @Query("pageNo") page:Int,
        @Query("numOfRows") pageSize: Int,
        @Query("MobileOS") OSType: String,
        @Query("MobileApp") appServiceName: String,
        @Query("contentId") contentId: Int,
        @Query("defaultYN") defaultYN: String,
        @Query("firstImageYN") firstImageYN: String,
        @Query("addrinfoYN") addrinfoYN: String,
        @Query("mapinfoYN") mapinfoYN: String,
        @Query("overviewYN") overviewYN: String,

    ) : Call<responseInfo_detail>
}