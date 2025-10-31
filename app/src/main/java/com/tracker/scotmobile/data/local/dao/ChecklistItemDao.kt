package com.tracker.scotmobile.data.local.dao

import androidx.room.*
import com.tracker.scotmobile.data.local.entity.ChecklistItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChecklistItemDao {
    @Query("SELECT * FROM checklist_items")
    fun getAllChecklistItems(): Flow<List<ChecklistItemEntity>>
    
    @Query("SELECT * FROM checklist_items WHERE fnChecklistItemId = :itemId")
    suspend fun getChecklistItemById(itemId: Long): ChecklistItemEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistItem(item: ChecklistItemEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistItems(items: List<ChecklistItemEntity>)
    
    @Delete
    suspend fun deleteChecklistItem(item: ChecklistItemEntity)
    
    @Query("DELETE FROM checklist_items")
    suspend fun deleteAllChecklistItems()
}

