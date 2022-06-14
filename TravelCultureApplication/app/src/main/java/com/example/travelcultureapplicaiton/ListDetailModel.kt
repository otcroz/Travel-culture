package com.example.travelcultureapplicaiton

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

// 불러올 공공데이터의 정보
@Xml(name = "response")
data class responseInfo_detail(
    @Element
    val header: Header_detail,
    @Element
    val body : myBody_detail
)

@Xml(name="header")
data class Header_detail(
    @PropertyElement
    val resultCode: Int,
    @PropertyElement
    val resultMsg: String
)

@Xml(name="body")
data class myBody_detail(
    @Element
    val items : myItems_detail,
    @PropertyElement
    val numOfRows: Int,
    @PropertyElement
    val pageNo: Int,
    @PropertyElement
    val totalCount: Int
)

@Xml(name="items")
data class myItems_detail(
    @Element(name="item")
    val item: MutableList<myItem_detail>
)

@Xml
data class myItem_detail(
    @PropertyElement
    val firstimage: String?,
    @PropertyElement
    val title: String?,
    @PropertyElement
    val addr1: String?,
    @PropertyElement
    val overview: String?,
){
    constructor() : this( null, null,null, null)
}

