package com.tracker.scotmobile.data.local.mapper

import com.tracker.scotmobile.data.local.entity.ResultCodeEntity
import com.tracker.scotmobile.data.local.entity.ResultCodeWarehouseTypeEntity
import com.tracker.scotmobile.data.model.ResultCode
import com.tracker.scotmobile.data.model.ResultCodeWarehouseType

object ResultCodeLocalMapper {
    
    fun ResultCode.toEntity(): ResultCodeEntity {
        return ResultCodeEntity(
            fnResultCodeId = this.fnResultCodeId,
            fcResultCodeNm = this.fcResultCodeNm,
            fcResultCodeDs = this.fcResultCodeDs,
            fnResultCodeSt = this.fnResultCodeSt,
            fcWarehouseTypeNameList = this.fcWarehouseTypeNameList
        )
    }
    
    fun ResultCodeEntity.toModel(): ResultCode {
        return ResultCode(
            fnResultCodeId = this.fnResultCodeId,
            fcResultCodeNm = this.fcResultCodeNm,
            fcResultCodeDs = this.fcResultCodeDs,
            fnResultCodeSt = this.fnResultCodeSt,
            fcWarehouseTypeNameList = this.fcWarehouseTypeNameList
        )
    }
    
    fun ResultCodeWarehouseType.toEntity(): ResultCodeWarehouseTypeEntity? {
        return if (this.scResultCode != null) {
            ResultCodeWarehouseTypeEntity(
                fnResultCodeWarehouseTypeId = this.fnResultCodeWarehouseTypeId,
                fnResultCodeId = this.fnResultCodeId,
                fcWarehouseTypeNameList = this.fcWarehouseTypeNameList
            )
        } else {
            null
        }
    }
    
    fun ResultCodeWarehouseTypeEntity.toModel(resultCode: ResultCode): ResultCodeWarehouseType {
        return ResultCodeWarehouseType(
            fnResultCodeWarehouseTypeId = this.fnResultCodeWarehouseTypeId,
            fnResultCodeId = this.fnResultCodeId,
            scResultCode = resultCode,
            fcWarehouseTypeNameList = this.fcWarehouseTypeNameList
        )
    }
}
