package com.example.anvil.data

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.media.AudioManager
import android.media.AudioManager.ADJUST_MUTE
import android.media.AudioManager.ADJUST_UNMUTE
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class AnvilAccessibilityService : AccessibilityService() {

    @Inject
    lateinit var ruleRepository: rulesRepository


    companion object {
        private const val MILLIS = 5_000L
    }

    val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    lateinit var appRuleList: StateFlow<AppRuleList>

    fun getRuleListStateFlow(): StateFlow<AppRuleList> {
        return ruleRepository.getAllAppRulesStream()
            .map { AppRuleList(it) }
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(MILLIS),
                initialValue = AppRuleList()
            )
    }


    fun saveRule(appRule: AppRule) {
        scope.launch {
            ruleRepository.insertAppRule(appRule)
        }
    }

    fun deleteRule(appRule: AppRule) {
        scope.launch {
            ruleRepository.deleteAppRule(appRule)
        }
    }

    fun updateRule(appRule: AppRule) {
        scope.launch {
            ruleRepository.updateAppRule(appRule)
        }
    }

    fun clearRules() {
        scope.launch {
            ruleRepository.clearAppRules()
        }
    }

    override fun onServiceConnected() {
        appRuleList = getRuleListStateFlow()
        scope.launch {
            appRuleList.last()
        }

    }


    override fun onInterrupt() {}

    var previousWindow by mutableStateOf("")
        private set

    fun updatePreviousWindow(input: String) {
        previousWindow = input
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        if (event?.eventType == AccessibilityEvent.TYPE_WINDOWS_CHANGED && ::ruleRepository.isInitialized == true) {

            Log.d("RunningAppsService", "TYPE_WINDOWS_CHANGED")

        }


        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && ::ruleRepository.isInitialized == true) {


            var runningPackageName = event.packageName.toString()

            event.contentChangeTypes
            Log.d("RunningAppsService", "App name: $runningPackageName")
            Log.d("RunningAppsService", "Is fullScreen: " + event.isFullScreen)

            this
            val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

            for (appRule: AppRule in appRuleList.value.appRules) {
                when (appRule.ruleCondition) {
                    RuleCondition.Open.ordinal -> {
                        if (runningPackageName == appRule.packageName && previousWindow != runningPackageName && event.isFullScreen == true) {
                            when (appRule.ruleType) {
                                RuleType.MuteUnmute.ordinal -> {
                                    when (appRule.ruleBool) {
                                        true -> {
                                            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, ADJUST_MUTE, AudioManager.FLAG_SHOW_UI)
                                            Log.d("RunningAppsService", "Mute on open")
                                        }

                                        false -> {
                                            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, ADJUST_UNMUTE, AudioManager.FLAG_SHOW_UI)
                                            Log.d("RunningAppsService", "Unmute on open")
                                        }

                                        else -> {}
                                    }

                                }

                                RuleType.Volume.ordinal -> {
                                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, appRule.ruleValue!!, AudioManager.FLAG_SHOW_UI)
                                    Log.d("RunningAppsService", "Set volume to " + appRule.ruleValue + " on open")
                                }
                            }
                        }
                    }

                    RuleCondition.Close.ordinal -> {
                        if (runningPackageName != appRule.packageName && previousWindow == appRule.packageName && event.isFullScreen == true) {
                            when (appRule.ruleType) {
                                RuleType.MuteUnmute.ordinal -> {
                                    when (appRule.ruleBool) {
                                        true -> {
                                            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, ADJUST_MUTE, AudioManager.FLAG_SHOW_UI)
                                            Log.d("RunningAppsService", "Mute on close")
                                        }

                                        false -> {
                                            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, ADJUST_UNMUTE, AudioManager.FLAG_SHOW_UI)
                                            Log.d("RunningAppsService", "Unmute on close")
                                        }

                                        else -> {}
                                    }
                                }

                                RuleType.Volume.ordinal -> {
                                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, appRule.ruleValue!!, AudioManager.FLAG_SHOW_UI)
                                    Log.d("RunningAppsService", "Set volume to " + appRule.ruleValue + " on close")
                                }
                            }
                        }
                    }
                }


            }
            if (event.isFullScreen == true) {
                updatePreviousWindow(event.packageName.toString())
            }
            Log.d("RunningAppsService", "    " )
        }



    }

}


