package com.example.anvil.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appRules")
data class AppRule(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val appName: String = "",
    val packageName: String = "",
    val ruleCondition: Int = 0,
    val ruleType: Int = 0,
    val ruleBool: Boolean? = null,
    val ruleValue: Int? = null
)

enum class RuleCondition(val value: String) {
    Open("Open"),
    Close("Close");
}

enum class RuleType(val value: String) {
    MuteUnmute("mute_unmute"),
    Volume("volume");
}
