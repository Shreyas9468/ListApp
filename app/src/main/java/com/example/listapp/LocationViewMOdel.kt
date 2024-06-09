package com.example.listapp

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class LocationViewModel : ViewModel() {



    private val _location = mutableStateOf<LocationData?>(null)

    val location : State<LocationData?> = _location

    private val _address = mutableStateOf(listOf<GeocodingResult>())

    val address : State<List<GeocodingResult>> = _address


    fun updateLocation(newLocation : LocationData){
        _location.value = newLocation
    }

    fun fetchaddress(latlng: String){
        try {
         viewModelScope.launch {
            val result = RetrofitClient.create().getAddressFromCoordinates(
                    latlng,
                "jdhjdidfhsjoifjmjg"
            )
             _address.value = result.ressults
         }

        }

        catch (e : Exception){
            Log.d("res1" , "${e.cause} ${e.message}")
        }
    }



}