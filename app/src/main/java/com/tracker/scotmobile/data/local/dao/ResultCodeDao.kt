package com.tracker.scotmobile.data.local.dao

import androidx.room.*
import com.tracker.scotmobile.data.local.entity.ResultCodeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ResultCodeDao {
    
    @Query("SELECT * FROM result_codes")
    fun getAllResultCodes(): Flow<List<ResultCodeEntity>>
    
    @Query("SELECT * FROM result_codes WHERE fnResultCodeSt = 1")
    fun getActiveResultCodes(): Flow<List<ResultCodeEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResultCode(resultCode: ResultCodeEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResultCodes(resultCodes: List<ResultCodeEntity>)
    
    @Delete
    suspend fun deleteResultCode(resultCode: ResultCodeEntity)
    
    @Query("DELETE FROM result_codes")
    suspend fun deleteAllResultCodes()
    
    @Query("SELECT COUNT(*) FROM result_codes")
    suspend fun getResultCodesCount(): Int
}
