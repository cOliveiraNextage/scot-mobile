package com.tracker.scotmobile.data.model

// Modelo para código de resultado
data class ResultCode(
    val fnResultCodeId: Long,
    val fcResultCodeNm: String,
    val fcResultCodeDs: String,
    val fnResultCodeSt: Boolean,
    val fcWarehouseTypeNameList: String? = null
)

// Modelo para código de resultado por tipo de armazém
data class ResultCodeWarehouseType(
    val fnResultCodeWarehouseTypeId: Long,
    val fnResultCodeId: Long?,
    val scResultCode: ResultCode?,
    val fcWarehouseTypeNameList: String
)

// Resposta da API de códigos de resultado por tipo de armazém
data class ResultCodeWarehouseTypeResponse(
    val scResultCodeWarehouseTypeDtos: List<ResultCodeWarehouseType>
)
