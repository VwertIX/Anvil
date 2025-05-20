package com.example.anvil.ui

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import com.example.anvil.R
import com.example.anvil.data.AppRule
import com.example.anvil.data.RuleCondition
import com.example.anvil.data.RuleType
import com.example.anvil.data.AppRuleList
import com.example.anvil.data.rulesRepository
import com.example.anvil.data.LocationRule
import com.example.anvil.data.LocationRuleCondition
import com.example.anvil.data.LocationRuleList
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject




@HiltViewModel
class AnvilViewModel @Inject constructor(private val ruleRepository: rulesRepository) : ViewModel() {
    companion object {
        private const val MILLIS = 5_000L
    }
    val getAllAppRules: StateFlow<AppRuleList> =
        ruleRepository.getAllAppRulesStream()
            .map { AppRuleList(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(MILLIS),
                initialValue = AppRuleList()
            )

    fun saveAppRule(appRule: AppRule) {
        viewModelScope.launch {
            ruleRepository.insertAppRule(appRule)
        }
    }
    fun deleteAppRule(appRule: AppRule) {
        viewModelScope.launch {
            ruleRepository.deleteAppRule(appRule)
        }
    }
    fun updateAppRule(appRule: AppRule) {
        viewModelScope.launch {
            ruleRepository.updateAppRule(appRule)
        }
    }
    fun updateAppRuleFromViewmodel() {
        viewModelScope.launch {
            ruleRepository.updateAppRule(appRule.value)
        }
    }

    fun clearRules() {
        viewModelScope.launch {
            ruleRepository.clearAppRules()
        }
        viewModelScope.launch {
            ruleRepository.clearLocationRules()
        }
    }

    private val _switch = MutableStateFlow(true)
    val switch: StateFlow<Boolean> = _switch

    fun updateSwitch(newSwitch: Boolean) {
        _switch.value = newSwitch
    }




    var searchText by mutableStateOf("")
        private set

    fun updateSearchText(input: String) {
        searchText = input
    }

    var installedPackages: List<PackageInfo> = emptyList()




    private val _appRule = MutableStateFlow(AppRule())
    val appRule: StateFlow<AppRule> = _appRule


    fun updateAppRule(id: Int, appName: String, packageName: String, ruleCondition: Int, ruleType: Int, ruleBool: Boolean?, ruleValue: Int?) {
        _appRule.value = AppRule(id, appName, packageName, ruleCondition, ruleType, ruleBool, ruleValue)
    }

    fun updateAppRuleFromAppRule(appRule: AppRule) {
        _appRule.value = appRule
    }

    fun updateAppRuleAppName(appName: String) {
        _appRule.value = _appRule.value.copy(appName = appName)
    }

    fun updateAppRulePackageName(packageName: String) {
        _appRule.value = _appRule.value.copy(packageName = packageName)
    }
    fun updateAppRuleCondition(ruleCondition: Int) {
        _appRule.value = _appRule.value.copy(ruleCondition = ruleCondition)
    }
    fun updateAppRuleCondition(ruleCondition: Int, ruleBool: Boolean?, ruleValue: Int?) {
        _appRule.value = _appRule.value.copy(ruleCondition = ruleCondition, ruleBool = ruleBool, ruleValue = ruleValue)
    }

    fun updateAppRuleType(ruleType: Int) {
        _appRule.value = _appRule.value.copy(ruleType = ruleType)
    }
    fun updateAppRuleType(ruleType: Int, ruleBool: Boolean?, ruleValue: Int?) {
        _appRule.value = _appRule.value.copy(ruleType = ruleType, ruleBool = ruleBool, ruleValue = ruleValue)
    }
    fun updateAppRuleType(ruleType: Int, ruleValue: Int?) {
        _appRule.value = _appRule.value.copy(ruleType = ruleType, ruleValue = ruleValue)
    }
    fun updateAppRuleBool(ruleBool: Boolean?) {
        _appRule.value = _appRule.value.copy(ruleBool = ruleBool)
    }
    fun updateAppRuleBool(ruleBool: Boolean?, ruleValue: Int?) {
        _appRule.value = _appRule.value.copy(ruleBool = ruleBool, ruleValue = ruleValue)
    }
    fun updateAppRuleValue(ruleValue: Int?) {
        _appRule.value = _appRule.value.copy(ruleValue = ruleValue)
    }

    fun checkAppRuleInput(choice: String, context: Context) {
        when (choice) {
            context.getString(R.string.mute_unmute) -> updateAppRuleType(RuleType.MuteUnmute.ordinal, ruleBool = true, ruleValue = null)
            context.getString(R.string.volume) -> updateAppRuleType(RuleType.Volume.ordinal, ruleBool = null, ruleValue = 0)

            context.getString(R.string.open) -> updateAppRuleCondition(ruleCondition = RuleCondition.Open.ordinal)
            context.getString(R.string.close) -> updateAppRuleCondition(ruleCondition = RuleCondition.Close.ordinal)

            context.getString(R.string.mute) -> updateAppRuleBool(ruleBool = true, ruleValue = null)
            context.getString(R.string.unmute) -> updateAppRuleBool(ruleBool = false, ruleValue = null)
        }
    }

    fun resetRule(ruleCondition: String) {
        _appRule.value = AppRule()
    }







    private val _locationRule = MutableStateFlow(LocationRule())
    val locationRule: StateFlow<LocationRule> = _locationRule


    val allLocationRules: StateFlow<LocationRuleList> =
        ruleRepository.getAllLocationRulesStream()
            .map { LocationRuleList(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(MILLIS),
                initialValue = LocationRuleList()
            )

    fun saveLocationRule(locationRule: LocationRule) {
        viewModelScope.launch {
            ruleRepository.insertLocationRule(locationRule)
        }
    }
    fun deleteLocationRule(locationRule: LocationRule) {
        viewModelScope.launch {
            ruleRepository.deleteLocationRule(locationRule)
        }
    }
    fun updateLocationRule(locationRule: LocationRule) {
        viewModelScope.launch {
            ruleRepository.updateLocationRule(locationRule)
        }
    }
    fun updateLocationRuleFromViewmodel() {
        viewModelScope.launch {
            ruleRepository.updateLocationRule(locationRule.value)
        }
    }



    fun updateLocationRule(locationName: String, ruleCondition: Int, ruleType: Int, ruleBool: Boolean?, ruleValue: Int?, latitude: Double, longitude: Double, radius: Float) {
        _locationRule.value = LocationRule(locationName, ruleCondition, ruleType, ruleBool, ruleValue, latitude, longitude, radius)
    }

    fun updateLocationRuleFromRule(locationRule: LocationRule) {
        _locationRule.value = locationRule
    }

    fun updateLocationRuleLocationName(locationName: String) {
        _locationRule.value = _locationRule.value.copy(locationName = locationName)
    }

    fun updateLocationRuleCondition(ruleCondition: Int) {
        _locationRule.value = _locationRule.value.copy(ruleCondition = ruleCondition)
    }
    fun updateLocationRuleCondition(ruleCondition: Int, ruleBool: Boolean?, ruleValue: Int?) {
        _locationRule.value = _locationRule.value.copy(ruleCondition = ruleCondition, ruleBool = ruleBool, ruleValue = ruleValue)
    }

    fun updateLocationRuleType(ruleType: Int) {
        _locationRule.value = _locationRule.value.copy(ruleType = ruleType)
    }
    fun updateLocationRuleType(ruleType: Int, ruleBool: Boolean?, ruleValue: Int?) {
        _locationRule.value = _locationRule.value.copy(ruleType = ruleType, ruleBool = ruleBool, ruleValue = ruleValue)
    }
    fun updateLocationRuleType(ruleType: Int, ruleValue: Int?) {
        _locationRule.value = _locationRule.value.copy(ruleType = ruleType, ruleValue = ruleValue)
    }
    fun updateLocationRuleBool(ruleBool: Boolean?) {
        _locationRule.value = _locationRule.value.copy(ruleBool = ruleBool)
    }
    fun updateLocationRuleBool(ruleBool: Boolean?, ruleValue: Int?) {
        _locationRule.value = _locationRule.value.copy(ruleBool = ruleBool, ruleValue = ruleValue)
    }
    fun updateLocationRuleValue(ruleValue: Int?) {
        _locationRule.value = _locationRule.value.copy(ruleValue = ruleValue)
    }

    fun updateLocationRuleLongitude(longitude: Double) {
        _locationRule.value = _locationRule.value.copy(longitude = longitude)
    }

    fun updateLocationRuleLatitude(latitude: Double) {
        _locationRule.value = _locationRule.value.copy(latitude = latitude)
    }
    fun updateLocationRuleLatLong(latitude: Double, longitude: Double) {
        _locationRule.value = _locationRule.value.copy(latitude = latitude, longitude = longitude)
    }
    fun updateLocationRuleLatLongRad(latitude: Double, longitude: Double, radius: Float) {
        _locationRule.value = _locationRule.value.copy(latitude = latitude, longitude = longitude, radius = radius)
    }
    fun updateLocationRuleRadius(radius: Float) {
        _locationRule.value = _locationRule.value.copy(radius = radius)
    }
    fun checkLocationRuleInput(choice: String, context: Context) {
        when (choice) {
            context.getString(R.string.mute_unmute) -> updateLocationRuleType(RuleType.MuteUnmute.ordinal, ruleBool = true, ruleValue = null)
            context.getString(R.string.volume) -> updateLocationRuleType(RuleType.Volume.ordinal, ruleBool = null, ruleValue = 0)

            context.getString(R.string.enter) -> updateLocationRuleCondition(ruleCondition = LocationRuleCondition.Enter.ordinal)
            context.getString(R.string.leave) -> updateLocationRuleCondition(ruleCondition = LocationRuleCondition.Leave.ordinal)

            context.getString(R.string.mute) -> updateLocationRuleBool(ruleBool = true, ruleValue = null)
            context.getString(R.string.unmute) -> updateLocationRuleBool(ruleBool = false, ruleValue = null)
        }
    }

    // State to hold the user's location as LatLng (latitude and longitude)


    private val _userLocation: MutableStateFlow<LatLng?> = MutableStateFlow(null)
    val userLocation: StateFlow<LatLng?> = _userLocation


    // Function to fetch the user's location and update the state
    fun fetchUserLocation(context: Context, fusedLocationClient: FusedLocationProviderClient) {
        // Check if the location permission is granted
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {

                // Fetch the last known location
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        // Update the user's location in the state
                        val userLatLng = LatLng(it.latitude, it.longitude)
                        _userLocation.value = userLatLng
                        Log.d("Success", "Location found")
                    }
                }
            } catch (e: SecurityException) {
                Log.e("Error", "Permission for location access was revoked: ${e.localizedMessage}")
            }
        } else {
            Log.e("Error", "Location permission is not granted.")
        }
    }

}
