package com.example.anvil.data


import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class rulesRepositoryImp @Inject constructor(private val ruleDao: RulesDao) : rulesRepository {
    override fun getAllAppRulesStream(): Flow<List<AppRule>> = ruleDao.getAllAppRules()

    override fun getAppRuleByIdStream(id: Int): Flow<AppRule?> = ruleDao.getAppRuleById(id)

    override fun getAppRuleByAppNameStream(appName: String): Flow<AppRule?> = ruleDao.getAppRuleByAppName(appName)

    override fun getAppRuleByPackageNameStream(packageName: String): Flow<AppRule?> = ruleDao.getAppRuleByPackageName(packageName)

    override suspend fun clearAppRules() = ruleDao.clearAppRules()

    override suspend fun insertAppRule(appRule: AppRule) = ruleDao.insertApp(appRule)

    override suspend fun deleteAppRule(appRule: AppRule) = ruleDao.deleteApp(appRule)

    override suspend fun updateAppRule(appRule: AppRule) = ruleDao.updateApp(appRule)





    override fun getAllLocationRulesStream(): Flow<List<LocationRule>> = ruleDao.getAllLocationRules()

    override fun getLocationRuleByLocationNameStream(locationName: String): Flow<LocationRule?> = ruleDao.getLocationRuleByLocationName(locationName)

    override suspend fun clearLocationRules() = ruleDao.clearLocationRules()

    override suspend fun insertLocationRule(locationRule: LocationRule) = ruleDao.insertLocation(locationRule)

    override suspend fun deleteLocationRule(locationRule: LocationRule) = ruleDao.deleteLocation(locationRule)

    override suspend fun updateLocationRule(locationRule: LocationRule) = ruleDao.updateLocation(locationRule)

}