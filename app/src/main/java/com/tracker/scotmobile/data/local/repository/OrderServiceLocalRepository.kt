package com.tracker.scotmobile.data.local.repository

import com.tracker.scotmobile.data.local.dao.*
import com.tracker.scotmobile.data.local.entity.*
import com.tracker.scotmobile.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderServiceLocalRepository(
    private val orderServiceDao: OrderServiceDao,
    private val ownerDao: OwnerDao,
    private val addressDao: AddressDao,
    private val phoneDao: PhoneDao,
    private val vehicleDao: VehicleDao,
    private val productDao: ProductDao,
    private val equipmentDao: EquipmentDao,
    private val compatibilityEquipmentDao: CompatibilityEquipmentDao,
    private val accessoryDao: AccessoryDao,
    private val taskDao: TaskDao,
    private val vehicleColorDao: VehicleColorDao,
    private val vehicleTypeDao: VehicleTypeDao,
    private val checklistTypeDao: ChecklistTypeDao,
    private val checklistItemDao: ChecklistItemDao,
    private val checklistTypeItemDao: ChecklistTypeItemDao
) {
    
    /**
     * Salva todos os dados da resposta OrderServiceResponse no banco local
     */
    suspend fun saveOrderServiceResponse(response: OrderServiceResponse) = withContext(Dispatchers.IO) {
        try {
            // Salvar ordens de serviço
            response.orderService.forEach { orderService ->
                // IMPORTANTE: Inserir OrderService PRIMEIRO (para satisfazer foreign keys)
                orderServiceDao.insertOrderService(
                    OrderServiceEntity(
                        fnServiceOrderId = orderService.fnServiceOrderId,
                        fnServiceTypeId = orderService.fnServiceTypeId,
                        fcWarehouseNm = orderService.fcWarehouseNm,
                        fcWarehouseType = orderService.fcWarehouseType,
                        fdSchedule = orderService.fdSchedule,
                        fcVehicleContractProductId = orderService.fcVehicleContractProductId,
                        fcCostumer = orderService.fcCostumer,
                        isInspection = orderService.isInspection,
                        fcOwnerId = orderService.scOwner?.fcOwnerId
                    )
                )
                
                // Agora inserir dados relacionados (após OrderService existir)
                orderService.scOwner?.let { owner ->
                    // Salvar proprietário
                    ownerDao.insertOwner(
                        OwnerEntity(
                            fcOwnerId = owner.fcOwnerId,
                            fnServiceOrderId = orderService.fnServiceOrderId,
                            fcDocument = owner.fcDocument,
                            fcNumberDocument = owner.fcNumberDocument,
                            fcFullName = owner.fcFullName
                        )
                    )

                    // Salvar endereço
                    owner.scAddress?.let { addr ->
                        addressDao.insertAddress(
                            AddressEntity(
                                fcOwnerId = owner.fcOwnerId,
                                fnServiceOrderId = orderService.fnServiceOrderId,
                                fcZipCode = addr.fcZipCode,
                                fcAddress = addr.fcAddress,
                                fcNumber = addr.fcNumber,
                                fcDistrict = addr.fcDistrict,
                                fcComplement = addr.fcComplement,
                                fcCity = addr.fcCity,
                                fcState = addr.fcState,
                                fcLatitude = addr.fcLatitude,
                                fcLongitude = addr.fcLongitude
                            )
                        )
                    }

                    // Salvar telefones
                    owner.scPhone?.let { phones ->
                        val phoneEntities = phones.map { p ->
                            PhoneEntity(
                                fcOwnerId = owner.fcOwnerId,
                                fnServiceOrderId = orderService.fnServiceOrderId,
                                fcDDD = p.fcDDD,
                                fcPhone = p.fcPhone,
                                fcPhoneType = p.fcPhoneType,
                                fcExtensionLine = p.fcExtensionLine
                            )
                        }
                        if (phoneEntities.isNotEmpty()) phoneDao.insertPhones(phoneEntities)
                    }

                    // Salvar veículo
                    owner.scVehicle?.let { veh ->
                        vehicleDao.insertVehicle(
                            VehicleEntity(
                                fcVehicleId = veh.fcVehicleId,
                                fcOwnerId = owner.fcOwnerId,
                                fnServiceOrderId = orderService.fnServiceOrderId,
                                fnVehicleColorId = veh.fnVehicleColorId,
                                fnVehicleTypeId = veh.fnVehicleTypeId,
                                fcBrand = veh.fcBrand,
                                fcModel = veh.fcModel,
                                fnYear = veh.fnYear,
                                fcPlate = veh.fcPlate,
                                fcVin = veh.fcVin,
                                fcFipe = veh.fcFipe
                            )
                        )
                    }

                    // Salvar produto e listas relacionadas
                    owner.scProduct?.let { prod ->
                        try {
                            productDao.insertProduct(
                                ProductEntity(
                                    fnServiceOrderId = orderService.fnServiceOrderId,
                                    fcOwnerId = owner.fcOwnerId,
                                    productId = prod.productId,
                                    productName = prod.productName
                                )
                            )
                            println("✓ Produto salvo: OS ${orderService.fnServiceOrderId}, Owner: ${owner.fcOwnerId}, Produto: ${prod.productName}")
                        } catch (e: Exception) {
                            println("✗ Erro ao salvar produto OS ${orderService.fnServiceOrderId}: ${e.message}")
                            e.printStackTrace()
                        }

                        prod.equipment?.let { eqList ->
                            try {
                                val entities = eqList.map { eq ->
                                    EquipmentEntity(
                                        fcOwnerId = owner.fcOwnerId,
                                        fnServiceOrderId = orderService.fnServiceOrderId,
                                        nameEquipment = eq.nameEquipment,
                                        typeEquipment = eq.typeEquipment,
                                        fcAccessory = eq.fcAccessory,
                                        serial = eq.serial,
                                        locationInstalled = eq.locationInstalled,
                                        fcCompatibility = eq.fcCompatibility
                                    )
                                }
                                if (entities.isNotEmpty()) {
                                    equipmentDao.insertEquipment(entities)
                                    println("✓ ${entities.size} equipamento(s) salvo(s) para OS ${orderService.fnServiceOrderId}")
                                }
                            } catch (e: Exception) {
                                println("✗ Erro ao salvar equipamentos OS ${orderService.fnServiceOrderId}: ${e.message}")
                                e.printStackTrace()
                            }
                        }

                        prod.compatibilityEquipment?.let { compList ->
                            val entities = compList.map { ce ->
                                CompatibilityEquipmentEntity(
                                    fcOwnerId = owner.fcOwnerId,
                                    fnServiceOrderId = orderService.fnServiceOrderId,
                                    fcAccessory = ce.fcAccessory,
                                    fcCompatibility = ce.fcCompatibility
                                )
                            }
                            if (entities.isNotEmpty()) compatibilityEquipmentDao.insertCompatibility(entities)
                        }
                    }

                    // Salvar acessórios
                    owner.scAccessory?.let { accList ->
                        val entities = accList.map { a ->
                            AccessoryEntity(
                                fcOwnerId = owner.fcOwnerId,
                                fnServiceOrderId = orderService.fnServiceOrderId,
                                fcAccessoryName = a.fcAccessoryName,
                                fcAccessoryProtheusId = a.fcAccessoryProtheusId
                            )
                        }
                        if (entities.isNotEmpty()) accessoryDao.insertAccessories(entities)
                    }
                }
            }
            
            // Salvar tarefas
            if (response.scTaskDTOList.isNotEmpty()) {
                val taskEntities = response.scTaskDTOList.map { task ->
                    TaskEntity(
                        fnTaskId = task.fnTaskId,
                        fcTaskNm = task.fcTaskNm,
                        fcTaskDs = task.fcTaskDs,
                        fdTaskIniDt = task.fdTaskIniDt,
                        fdTaskEndDt = task.fdTaskEndDt
                    )
                }
                taskDao.insertTasks(taskEntities)
            }
            
            // Salvar cores de veículos
            if (response.scVehicleColorDtoList.isNotEmpty()) {
                val vehicleColorEntities = response.scVehicleColorDtoList.map { color ->
                    VehicleColorEntity(
                        fnVehicleColorId = color.fnVehicleColorId,
                        fcVehicleColorNm = color.fcVehicleColorNm,
                        fnVehicleColorSt = color.fnVehicleColorSt
                    )
                }
                vehicleColorDao.insertVehicleColors(vehicleColorEntities)
            }
            
            // Salvar tipos de veículos
            if (response.scVehicleTypeDTOList.isNotEmpty()) {
                val vehicleTypeEntities = response.scVehicleTypeDTOList.map { type ->
                    VehicleTypeEntity(
                        fnVehicleTypeId = type.fnVehicleTypeId,
                        fcVehicleTypeNm = type.fcVehicleTypeNm,
                        fcVehicleTypeDs = type.fcVehicleTypeDs,
                        fcTypeUnion = type.fcTypeUnion
                    )
                }
                vehicleTypeDao.insertVehicleTypes(vehicleTypeEntities)
            }
            
            // Salvar dados do checklist
            val checklist = response.checklistDTO
            // Salvar tipos de checklist
            if (checklist.scServiceCertificateChecklistType.isNotEmpty()) {
                val checklistTypeEntities = checklist.scServiceCertificateChecklistType.map { type ->
                    ChecklistTypeEntity(
                        fnChecklistTypeId = type.fnChecklistTypeId,
                        fcName = type.fcName
                    )
                }
                checklistTypeDao.insertChecklistTypes(checklistTypeEntities)
            }
            
            // Salvar itens de checklist
            if (checklist.scServiceCertificateChecklistItems.isNotEmpty()) {
                val checklistItemEntities = checklist.scServiceCertificateChecklistItems.map { item ->
                    ChecklistItemEntity(
                        fnChecklistItemId = item.fnChecklistItemId,
                        fcName = item.fcName,
                        fcDescription = item.fcDescription
                    )
                }
                checklistItemDao.insertChecklistItems(checklistItemEntities)
            }
            
            // Salvar relações tipo-item de checklist
            if (checklist.scServiceCertificateChecklistTypeItems.isNotEmpty()) {
                val checklistTypeItemEntities = checklist.scServiceCertificateChecklistTypeItems.map { typeItem ->
                    ChecklistTypeItemEntity(
                        fnChecklistTypeItemId = typeItem.fnChecklistTypeItemId,
                        fnChecklistTypeId = typeItem.fnChecklistType.fnChecklistTypeId,
                        fnChecklistItemId = typeItem.fnChecklistItem.fnChecklistItemId
                    )
                }
                checklistTypeItemDao.insertChecklistTypeItems(checklistTypeItemEntities)
            }
            
        } catch (e: Exception) {
            println("Erro ao salvar dados de ordem de serviço: ${e.message}")
            throw e
        }
    }
    
    /**
     * Busca todas as ordens de serviço do banco local
     */
    fun getAllOrderServices() = orderServiceDao.getAllOrderServices()
    
    /**
     * Busca uma ordem de serviço específica
     */
    suspend fun getOrderServiceById(orderId: Long): OrderServiceEntity? {
        return withContext(Dispatchers.IO) {
            orderServiceDao.getOrderServiceById(orderId)
        }
    }
    
    /**
     * Busca produto de uma ordem de serviço
     */
    suspend fun getProductByOrderId(orderId: Long): ProductEntity? {
        return withContext(Dispatchers.IO) {
            productDao.getFirstProductByOrderId(orderId)
        }
    }
    
    /**
     * Busca task por ID (para obter tipo de serviço)
     */
    suspend fun getTaskById(taskId: Long): TaskEntity? {
        return withContext(Dispatchers.IO) {
            taskDao.getTaskById(taskId)
        }
    }
    
    /**
     * Busca todos os produtos de uma ordem
     */
    fun getProductsByOrderId(orderId: Long) = productDao.getProductsByOrderId(orderId)
    
    /**
     * Busca endereço de uma ordem de serviço
     */
    suspend fun getAddressByOrderId(orderId: Long): AddressEntity? {
        return withContext(Dispatchers.IO) {
            addressDao.getFirstAddressByOrderId(orderId)
        }
    }
    
    /**
     * Limpa todos os dados de ordem de serviço do banco local
     */
    suspend fun clearAllOrderServiceData() = withContext(Dispatchers.IO) {
        orderServiceDao.deleteAllOrderServices()
        ownerDao.deleteAllOwners()
        addressDao.deleteAddressesByOrderId(-1) // noop placeholder
        phoneDao.deletePhonesByOrderId(-1) // noop placeholder
        productDao.deleteProductsByOrderId(-1)
        equipmentDao.deleteEquipmentByOrderId(-1)
        compatibilityEquipmentDao.deleteCompatibilityByOrderId(-1)
        accessoryDao.deleteAccessoriesByOrderId(-1)
        taskDao.deleteAllTasks()
        vehicleColorDao.deleteAllVehicleColors()
        vehicleTypeDao.deleteAllVehicleTypes()
        checklistTypeDao.deleteAllChecklistTypes()
        checklistItemDao.deleteAllChecklistItems()
        checklistTypeItemDao.deleteAllChecklistTypeItems()
    }
}

