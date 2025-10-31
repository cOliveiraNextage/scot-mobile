package com.tracker.scotmobile.data.local.repository

import com.tracker.scotmobile.data.local.dao.ResultCodeDao
import com.tracker.scotmobile.data.local.dao.ResultCodeWarehouseTypeDao
import com.tracker.scotmobile.data.local.entity.ResultCodeEntity
import com.tracker.scotmobile.data.local.entity.ResultCodeWarehouseTypeEntity
import com.tracker.scotmobile.data.local.mapper.ResultCodeLocalMapper.toEntity
import com.tracker.scotmobile.data.local.mapper.ResultCodeLocalMapper.toModel
import com.tracker.scotmobile.data.model.ResultCode
import com.tracker.scotmobile.data.model.ResultCodeWarehouseType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ResultCodeLocalRepository(
    private val resultCodeDao: ResultCodeDao,
    private val resultCodeWarehouseTypeDao: ResultCodeWarehouseTypeDao
) {
    
    // Result Codes
    fun getAllResultCodes(): Flow<List<ResultCode>> {
        return resultCodeDao.getAllResultCodes().map { entities ->
            entities.map { it.toModel() }
        }
    }
    
    fun getActiveResultCodes(): Flow<List<ResultCode>> {
        return resultCodeDao.getActiveResultCodes().map { entities ->
            entities.map { it.toModel() }
        }
    }
    
    suspend fun insertResultCode(resultCode: ResultCode) {
        resultCodeDao.insertResultCode(resultCode.toEntity())
    }
    
    suspend fun insertResultCodes(resultCodes: List<ResultCode>) {
        resultCodeDao.insertResultCodes(resultCodes.map { it.toEntity() })
    }
    
    suspend fun deleteAllResultCodes() {
        resultCodeDao.deleteAllResultCodes()
    }
    
    suspend fun getResultCodesCount(): Int {
        return resultCodeDao.getResultCodesCount()
    }
    
    // Result Code Warehouse Types
    fun getAllResultCodeWarehouseTypes(): Flow<List<ResultCodeWarehouseTypeEntity>> {
        return resultCodeWarehouseTypeDao.getAllResultCodeWarehouseTypes()
    }
    
    suspend fun insertResultCodeWarehouseType(resultCodeWarehouseType: ResultCodeWarehouseType) {
        resultCodeWarehouseType.toEntity()?.let { entity ->
            resultCodeWarehouseTypeDao.insertResultCodeWarehouseType(entity)
        }
    }
    
    suspend fun insertResultCodeWarehouseTypes(resultCodeWarehouseTypes: List<ResultCodeWarehouseType>) {
        val entities = resultCodeWarehouseTypes.mapNotNull { it.toEntity() }
        if (entities.isNotEmpty()) {
            resultCodeWarehouseTypeDao.insertResultCodeWarehouseTypes(entities)
        }
    }
    
    suspend fun deleteAllResultCodeWarehouseTypes() {
        resultCodeWarehouseTypeDao.deleteAllResultCodeWarehouseTypes()
    }
    
    suspend fun getResultCodeWarehouseTypesCount(): Int {
        return resultCodeWarehouseTypeDao.getResultCodeWarehouseTypesCount()
    }
    
    // Método para salvar dados completos da API
    suspend fun saveResultCodeWarehouseTypesFromApi(resultCodeWarehouseTypes: List<ResultCodeWarehouseType>) {
        try {
            // Filtrar apenas os que têm scResultCode não nulo
            val validResultCodeWarehouseTypes = resultCodeWarehouseTypes.filter { it.scResultCode != null }
            
            if (validResultCodeWarehouseTypes.isNotEmpty()) {
                // Criar ResultCode com fcWarehouseTypeNameList incluído
                val resultCodes = validResultCodeWarehouseTypes
                    .map { warehouseType ->
                        warehouseType.scResultCode!!.copy(
                            fcWarehouseTypeNameList = warehouseType.fcWarehouseTypeNameList
                        )
                    }
                    .distinctBy { it.fnResultCodeId }
                
                // Salvar códigos de resultado
                insertResultCodes(resultCodes)
            }
        } catch (e: Exception) {
            println("Erro ao salvar códigos de resultado: ${e.message}")
            throw e
        }
    }
}
