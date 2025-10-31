package com.tracker.scotmobile.data.local.dao

import androidx.room.*
import com.tracker.scotmobile.data.local.entity.ChecklistTypeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChecklistTypeDao {
    @Query("SELECT * FROM checklist_types")
    fun getAllChecklistTypes(): Flow<List<ChecklistTypeEntity>>
    
    @Query("SELECT * FROM checklist_types WHERE fnChecklistTypeId = :typeId")
    suspend fun getChecklistTypeById(typeId: Long): ChecklistTypeEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistType(type: ChecklistTypeEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistTypes(types: List<ChecklistTypeEntity>)
    
    @Delete
    suspend fun deleteChecklistType(type: ChecklistTypeEntity)
    
    @Query("DELETE FROM checklist_types")
    suspend fun deleteAllChecklistTypes()
}

