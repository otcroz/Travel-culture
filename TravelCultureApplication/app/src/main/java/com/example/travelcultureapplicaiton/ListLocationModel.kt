package com.example.travelcultureapplicaiton

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

// 불러올 공공데이터의 정보
@Xml(name = "response")
data class responseInfo_locate(
    @Element
    val header: Header_locate,
    @Element
    val body : myBody_locate
)

@Xml(name="header")
data class Header_locate(
    @PropertyElement
    val resultCode: Int,
    @PropertyElement
    val resultMsg: String
)

@Xml(name="body")
data class myBody_locate(
    @Element
    val items : myItems_locate,
    @PropertyElement
    val numOfRows: Int,
    @PropertyElement
    val pageNo: Int,
    @PropertyElement
    val totalCount: Int
)

@Xml(name="items")
data class myItems_locate(
    @Element(name="item")
    val item: MutableList<myItem_locate>
)

@Xml
data class myItem_locate(
    @PropertyElement
    val firstimage2: String?,
    @PropertyElement
    val title: String?,
    @PropertyElement
    val addr1: String?,
){
    constructor() : this( null,null, null)
}

