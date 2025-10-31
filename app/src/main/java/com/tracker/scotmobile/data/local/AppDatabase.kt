package com.tracker.scotmobile.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tracker.scotmobile.data.local.dao.*
import com.tracker.scotmobile.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        ResultCodeEntity::class,
        ResultCodeWarehouseTypeEntity::class,
        OrderServiceEntity::class,
        OwnerEntity::class,
        PhoneEntity::class,
        AddressEntity::class,
        VehicleEntity::class,
        ProductEntity::class,
        EquipmentEntity::class,
        CompatibilityEquipmentEntity::class,
        AccessoryEntity::class,
        TaskEntity::class,
        VehicleColorEntity::class,
        VehicleTypeEntity::class,
        ChecklistTypeEntity::class,
        ChecklistItemEntity::class,
        ChecklistTypeItemEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun resultCodeDao(): ResultCodeDao
    abstract fun resultCodeWarehouseTypeDao(): ResultCodeWarehouseTypeDao
    abstract fun orderServiceDao(): OrderServiceDao
    abstract fun ownerDao(): OwnerDao
    abstract fun addressDao(): AddressDao
    abstract fun phoneDao(): PhoneDao
    abstract fun vehicleDao(): VehicleDao
    abstract fun productDao(): ProductDao
    abstract fun equipmentDao(): EquipmentDao
    abstract fun compatibilityEquipmentDao(): CompatibilityEquipmentDao
    abstract fun accessoryDao(): AccessoryDao
    abstract fun taskDao(): TaskDao
    abstract fun vehicleColorDao(): VehicleColorDao
    abstract fun vehicleTypeDao(): VehicleTypeDao
    abstract fun checklistTypeDao(): ChecklistTypeDao
    abstract fun checklistItemDao(): ChecklistItemDao
    abstract fun checklistTypeItemDao(): ChecklistTypeItemDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "scotmobile_database"
                )
                .fallbackToDestructiveMigration(true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
