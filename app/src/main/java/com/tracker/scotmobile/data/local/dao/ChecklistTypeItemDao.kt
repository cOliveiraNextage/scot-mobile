package com.tracker.scotmobile.data.local.dao

import androidx.room.*
import com.tracker.scotmobile.data.local.entity.ChecklistTypeItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChecklistTypeItemDao {
    @Query("SELECT * FROM checklist_type_items")
    fun getAllChecklistTypeItems(): Flow<List<ChecklistTypeItemEntity>>
    
    @Query("SELECT * FROM checklist_type_items WHERE fnChecklistTypeItemId = :id")
    suspend fun getChecklistTypeItemById(id: Long): ChecklistTypeItemEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistTypeItem(item: ChecklistTypeItemEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistTypeItems(items: List<ChecklistTypeItemEntity>)
    
    @Delete
    suspend fun deleteChecklistTypeItem(item: ChecklistTypeItemEntity)
    
    @Query("DELETE FROM checklist_type_items")
    suspend fun deleteAllChecklistTypeItems()
}

