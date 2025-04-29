package com.example.anvil.data


import android.location.Location
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locationRules")
data class LocationRule(
    @PrimaryKey(autoGenerate = false)
    val locationName: String = "",
    val ruleCondition: Int = 0,
    val ruleType: Int = 0,
    val ruleBool: Boolean? = null,
    val ruleValue: Int? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val radius: Float = 0f
)

enum class LocationRuleCondition(val value: String) {
    Enter("Enter Location"),
    Leave("Leave Location");
}

enum class LocationRuleType(val value: String) {
    MuteUnmute("mute_unmute"),
    Volume("volume"),
}