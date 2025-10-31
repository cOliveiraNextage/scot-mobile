package com.tracker.scotmobile.data.api

import com.tracker.scotmobile.data.model.SyncResponse
import com.tracker.scotmobile.data.model.OrderServiceResponse
import com.tracker.scotmobile.data.model.ResultCodeWarehouseTypeResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SyncApi {
    
    /**
     * Sincroniza dados de serviços e ordens de serviço
     */
    @GET("order")
    suspend fun syncServices(
        @Header("Authorization") token: String,
        @Query("lastSync") lastSync: Long? = null
    ): OrderServiceResponse
    
    /**
     * Busca apenas serviços
     */
    @GET("services")
    suspend fun getServices(
        @Header("Authorization") token: String
    ): SyncResponse
    
    /**
     * Busca apenas ordens de serviço
     */
    @GET("service-orders")
    suspend fun getServiceOrders(
        @Header("Authorization") token: String,
        @Query("status") status: String? = null,
        @Query("assignedTo") assignedTo: Long? = null
    ): SyncResponse
    
    /**
     * Busca todos os códigos de resultado por tipo de armazém
     */
    @GET("scotMobileOS/getAllResultCodeWarehouseTypeData")
    suspend fun getResultCodeWarehouseTypes(
        @Header("Authorization") token: String
    ): ResultCodeWarehouseTypeResponse
}