fun contentChangeTypeToString(type: Int): String {
    when (type) {
        AccessibilityEvent.CONTENT_CHANGE_TYPE_CONTENT_DESCRIPTION -> return "CONTENT_CHANGE_TYPE_CONTENT_DESCRIPTION"
        AccessibilityEvent.CONTENT_CHANGE_TYPE_STATE_DESCRIPTION -> return "CONTENT_CHANGE_TYPE_STATE_DESCRIPTION"
        AccessibilityEvent.CONTENT_CHANGE_TYPE_SUBTREE -> return "CONTENT_CHANGE_TYPE_SUBTREE"
        AccessibilityEvent.CONTENT_CHANGE_TYPE_TEXT -> return "CONTENT_CHANGE_TYPE_TEXT"
        AccessibilityEvent.CONTENT_CHANGE_TYPE_PANE_TITLE -> return "CONTENT_CHANGE_TYPE_PANE_TITLE"
        AccessibilityEvent.CONTENT_CHANGE_TYPE_UNDEFINED -> return "CONTENT_CHANGE_TYPE_UNDEFINED"
        AccessibilityEvent.CONTENT_CHANGE_TYPE_PANE_APPEARED -> return "CONTENT_CHANGE_TYPE_PANE_APPEARED"
        AccessibilityEvent.CONTENT_CHANGE_TYPE_PANE_DISAPPEARED -> return "CONTENT_CHANGE_TYPE_PANE_DISAPPEARED"
        AccessibilityEvent.CONTENT_CHANGE_TYPE_DRAG_STARTED -> return "CONTENT_CHANGE_TYPE_DRAG_STARTED"
        AccessibilityEvent.CONTENT_CHANGE_TYPE_DRAG_DROPPED -> return "CONTENT_CHANGE_TYPE_DRAG_DROPPED"
        AccessibilityEvent.CONTENT_CHANGE_TYPE_DRAG_CANCELLED -> return "CONTENT_CHANGE_TYPE_DRAG_CANCELLED"
        AccessibilityEvent.CONTENT_CHANGE_TYPE_CONTENT_INVALID -> return "CONTENT_CHANGE_TYPE_CONTENT_INVALID"
        AccessibilityEvent.CONTENT_CHANGE_TYPE_ERROR -> return "CONTENT_CHANGE_TYPE_ERROR"
        AccessibilityEvent.CONTENT_CHANGE_TYPE_ENABLED -> return "CONTENT_CHANGE_TYPE_ENABLED"
        else -> return Integer.toHexString(type)
    }
}

fun windowChangeTypeToString(type: Int): String {
    return when (type) {
        AccessibilityEvent.WINDOWS_CHANGE_ADDED -> "WINDOWS_CHANGE_ADDED"
        AccessibilityEvent.WINDOWS_CHANGE_REMOVED -> "WINDOWS_CHANGE_REMOVED"
        AccessibilityEvent.WINDOWS_CHANGE_TITLE -> "WINDOWS_CHANGE_TITLE"
        AccessibilityEvent.WINDOWS_CHANGE_BOUNDS -> "WINDOWS_CHANGE_BOUNDS"
        AccessibilityEvent.WINDOWS_CHANGE_LAYER -> "WINDOWS_CHANGE_LAYER"
        AccessibilityEvent.WINDOWS_CHANGE_ACTIVE -> "WINDOWS_CHANGE_ACTIVE"
        AccessibilityEvent.WINDOWS_CHANGE_FOCUSED -> "WINDOWS_CHANGE_FOCUSED"
        AccessibilityEvent.WINDOWS_CHANGE_ACCESSIBILITY_FOCUSED -> "WINDOWS_CHANGE_ACCESSIBILITY_FOCUSED"
        AccessibilityEvent.WINDOWS_CHANGE_PARENT -> "WINDOWS_CHANGE_PARENT"
        AccessibilityEvent.WINDOWS_CHANGE_CHILDREN -> "WINDOWS_CHANGE_CHILDREN"
        AccessibilityEvent.WINDOWS_CHANGE_PIP -> "WINDOWS_CHANGE_PIP"
        else -> Integer.toHexString(type)
    }
}