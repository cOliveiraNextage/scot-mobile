package com.tracker.scotmobile.data.model

// Resposta da API de sincronização
data class SyncResponse(
    val success: Boolean,
    val message: String,
    val data: SyncData? = null
)

data class SyncData(
    val services: List<Service>? = null,
    val serviceOrders: List<ServiceOrder>? = null,
    val lastSync: Long = System.currentTimeMillis()
)

// Modelo de Serviço
data class Service(
    val id: Long,
    val name: String,
    val description: String?,
    val status: ServiceStatus,
    val priority: Priority,
    val assignedTo: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val location: String?,
    val estimatedDuration: Int? // em minutos
)

// Modelo de Ordem de Serviço
data class ServiceOrder(
    val id: Long,
    val orderNumber: String,
    val serviceId: Long,
    val serviceName: String,
    val status: OrderStatus,
    val priority: Priority,
    val assignedTo: String?,
    val assignedToId: Long?,
    val clientName: String?,
    val clientPhone: String?,
    val location: String?,
    val description: String?,
    val scheduledDate: Long?,
    val estimatedDuration: Int?, // em minutos
    val actualDuration: Int?, // em minutos
    val createdAt: Long,
    val updatedAt: Long,
    val completedAt: Long?,
    val notes: String?
)

// Enums
enum class ServiceStatus(val description: String) {
    ACTIVE("Ativo"),
    INACTIVE("Inativo"),
    MAINTENANCE("Em Manutenção"),
    SUSPENDED("Suspenso")
}

enum class OrderStatus(val description: String, val color: String) {
    PENDING("Pendente", "#FFA500"),
    IN_PROGRESS("Em Andamento", "#007BFF"),
    COMPLETED("Concluído", "#28A745"),
    CANCELLED("Cancelado", "#DC3545"),
    SCHEDULED("Agendado", "#6F42C1"),
    ON_HOLD("Em Espera", "#FFC107")
}

enum class Priority(val description: String, val color: String) {
    LOW("Baixa", "#28A745"),
    MEDIUM("Média", "#FFC107"),
    HIGH("Alta", "#FFA500"),
    URGENT("Urgente", "#DC3545")
}
