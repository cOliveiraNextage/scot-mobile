package com.tracker.scotmobile.data.local.mapper

import com.tracker.scotmobile.data.local.entity.UserEntity
import com.tracker.scotmobile.data.model.Role
import com.tracker.scotmobile.data.model.User

object UserLocalMapper {
    
    /**
     * Converte User para UserEntity
     */
    fun userToEntity(user: User): UserEntity {
        return UserEntity(
            id = user.id,
            name = user.name,
            login = user.login,
            document = user.document,
            token = user.token,
            roleId = user.role?.id,
            roleDescription = user.role?.description,
            roleName = user.role?.name
        )
    }
    
    /**
     * Converte UserEntity para User
     */
    fun entityToUser(entity: UserEntity): User {
        return User(
            id = entity.id,
            name = entity.name,
            login = entity.login,
            document = entity.document,
            token = entity.token,
            role = if (entity.roleId != null) {
                Role(
                    id = entity.roleId,
                    description = entity.roleDescription ?: "",
                    name = entity.roleName ?: ""
                )
            } else null
        )
    }
}
