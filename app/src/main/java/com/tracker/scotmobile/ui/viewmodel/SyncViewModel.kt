package com.tracker.scotmobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracker.scotmobile.data.model.Service
import com.tracker.scotmobile.data.model.ServiceOrder
import com.tracker.scotmobile.data.model.ResultCode
import com.tracker.scotmobile.data.model.ResultCodeWarehouseType
import com.tracker.scotmobile.data.model.OrderService
import com.tracker.scotmobile.data.repository.SyncRepository
import com.tracker.scotmobile.data.local.repository.ResultCodeLocalRepository
import com.tracker.scotmobile.data.local.repository.OrderServiceLocalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SyncUiState(
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false,
    val services: List<Service> = emptyList(),
    val serviceOrders: List<ServiceOrder> = emptyList(),
    val orderServices: List<OrderService> = emptyList(),
    val resultCodes: List<ResultCode> = emptyList(),
    val lastSync: Long? = null,
    val errorMessage: String? = null,
    val syncSuccess: Boolean = false
)

class SyncViewModel(
    private val resultCodeLocalRepository: ResultCodeLocalRepository,
    private val orderServiceLocalRepository: OrderServiceLocalRepository
) : ViewModel() {
    private val repository = SyncRepository(resultCodeLocalRepository, orderServiceLocalRepository)
    
    private val _uiState = MutableStateFlow(SyncUiState())
    val uiState: StateFlow<SyncUiState> = _uiState.asStateFlow()
    
    /**
     * Sincroniza todos os dados de serviços
     */
    fun syncServices(token: String) {
        _uiState.value = _uiState.value.copy(
            isSyncing = true,
            errorMessage = null,
            syncSuccess = false
        )
        
        viewModelScope.launch {
            try {
                // Sincronizar serviços e ordens
                val syncResult = repository.syncServices(token, _uiState.value.lastSync)
                syncResult.fold(
                    onSuccess = { response ->
                        val orderServices = response.orderService
                        
                        // Carregar códigos de resultado
                        val resultCodesResult = repository.getResultCodeWarehouseTypes(token)
                        resultCodesResult.fold(
                            onSuccess = { resultCodesResponse ->
                                val resultCodes = resultCodesResponse.scResultCodeWarehouseTypeDtos
                                    .filterNotNull()
                                    .filter { it.scResultCode != null }
                                    .map { warehouseType ->
                                        warehouseType.scResultCode!!.copy(
                                            fcWarehouseTypeNameList = warehouseType.fcWarehouseTypeNameList
                                        )
                                    }
                                    .distinctBy { it.fnResultCodeId }
                                
                                _uiState.value = _uiState.value.copy(
                                    isSyncing = false,
                                    orderServices = orderServices,
                                    resultCodes = resultCodes,
                                    lastSync = System.currentTimeMillis(),
                                    syncSuccess = true
                                )
                            },
                            onFailure = { exception ->
                                _uiState.value = _uiState.value.copy(
                                    isSyncing = false,
                                    orderServices = orderServices,
                                    lastSync = System.currentTimeMillis(),
                                    syncSuccess = true,
                                    errorMessage = "Serviços sincronizados, mas erro ao carregar códigos: ${exception.message}"
                                )
                            }
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isSyncing = false,
                            errorMessage = exception.message ?: "Erro na sincronização"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSyncing = false,
                    errorMessage = e.message ?: "Erro desconhecido"
                )
            }
        }
    }
    
    /**
     * Busca apenas serviços
     */
    fun loadServices(token: String) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        
        viewModelScope.launch {
            try {
                val result = repository.getServices(token)
                result.fold(
                    onSuccess = { response ->
                        val orderServices = response.orderService
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            orderServices = orderServices
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Erro ao carregar serviços"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro desconhecido"
                )
            }
        }
    }
    
    /**
     * Busca ordens de serviço
     */
    fun loadServiceOrders(token: String, status: String? = null, assignedTo: Long? = null) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        
        viewModelScope.launch {
            try {
                val result = repository.getServiceOrders(token, status, assignedTo)
                result.fold(
                    onSuccess = { response ->
                        val orderServices = response.orderService
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            orderServices = orderServices
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Erro ao carregar ordens de serviço"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro desconhecido"
                )
            }
        }
    }
    
    /**
     * Limpa mensagens de erro
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    /**
     * Busca códigos de resultado
     */
    fun loadResultCodes(token: String) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        
        viewModelScope.launch {
            try {
                val result = repository.getResultCodeWarehouseTypes(token)
                result.fold(
                    onSuccess = { response ->
                        val resultCodes = response.scResultCodeWarehouseTypeDtos
                            .filterNotNull()
                            .filter { it.scResultCode != null }
                            .map { warehouseType ->
                                warehouseType.scResultCode!!.copy(
                                    fcWarehouseTypeNameList = warehouseType.fcWarehouseTypeNameList
                                )
                            }
                            .distinctBy { it.fnResultCodeId }
                        
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            resultCodes = resultCodes
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Erro ao carregar códigos de resultado"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro desconhecido"
                )
            }
        }
    }
    
    /**
     * Reseta o estado de sucesso
     */
    fun resetSyncSuccess() {
        _uiState.value = _uiState.value.copy(syncSuccess = false)
    }
}
