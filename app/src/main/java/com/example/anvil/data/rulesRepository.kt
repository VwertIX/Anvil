package com.example.anvil.data

import kotlinx.coroutines.flow.Flow


/**
 * Repository that provides insert, update, delete, and retrieve of [AppRule] from a given data source.
 */
interface rulesRepository {

    fun getAllAppRulesStream(): Flow<List<AppRule>>

    fun getAppRuleByIdStream(id: Int): Flow<AppRule?>

    fun getAppRuleByAppNameStream(appName: String): Flow<AppRule?>

    fun getAppRuleByPackageNameStream(packageName: String): Flow<AppRule?>

    suspend fun clearAppRules()

    suspend fun insertAppRule(appRule: AppRule)

    suspend fun deleteAppRule(appRule: AppRule)

    suspend fun updateAppRule(appRule: AppRule)





    fun getAllLocationRulesStream(): Flow<List<LocationRule>>

    fun getLocationRuleByLocationNameStream(locationName: String): Flow<LocationRule?>

    suspend fun clearLocationRules()

    suspend fun insertLocationRule(locationRule: LocationRule)

    suspend fun deleteLocationRule(locationRule: LocationRule)

    suspend fun updateLocationRule(locationRule: LocationRule)

}