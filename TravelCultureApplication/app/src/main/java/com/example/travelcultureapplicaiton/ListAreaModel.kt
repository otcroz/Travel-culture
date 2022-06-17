package com.example.travelcultureapplicaiton

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

// 불러올 공공데이터의 정보
@Xml(name = "response")
data class responseInfo_area(
    @Element
    val header: Header_area,
    @Element
    val body : myBody_area
)

@Xml(name="header")
data class Header_area(
    @PropertyElement
    val resultCode: Int,
    @PropertyElement
    val resultMsg: String
)

@Xml(name="body")
data class myBody_area(
    @Element
    val items : myItems_area,
    @PropertyElement
    val numOfRows: Int,
    @PropertyElement
    val pageNo: Int,
    @PropertyElement
    val totalCount: Int
)

@Xml(name="items")
data class myItems_area(
    @Element(name="item")
    val item: MutableList<myItem_area>
)

@Xml
data class myItem_area(
    @PropertyElement
    val firstimage: String?,
    @PropertyElement
    val title: String?,
    @PropertyElement
    val addr1: String?,
    @PropertyElement
    val contentid: Int?,

){
    constructor() : this( null, null,null, null)
}