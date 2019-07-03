package com.example.study_project.model

/**
 * air api data model
 */

data class ResponseData(
    val aqi: Int,
    val idx: Int,
    val attributions: List<AttributionsData>,
    val city: CityData,
    val dominentpol: String,
    val iaqi: Iaqi,
    val time: TimeData,
    val debug: DebugData

)

data class AttributionsData(
    val url: String,
    val name: String
)

data class CityData(
    val geo: List<Double>,
    val name: String,
    val url: String
)

data class Iaqi(
    val co: IaqiData,
    val no2: IaqiData,
    val o3: IaqiData,
    val pm10: IaqiData,
    val pm25: IaqiData,
    val so2: IaqiData
)

data class IaqiData(val v: String)

data class TimeData(
    val s: String,
    val tz: String,
    val v: String
)

data class DebugData(
    val sync: String
)