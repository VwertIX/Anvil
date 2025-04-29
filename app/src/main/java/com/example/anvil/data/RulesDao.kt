package com.example.anvil.data



import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface RulesDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertApp(appRule: AppRule)

    @Update
    suspend fun updateApp(appRule: AppRule)

    @Delete
    suspend fun deleteApp(appRule: AppRule)


    @Query("SELECT * from AppRules WHERE id = :id")
    fun getAppRuleById(id: Int): Flow<AppRule>


    @Query("SELECT * from AppRules WHERE appName = :appName")
    fun getAppRuleByAppName(appName: String): Flow<AppRule>


    @Query("SELECT * from AppRules WHERE packageName = :packageName")
    fun getAppRuleByPackageName(packageName: String): Flow<AppRule>


    @Query("SELECT * from AppRules ORDER BY packageName ASC")
    fun getAllAppRules(): Flow<List<AppRule>>

    @Query("DELETE from AppRules")
    suspend fun clearAppRules()





    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLocation(locationRule: LocationRule)

    @Update
    suspend fun updateLocation(locationRule: LocationRule)

    @Delete
    suspend fun deleteLocation(locationRule: LocationRule)

    @Query("SELECT * from LocationRules WHERE locationName = :locationName")
    fun getLocationRuleByLocationName(locationName: String): Flow<LocationRule>


    @Query("SELECT * from LocationRules ORDER BY locationName ASC")
    fun getAllLocationRules(): Flow<List<LocationRule>>

    @Query("DELETE from LocationRules")
    suspend fun clearLocationRules()







}
