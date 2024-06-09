package com.example.listapp

data class LocationData(
    val latitude : Double,
    val longitude : Double
)

data class GeoCodingResponces(
    val ressults : List<GeocodingResult>,
    val status : String
)

data class GeocodingResult(
    val formatted_address : String
)