package com.tracker.scotmobile.data.model

// Response model para o endpoint /order
data class OrderServiceResponse(
    val orderService: List<OrderService>,
    val scTaskDTOList: List<Task>,
    val scVehicleColorDtoList: List<VehicleColor>,
    val scVehicleTypeDTOList: List<VehicleType>,
    val checklistDTO: ChecklistDTO
)

// Modelo de Ordem de Serviço
data class OrderService(
    val fnServiceOrderId: Long,
    val fnServiceTypeId: Long,
    val fcWarehouseNm: String,
    val fcWarehouseType: String,
    val fdSchedule: String,
    val fcVehicleContractProductId: String,
    val fcCostumer: String,
    val isInspection: Boolean,
    val scOwner: Owner?
)

// Modelo de Proprietário
data class Owner(
    val fcOwnerId: String,
    val fcDocument: String,
    val fcNumberDocument: String,
    val fcFullName: String,
    val scPhone: List<Phone>? = null,
    val scAddress: Address? = null,
    val scVehicle: Vehicle? = null,
    val scProduct: Product? = null,
    val scAccessory: List<Accessory>? = null
)

// Modelo de Telefone
data class Phone(
    val fcDDD: String,
    val fcPhone: String,
    val fcPhoneType: String,
    val fcExtensionLine: String? = null
)

// Modelo de Endereço
data class Address(
    val fcZipCode: String,
    val fcAddress: String,
    val fcNumber: String,
    val fcDistrict: String,
    val fcComplement: String? = null,
    val fcCity: String,
    val fcState: String,
    val fcLatitude: String? = null,
    val fcLongitude: String? = null
)

// Modelo de Veículo
data class Vehicle(
    val fcVehicleId: String,
    val fnVehicleColorId: Long,
    val fnVehicleTypeId: Long,
    val fcBrand: String,
    val fcModel: String,
    val fnYear: Int,
    val fcPlate: String,
    val fcVin: String,
    val fcFipe: String? = null
)

// Modelo de Produto
data class Product(
    val productId: String,
    val productName: String,
    val equipment: List<Equipment>? = null,
    val compatibilityEquipment: List<CompatibilityEquipment>? = null
)

// Modelo de Equipamento
data class Equipment(
    val nameEquipment: String,
    val typeEquipment: String,
    val fcAccessory: String? = null,
    val serial: String? = null,
    val locationInstalled: String? = null,
    val fcCompatibility: String? = null
)

// Modelo de Equipamento Compatível
data class CompatibilityEquipment(
    val fcAccessory: String,
    val fcCompatibility: String
)

// Modelo de Acessório
data class Accessory(
    val fcAccessoryName: String,
    val fcAccessoryProtheusId: String
)

// Modelo de Tarefa
data class Task(
    val fnTaskId: Long,
    val fcTaskNm: String,
    val fcTaskDs: String,
    val fdTaskIniDt: Long? = null,
    val fdTaskEndDt: Long? = null
)

// Modelo de Cor de Veículo
data class VehicleColor(
    val fnVehicleColorId: Long,
    val fcVehicleColorNm: String,
    val fnVehicleColorSt: Int,
    val scRequisitions: Any? = null
)

// Modelo de Tipo de Veículo
data class VehicleType(
    val fnVehicleTypeId: Long,
    val fcVehicleTypeNm: String,
    val fcVehicleTypeDs: String,
    val fcTypeUnion: String? = null
)

// Modelo de Checklist
data class ChecklistDTO(
    val scServiceCertificateChecklistType: List<ChecklistType>,
    val scServiceCertificateChecklistItems: List<ChecklistItem>,
    val scServiceCertificateChecklistTypeItems: List<ChecklistTypeItem>
)

// Modelo de Tipo de Checklist
data class ChecklistType(
    val fnChecklistTypeId: Long,
    val fcName: String
)

// Modelo de Item de Checklist
data class ChecklistItem(
    val fnChecklistItemId: Long,
    val fcName: String,
    val fcDescription: String
)

// Modelo de Relação Tipo-Item de Checklist
data class ChecklistTypeItem(
    val fnChecklistTypeItemId: Long,
    val fnChecklistType: ChecklistType,
    val fnChecklistItem: ChecklistItem
)

