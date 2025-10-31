package com.tracker.scotmobile.data.repository

import com.tracker.scotmobile.data.api.TokenExpiredEvent
import com.tracker.scotmobile.data.api.RetrofitClient
import com.tracker.scotmobile.data.model.Service
import com.tracker.scotmobile.data.model.ServiceOrder
import com.tracker.scotmobile.data.model.SyncResponse
import com.tracker.scotmobile.data.model.ResultCode
import com.tracker.scotmobile.data.model.ResultCodeWarehouseType
import com.tracker.scotmobile.data.model.ResultCodeWarehouseTypeResponse
import com.tracker.scotmobile.data.model.OrderServiceResponse
import com.tracker.scotmobile.data.local.repository.ResultCodeLocalRepository
import com.tracker.scotmobile.data.local.repository.OrderServiceLocalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class SyncRepository(
    private val resultCodeLocalRepository: ResultCodeLocalRepository,
    private val orderServiceLocalRepository: OrderServiceLocalRepository
) {
    private val syncApi = RetrofitClient.syncApi
    
    /**
     * Sincroniza todos os dados de serviços
     */
    suspend fun syncServices(token: String, lastSync: Long? = null): Result<OrderServiceResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = syncApi.syncServices("Bearer $token", lastSync)
                
                // Salvar no banco local se a resposta for bem-sucedida
                try {
                    orderServiceLocalRepository.saveOrderServiceResponse(response)
                } catch (dbException: Exception) {
                    // Log do erro de banco, mas não falha a operação
                    println("Erro ao salvar no banco local: ${dbException.message}")
                }
                
                Result.success(response)
            } catch (e: HttpException) {
                // Verificar se é erro 401 (Unauthorized)
                if (e.code() == 401) {
                    TokenExpiredEvent.notifyTokenExpired()
                }
                Result.failure(e)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Busca apenas serviços
     */
    suspend fun getServices(token: String): Result<OrderServiceResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = syncApi.syncServices("Bearer $token")
                
                // Salvar no banco local se a resposta for bem-sucedida
                try {
                    orderServiceLocalRepository.saveOrderServiceResponse(response)
                } catch (dbException: Exception) {
                    // Log do erro de banco, mas não falha a operação
                    println("Erro ao salvar no banco local: ${dbException.message}")
                }
                
                Result.success(response)
            } catch (e: HttpException) {
                // Verificar se é erro 401 (Unauthorized)
                if (e.code() == 401) {
                    TokenExpiredEvent.notifyTokenExpired()
                }
                Result.failure(e)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Busca ordens de serviço
     */
    suspend fun getServiceOrders(
        token: String, 
        status: String? = null, 
        assignedTo: Long? = null
    ): Result<OrderServiceResponse> {
        return withContext(Dispatchers.IO) {
            try {
                // Por enquanto usa o mesmo endpoint, mas pode ser atualizado depois
                val response = syncApi.syncServices("Bearer $token")
                
                // Salvar no banco local se a resposta for bem-sucedida
                try {
                    orderServiceLocalRepository.saveOrderServiceResponse(response)
                } catch (dbException: Exception) {
                    // Log do erro de banco, mas não falha a operação
                    println("Erro ao salvar no banco local: ${dbException.message}")
                }
                
                Result.success(response)
            } catch (e: HttpException) {
                // Verificar se é erro 401 (Unauthorized)
                if (e.code() == 401) {
                    TokenExpiredEvent.notifyTokenExpired()
                }
                Result.failure(e)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Busca códigos de resultado por tipo de armazém
     */
    suspend fun getResultCodeWarehouseTypes(token: String): Result<ResultCodeWarehouseTypeResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = syncApi.getResultCodeWarehouseTypes("Bearer $token")
                
                // Salvar no banco local se a resposta for bem-sucedida
                if (response.scResultCodeWarehouseTypeDtos.isNotEmpty()) {
                    try {
                        resultCodeLocalRepository.saveResultCodeWarehouseTypesFromApi(
                            response.scResultCodeWarehouseTypeDtos
                        )
                    } catch (dbException: Exception) {
                        // Log do erro de banco, mas não falha a operação
                        println("Erro ao salvar no banco local: ${dbException.message}")
                    }
                }
                
                Result.success(response)
            } catch (e: HttpException) {
                // Verificar se é erro 401 (Unauthorized)
                if (e.code() == 401) {
                    TokenExpiredEvent.notifyTokenExpired()
                }
                Result.failure(e)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
