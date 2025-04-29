package com.example.anvil.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton




@Database(entities = [AppRule::class, LocationRule::class], version = 1)
abstract class RulesDatabase : RoomDatabase() {
    abstract fun ruleDao(): RulesDao

}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): RulesDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            RulesDatabase::class.java,
            "rule_database"
        ).build()

    }

    @Singleton
    @Provides
    fun getRuleDao(db: RulesDatabase): RulesDao {
        return db.ruleDao()
    }
}
